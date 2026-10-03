package dev.jiaowh.comicreader.data

import dev.jiaowh.comicreader.data.db.ChapterEntity
import dev.jiaowh.comicreader.data.db.ComicEntity
import dev.jiaowh.comicreader.data.db.ReadingStepEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReadingOrderTest {

    private val metal = "/metal.html"
    private val redDeath = "/red-death.html"

    private fun comic(url: String, chapterId: Int? = null, page: Int = 0, finished: Boolean = false) = ComicEntity(
        url = url, title = url, coverUrl = null, publisher = null, status = null, newsId = 1, addedAt = 0,
        lastReadChapterId = chapterId, lastReadPage = page, lastReadPageCount = 20, lastChapterFinished = finished,
    )

    /** Chapters with ids 100+n and issue numbers 1..count. */
    private fun chapters(url: String, count: Int) =
        (1..count).map { ChapterEntity(url, 100 + it, it.toFloat(), "#$it", "", it - 1) }

    private fun step(id: Long, url: String?, first: Int? = null, last: Int? = first, optional: Boolean = false) =
        ReadingStepEntity(
            id = id, seriesId = 1, position = id.toInt(), section = "", label = "step $id", title = "",
            year = null, firstIssue = first?.toFloat(), lastIssue = last?.toFloat(), optional = optional,
            note = null, comicUrl = url,
        )

    private val steps = listOf(
        step(1, metal, 1),
        step(2, metal, 2),
        step(3, redDeath),
        step(4, null, optional = true),
        step(5, metal, 3),
    )

    private fun order(metalComic: ComicEntity, redDeathComic: ComicEntity) = buildReadingOrder(
        seriesId = 1,
        steps = steps,
        comics = mapOf(metal to metalComic, redDeath to redDeathComic),
        chaptersByComic = mapOf(metal to chapters(metal, 6), redDeath to chapters(redDeath, 1)),
    )

    @Test
    fun `nothing read starts at the first step`() {
        val state = order(comic(metal), comic(redDeath))
        assertEquals(1L, state.nextUp?.step?.id)
        assertEquals(0, state.doneCount)
        assertEquals(StepStatus.NotStarted, state.steps[0].status)
    }

    @Test
    fun `after Metal 2 the chain jumps to the one-shot, not Metal 3`() {
        val state = order(comic(metal, chapterId = 102, finished = true), comic(redDeath))
        assertEquals(StepStatus.Done, state.steps[1].status)
        assertEquals(3L, state.nextUp?.step?.id)
        assertEquals(3L, state.stepAfter(2)?.step?.id)
    }

    @Test
    fun `optional unlinked steps are skipped by next up`() {
        val state = order(comic(metal, chapterId = 102, finished = true), comic(redDeath, chapterId = 101, finished = true))
        assertEquals(StepStatus.Unlinked, state.steps[3].status)
        assertEquals(5L, state.nextUp?.step?.id)
        assertEquals(5L, state.stepAfter(3)?.step?.id)
    }

    @Test
    fun `books missing from BatCave don't stall the chain`() {
        val withGap = steps.map { if (it.id == 3L) it.copy(comicUrl = null, unresolved = true) else it }
        val state = buildReadingOrder(
            seriesId = 1,
            steps = withGap,
            comics = mapOf(metal to comic(metal, chapterId = 102, finished = true)),
            chaptersByComic = mapOf(metal to chapters(metal, 6)),
        )
        assertEquals(StepStatus.NotFound, state.steps[2].status)
        assertEquals(5L, state.nextUp?.step?.id)
        assertEquals(5L, state.stepAfter(2)?.step?.id)
    }

    @Test
    fun `mid-chapter progress resumes at the saved page`() {
        val state = order(comic(metal, chapterId = 103, page = 7), comic(redDeath, chapterId = 101, finished = true))
        val metal3 = state.steps[4]
        assertEquals(StepStatus.InProgress, metal3.status)
        assertEquals(103 to 7, metal3.openTarget)
    }

    @Test
    fun `stepContaining finds the step for a chapter`() {
        val state = order(comic(metal), comic(redDeath))
        assertEquals(5L, state.stepContaining(metal, 103)?.step?.id)
        assertNull(state.stepContaining(metal, 104))
    }

    @Test
    fun `issue ranges select by number, falling back to position`() {
        val numbered = listOf(1f, 2f, 3f, 4f)
        assertEquals(listOf(2f, 3f), selectIssues(numbered, 2f, 3f) { it })
        // Site numbered chapters 0..3 instead of 1..4: issue 4 is the 4th chapter.
        val offByOne = listOf(10f, 11f, 12f, 13f)
        assertEquals(listOf(13f), selectIssues(offByOne, 4f, 4f) { it })
        assertEquals(numbered, selectIssues(numbered, null, null) { it })
    }

    @Test
    fun `titles match across punctuation, years and a leading The`() {
        assertEquals(TitleMatch.normalize("The Batman Who Laughs (2018)"), TitleMatch.normalize("Batman Who Laughs"))
        assertEquals(TitleMatch.normalize("Dark Nights: Metal"), TitleMatch.normalize("Dark Nights - Metal (2017)"))
        assertEquals(TitleMatch.normalize("Batman/Superman"), TitleMatch.normalize("Batman / Superman (2019)"))
        assertEquals(2018, TitleMatch.year("The Batman Who Laughs (2018)"))
        assertEquals("Batman Superman", TitleMatch.query("Batman/Superman"))
        assertEquals(TitleMatch.normalize("DC K.O.: Knightfight"), TitleMatch.normalize("DC KO - Knightfight (2025)"))
        assertEquals(TitleMatch.normalize("DC K.O.: Red Hood vs. The Joker"), TitleMatch.normalize("DC KO Red Hood vs The Joker"))
        assertEquals(TitleMatch.normalize("Fire & Ice: When Hell Freezes Over"), TitleMatch.normalize("Fire and Ice - When Hell Freezes Over"))
    }

    @Test
    fun `search phrasings keep dots first, then drop them`() {
        assertEquals(
            listOf("DC K.O. Knightfight", "DC K O Knightfight", "DC KO Knightfight"),
            TitleMatch.queries("DC K.O.: Knightfight"),
        )
        assertEquals(listOf("Batman Superman"), TitleMatch.queries("Batman/Superman"))
    }

    @Test
    fun `syncing keeps links and progress, adds sequels, drops removed steps`() {
        val seeds = listOf(
            StepSeed("Main", "Old Man Logan", "Wolverine", 2003, 66f, 72f),
            StepSeed("Main", "Giant-Size", "Wolverine: Old Man Logan Giant-Size"),
        )
        val stored = seeds.mapIndexed { i, seed -> seed.toEntity(seriesId = 9, position = i).copy(id = i + 1L) }
            .toMutableList()
        // The user linked the first step; a since-removed step is still stored.
        stored[0] = stored[0].copy(comicUrl = "/wolverine.html")
        stored += StepSeed("Old", "Gone", "Removed Title").toEntity(9, 2).copy(id = 3)

        val updated = listOf(
            StepSeed("Prelude", "New first step", "Something Earlier"),
            seeds[0].copy(note = "now with a note"),
            seeds[1],
            StepSeed("Sequels", "Dead Man Logan #1–12", "Dead Man Logan", null, 1f, 12f),
        )
        val plan = planStepSync(seriesId = 9, current = stored, seeds = updated)

        val wolverine = plan.updates.single { it.id == 1L }
        assertEquals("/wolverine.html", wolverine.comicUrl)
        assertEquals(1, wolverine.position)
        assertEquals("now with a note", wolverine.note)
        assertEquals(2, plan.updates.single { it.id == 2L }.position)
        assertEquals(listOf("Something Earlier", "Dead Man Logan"), plan.inserts.map { it.title })
        assertEquals(listOf(0, 3), plan.inserts.map { it.position })
        assertEquals(listOf(3L), plan.deletes)
    }

    @Test
    fun `syncing an unchanged order is a no-op`() {
        val seeds = DARK_NIGHTS_METAL.steps
        val stored = seeds.mapIndexed { i, seed -> seed.toEntity(1, i).copy(id = i + 1L) }
        val plan = planStepSync(1, stored, seeds)
        assertEquals(StepSyncPlan(emptyList(), emptyList(), emptyList()), plan)
    }

    @Test
    fun `built-in orders have unique keys and names`() {
        assertEquals(STARTER_READING_ORDERS.size, STARTER_READING_ORDERS.map { it.key }.toSet().size)
        assertEquals(STARTER_READING_ORDERS.size, STARTER_READING_ORDERS.map { it.name }.toSet().size)
    }

    @Test
    fun `built-in orders have sane issue ranges`() {
        for (order in STARTER_READING_ORDERS) {
            for (seed in order.steps) {
                if (seed.first != null && seed.last != null) {
                    assert(seed.first <= seed.last) { "${order.name}: ${seed.label}" }
                }
            }
        }
    }
}
