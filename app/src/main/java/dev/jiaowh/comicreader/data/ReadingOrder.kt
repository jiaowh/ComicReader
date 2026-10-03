package dev.jiaowh.comicreader.data

import dev.jiaowh.comicreader.data.db.ChapterEntity
import dev.jiaowh.comicreader.data.db.ComicEntity
import dev.jiaowh.comicreader.data.db.ReadingStepEntity

enum class StepStatus {
    /** Not matched to a BatCave comic yet. */
    Unlinked,

    /** Automatic matching found nothing; the user needs to pick the comic. */
    NotFound,

    /** The comic is linked but doesn't have these issues (yet). */
    IssueMissing,
    NotStarted,
    InProgress,
    Done,
}

data class StepState(
    val step: ReadingStepEntity,
    val comic: ComicEntity?,
    /** The chapters this step covers, in reading order. */
    val chapters: List<ChapterEntity>,
    val readCount: Int,
    /** First unread chapter in the step, and the page to resume at. */
    val resumeChapterId: Int?,
    val resumePage: Int,
    val status: StepStatus,
) {
    val isLinked get() = comic != null && chapters.isNotEmpty()

    /** Chapter and page to open: where the user left off, or the step's start if it's already read. */
    val openTarget: Pair<Int, Int>?
        get() = when {
            resumeChapterId != null -> resumeChapterId to resumePage
            chapters.isNotEmpty() -> chapters.first().id to 0
            else -> null
        }
}

data class ReadingOrderState(val seriesId: Long, val steps: List<StepState>) {
    val required get() = steps.filterNot { it.step.optional }
    val doneCount get() = required.count { it.status == StepStatus.Done }

    /** The first unfinished required step. */
    val nextUp: StepState? get() = steps.firstOrNull { it.isPending }

    fun stepContaining(url: String, chapterId: Int): StepState? =
        steps.firstOrNull { it.comic?.url == url && it.chapters.any { chapter -> chapter.id == chapterId } }

    /** The next unfinished required step after [stepId]. */
    fun stepAfter(stepId: Long): StepState? {
        val index = steps.indexOfFirst { it.step.id == stepId }
        if (index == -1) return null
        return steps.drop(index + 1).firstOrNull { it.isPending }
    }

    /**
     * Still to read and worth stopping at. Books that BatCave doesn't have (not found, or issues
     * missing) are skipped so one gap doesn't stall the chain; they stay flagged in the list.
     */
    private val StepState.isPending
        get() = !step.optional && status != StepStatus.Done &&
            status != StepStatus.NotFound && status != StepStatus.IssueMissing
}

/** Derives every step's status from the linked comics' reading progress. */
fun buildReadingOrder(
    seriesId: Long,
    steps: List<ReadingStepEntity>,
    comics: Map<String, ComicEntity>,
    chaptersByComic: Map<String, List<ChapterEntity>>,
): ReadingOrderState = ReadingOrderState(
    seriesId,
    steps.map { step ->
        val comic = step.comicUrl?.let(comics::get)
            ?: return@map StepState(
                step, null, emptyList(), 0, null, 0,
                if (step.unresolved) StepStatus.NotFound else StepStatus.Unlinked,
            )
        val all = chaptersByComic[comic.url].orEmpty()
        val mine = selectIssues(all, step.firstIssue, step.lastIssue) { it.number }
        if (mine.isEmpty()) return@map StepState(step, comic, emptyList(), 0, null, 0, StepStatus.IssueMissing)

        val currentSort = all.firstOrNull { it.id == comic.lastReadChapterId }?.sortIndex ?: -1
        fun isRead(chapter: ChapterEntity) =
            chapter.sortIndex < currentSort || (chapter.sortIndex == currentSort && comic.lastChapterFinished)

        val readCount = mine.count(::isRead)
        val resume = mine.firstOrNull { !isRead(it) }
        val resumingHere = resume != null && resume.id == comic.lastReadChapterId
        val status = when {
            resume == null -> StepStatus.Done
            readCount > 0 || resumingHere -> StepStatus.InProgress
            else -> StepStatus.NotStarted
        }
        StepState(step, comic, mine, readCount, resume?.id, if (resumingHere) comic.lastReadPage else 0, status)
    },
)

/**
 * Picks the chapters for an issue range from a comic's chapters (sorted oldest first).
 * Falls back to position (issue 1 = first chapter) if the site's numbering doesn't line up.
 */
fun <T> selectIssues(sorted: List<T>, first: Float?, last: Float?, number: (T) -> Float): List<T> {
    if (first == null && last == null) return sorted
    val low = first ?: Float.NEGATIVE_INFINITY
    val high = last ?: Float.POSITIVE_INFINITY
    val byNumber = sorted.filter { number(it) in low..high }
    if (byNumber.isNotEmpty()) return byNumber

    val from = (first?.toInt() ?: 1) - 1
    val to = (last?.toInt() ?: sorted.size).coerceAtMost(sorted.size)
    return if (from in sorted.indices && to > from) sorted.subList(from, to) else emptyList()
}

fun StepSeed.toEntity(seriesId: Long, position: Int) = ReadingStepEntity(
    seriesId = seriesId,
    position = position,
    section = section,
    label = label,
    title = title,
    year = year,
    firstIssue = first,
    lastIssue = last,
    optional = optional,
    note = note,
)

data class StepSyncPlan(
    val updates: List<ReadingStepEntity>,
    val inserts: List<ReadingStepEntity>,
    val deletes: List<Long>,
)

/**
 * Works out how to turn a stored order into [seeds]. Steps are matched on what they point at
 * (title, year, issue range), so a matched step keeps its id, link and "not found" state; only its
 * position and display fields are updated.
 */
fun planStepSync(seriesId: Long, current: List<ReadingStepEntity>, seeds: List<StepSeed>): StepSyncPlan {
    fun key(title: String, year: Int?, first: Float?, last: Float?) = "${TitleMatch.normalize(title)}|$year|$first|$last"

    val unmatched = current
        .groupBy { key(it.title, it.year, it.firstIssue, it.lastIssue) }
        .mapValues { (_, steps) -> steps.toMutableList() }
    val updates = mutableListOf<ReadingStepEntity>()
    val inserts = mutableListOf<ReadingStepEntity>()
    seeds.forEachIndexed { index, seed ->
        val existing = unmatched[key(seed.title, seed.year, seed.first, seed.last)]?.removeFirstOrNull()
        if (existing == null) {
            inserts += seed.toEntity(seriesId, index)
        } else {
            val wanted = existing.copy(
                position = index,
                section = seed.section,
                label = seed.label,
                optional = seed.optional,
                note = seed.note,
            )
            if (wanted != existing) updates += wanted
        }
    }
    return StepSyncPlan(updates, inserts, unmatched.values.flatten().map { it.id })
}

/** Loose title comparison for matching reading-order entries to BatCave listings. */
object TitleMatch {
    private val yearPattern = Regex("""\((\d{4})\)""")

    fun year(title: String): Int? = yearPattern.find(title)?.groupValues?.get(1)?.toIntOrNull()

    /**
     * "The Batman Who Laughs (2018)" and "Batman Who Laughs" both become "batman who laughs".
     * Dots and apostrophes are dropped rather than spaced, so "DC K.O." and "DC KO" match.
     */
    fun normalize(title: String): String =
        title.replace(yearPattern, " ")
            .lowercase()
            .replace("&", " and ")
            .replace(Regex("[.'’]"), "")
            .replace(Regex("[^a-z0-9]+"), " ")
            .trim()
            .removePrefix("the ")

    /** Search text without punctuation, which the site's search can trip over. */
    fun query(title: String): String =
        title.replace(Regex("[^A-Za-z0-9 ]+"), " ").replace(Regex("\\s+"), " ").trim()

    /** Search phrasings to try in turn, e.g. "DC K.O. Knightfight", "DC K O Knightfight", "DC KO Knightfight". */
    fun queries(title: String): List<String> = listOf(
        title.replace(Regex("[:/]"), " "),
        query(title),
        query(title.replace(Regex("[.'’]"), "")),
    ).map { it.replace(Regex("\\s+"), " ").trim() }.distinct()
}
