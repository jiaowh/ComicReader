package dev.jiaowh.comicreader.data

/** One step of a built-in reading order. [first]/[last] null = the whole comic (one-shots). */
data class StepSeed(
    val section: String,
    val label: String,
    val title: String,
    val year: Int? = null,
    val first: Float? = null,
    val last: Float? = null,
    val optional: Boolean = false,
    val note: String? = null,
)

data class StarterReadingOrder(val key: String, val name: String, val steps: List<StepSeed>)

internal fun issues(
    section: String,
    title: String,
    year: Int?,
    first: Int,
    last: Int = first,
    optional: Boolean = false,
    note: String? = null,
) = StepSeed(
    section = section,
    label = if (first == last) "$title #$first" else "$title #$first–$last",
    title = title,
    year = year,
    first = first.toFloat(),
    last = last.toFloat(),
    optional = optional,
    note = note,
)

internal fun oneShot(section: String, title: String, year: Int? = null, optional: Boolean = false, note: String? = null) =
    StepSeed(section, label = title, title = title, year = year, optional = optional, note = note)

private const val PRELUDE = "Prelude"
private const val METAL = "Dark Nights: Metal"
private const val DARK_KNIGHTS = "The Dark Knights (any order)"
private const val BWL = "After Metal: The Batman Who Laughs"
private const val SECRET_SIX = "After Metal: Batman/Superman"
private const val JUSTICE_LEAGUE = "After Metal: Justice League (optional)"
private const val DEATH_METAL = "Dark Nights: Death Metal"

/** Metal through Death Metal, focused on the evil Batmen of the Dark Multiverse. */
val DARK_NIGHTS_METAL = StarterReadingOrder(
    key = "metal_order_v1",
    name = "Dark Nights: Metal",
    steps = listOf(
        oneShot(PRELUDE, "Dark Days: The Forge", note = "Batman's secret investigation begins"),
        oneShot(PRELUDE, "Dark Days: The Casting"),

        issues(METAL, "Dark Nights: Metal", 2017, 1),
        issues(METAL, "Dark Nights: Metal", 2017, 2, note = "The evil Batmen arrive"),

        oneShot(DARK_KNIGHTS, "Batman: The Red Death", note = "Batman + the Flash"),
        oneShot(DARK_KNIGHTS, "Batman: The Murder Machine", note = "Batman + Cyborg"),
        oneShot(DARK_KNIGHTS, "Batman: The Dawnbreaker", note = "Batman + a Green Lantern ring"),
        oneShot(DARK_KNIGHTS, "Batman: The Drowned", note = "Batman + Aquaman"),
        oneShot(DARK_KNIGHTS, "Batman: The Merciless", note = "Batman + Ares's helmet"),
        oneShot(DARK_KNIGHTS, "Batman: The Devastator", note = "Batman + Doomsday"),
        oneShot(DARK_KNIGHTS, "The Batman Who Laughs", year = 2017, note = "Batman + the Joker; the ringleader"),

        issues(METAL, "Dark Nights: Metal", 2017, 3),
        issues(METAL, "Dark Nights: Metal", 2017, 4),
        oneShot(METAL, "Batman: Lost", note = "Important; don't skip"),
        oneShot(METAL, "Hawkman: Found", optional = true),
        issues(METAL, "Dark Nights: Metal", 2017, 5),
        oneShot(METAL, "The Wild Hunt", optional = true),
        issues(METAL, "Dark Nights: Metal", 2017, 6, note = "Finale"),

        issues(BWL, "The Batman Who Laughs", 2018, 1, 2, note = "Snyder & Jock; the best follow-up"),
        oneShot(BWL, "The Batman Who Laughs: The Grim Knight", note = "Origin of the Grim Knight"),
        issues(BWL, "The Batman Who Laughs", 2018, 3, 7),

        issues(SECRET_SIX, "Batman/Superman", 2019, 1, 6, note = "\"Who Are the Secret Six?\""),

        issues(
            JUSTICE_LEAGUE, "Justice League", 2018, 1, 39, optional = true,
            note = "Snyder's run; introduces Perpetua and sets up Death Metal",
        ),

        issues(DEATH_METAL, "Dark Nights: Death Metal", 2020, 1, 3),
        oneShot(DEATH_METAL, "Dark Nights: Death Metal: Robin King", note = "Bruce's evil child-self"),
        issues(DEATH_METAL, "Dark Nights: Death Metal", 2020, 4),
        oneShot(DEATH_METAL, "Dark Nights: Death Metal: Legends of the Dark Knights", note = "New evil Batmen"),
        issues(DEATH_METAL, "Dark Nights: Death Metal", 2020, 5),
        oneShot(DEATH_METAL, "Dark Nights: Death Metal: The Darkest Knight"),
        oneShot(DEATH_METAL, "Dark Nights: Death Metal: The Multiverse Who Laughs", note = "Dark Multiverse anthology"),
        issues(DEATH_METAL, "Dark Nights: Death Metal", 2020, 6, 7, note = "Finale"),
    ),
)

// --- DC All In, Act I ---

private const val AI_BEFORE = "Before DC All In (optional backstory)"
private const val AI_PRELUDE = "Prelude: Alpha/Omega"
private const val AI_JLU = "Justice League Unlimited: All Along the Watchtower"
private const val AI_GOTHAM = "The Batfamily and Gotham City"
private const val AI_EL = "The Rise of the House of El"
private const val AI_SUMMER = "Summer of Superman"
private const val AI_YESTERDAY = "We Are Yesterday"
private const val AI_LANTERNS = "The Green Lantern Corps at war"
private const val AI_NEW_GODS = "The New Gods"
private const val AI_BLUDHAVEN = "A new day in Blüdhaven and Gotham"
private const val AI_FLASH = "Catching up with the Flash"
private const val AI_BEFORE_BATTLE = "Before the battle begins"
private const val KO_LEAD_IN = "DC K.O.: The road to the tournament"
private const val KO_ROUND_1 = "DC K.O.: The tournament begins"
private const val KO_FIGHT_MONTH = "DC K.O.: All Fight Month"
private const val KO_FINAL = "DC K.O.: Final rounds"

private const val KO = "DC K.O."
private const val KNIGHTFIGHT = "DC K.O.: Knightfight"
private const val SUPERMAN_2023 = "Superman"
private const val ACTION_2016 = "Action Comics"
private const val BATMAN = "Batman"
private const val DETECTIVE_2016 = "Detective Comics"
private const val BATMAN_ROBIN_2023 = "Batman and Robin"
private const val WORLDS_FINEST_2022 = "Batman/Superman: World's Finest"
private const val WONDER_WOMAN_2023 = "Wonder Woman"
private const val GREEN_LANTERN_2023 = "Green Lantern"
private const val GL_CORPS_2025 = "Green Lantern Corps"
private const val NIGHTWING_2016 = "Nightwing"
private const val TITANS_2023 = "Titans"
private const val FLASH_2023 = "The Flash"
private const val GREEN_ARROW_2023 = "Green Arrow"
private const val AQUAMAN_2025 = "Aquaman"
private const val JLU_2024 = "Justice League Unlimited"
private const val POISON_IVY_2022 = "Poison Ivy"
private const val HARLEY_2021 = "Harley Quinn"

/**
 * DC All In, Act I ("Heroes United", Oct 2024 – Mar 2026), ending with the DC K.O. event.
 * Follows ComicBookTreasury's DC All In order (grouped by family, collected editions expanded
 * to their issues) and its interleaved DC K.O. order for the finale. Act II ("DC Next Level",
 * from Mar 2026) isn't included yet: no reading order existed for it when this was written.
 * The Absolute Universe runs in parallel and has its own "DC Absolute" series.
 */
val DC_ALL_IN = StarterReadingOrder(
    key = "dc_all_in_order_v1",
    name = "DC All In",
    steps = listOf(
        issues(AI_BEFORE, "New History of the DC Universe", null, 1, 4, optional = true),
        issues(AI_BEFORE, "Batman: Dark Patterns", null, 1, 12, optional = true),
        issues(AI_BEFORE, "Batman and Robin: Year One", null, 1, 12, optional = true),
        issues(AI_BEFORE, WORLDS_FINEST_2022, 2022, 31, 37, optional = true, note = "Total Eclipso; 20,000 Leagues"),
        oneShot(AI_BEFORE, "Superman Treasury 2025: Hero for All", optional = true),
        issues(AI_BEFORE, ACTION_2016, 2016, 1082, 1086, optional = true, note = "Superstars"),

        issues(AI_PRELUDE, ACTION_2016, 2016, 1070, 1081, note = "Phantoms"),
        oneShot(AI_PRELUDE, "DC All In Special", year = 2024, note = "Where it all starts: Darkseid dies, the Absolute Universe is born"),

        issues(AI_JLU, "Metamorpho: The Element Man", null, 1, 6),
        issues(AI_JLU, WONDER_WOMAN_2023, 2023, 14, 19, note = "Fury"),
        issues(AI_JLU, "Cheetah and Cheshire Rob the Justice League", null, 1, 6),
        issues(AI_JLU, "The Question: All Along the Watchtower", null, 1, 6),
        issues(AI_JLU, "Challengers of the Unknown", 2024, 1, 5),
        issues(AI_JLU, JLU_2024, 2024, 1, 5, note = "Into the Inferno"),
        issues(AI_JLU, "Black Lightning", 2025, 1, 5),
        oneShot(AI_JLU, "DC Power: Rise of the Power Company"),
        issues(AI_JLU, AQUAMAN_2025, 2025, 1, 8, note = "The Dark Tide"),
        issues(AI_JLU, GREEN_ARROW_2023, 2023, 17, 24, note = "Fresh Water Kills"),
        issues(AI_JLU, "Birds of Prey", 2023, 14, 26, note = "Bird Undercover; On the Run"),

        issues(AI_GOTHAM, DETECTIVE_2016, 2016, 1090, 1096, note = "Mercy of the Father"),
        issues(AI_GOTHAM, "Catwoman", 2018, 69, 78),
        issues(AI_GOTHAM, "Batgirl", 2024, 1, 11),
        issues(AI_GOTHAM, "Two-Face", 2025, 1, 6, note = "Trial Separation"),
        issues(AI_GOTHAM, "Batman/Santa Claus: Silent Knight Returns", null, 1, 5),
        issues(AI_GOTHAM, "Shazam!", 2023, 16, 21),
        issues(AI_GOTHAM, "Gotham City Sirens", 2024, 1, 5, note = "Unfit for Orbit"),
        issues(AI_GOTHAM, POISON_IVY_2022, 2022, 25, 30, note = "Human Botany"),
        issues(AI_GOTHAM, HARLEY_2021, 2021, 44, 49, note = "Destructive Comics"),
        issues(AI_GOTHAM, POISON_IVY_2022, 2022, 31, 41, note = "A Death in Marshview; Amuse-bouche"),
        issues(AI_GOTHAM, HARLEY_2021, 2021, 50, 55, note = "Friends with Detriments"),
        issues(AI_GOTHAM, BATMAN_ROBIN_2023, 2023, 14, 24, note = "Memento; The Gotham Cycle"),
        issues(AI_GOTHAM, BATMAN, 2016, 153, 157, note = "The Dying City"),

        issues(AI_EL, SUPERMAN_2023, 2023, 19, 23, note = "Rise of the Superwoman"),
        oneShot(AI_EL, "Superwoman Special"),
        oneShot(AI_EL, "Supergirl: Universe End", optional = true),
        issues(AI_EL, "Power Girl", 2023, 14, 20, note = "The Star"),
        issues(AI_EL, "Fire & Ice: When Hell Freezes Over", null, 1, 6),

        oneShot(AI_SUMMER, "Summer of Superman Special"),
        issues(AI_SUMMER, SUPERMAN_2023, 2023, 24, 27, note = "Love and Mercy"),
        oneShot(AI_SUMMER, "Superman: Lex Luthor Special"),
        issues(AI_SUMMER, "Secret Six", 2025, 1, 6),
        issues(AI_SUMMER, "Krypto: The Last Dog of Krypton", null, 1, 5),
        issues(AI_SUMMER, ACTION_2016, 2016, 1087, 1092, note = "Boy of Steel"),
        issues(AI_SUMMER, "Superman Unlimited", 2025, 1, 6, note = "Kryptonite Kingdom"),
        issues(AI_SUMMER, "Supergirl", 2025, 1, 6, note = "Misadventures in Midvale"),
        oneShot(AI_SUMMER, "Superman: The World"),
        issues(AI_SUMMER, "Adventures of Superman: Book of El", null, 1, 12),

        issues(AI_YESTERDAY, WORLDS_FINEST_2022, 2022, 38, 39, note = "The first DC All In crossover"),
        issues(AI_YESTERDAY, JLU_2024, 2024, 6, 8),
        oneShot(AI_YESTERDAY, "Batman/Superman: World's Finest 2025 Annual"),
        issues(AI_YESTERDAY, "Justice League: The Atom Project", null, 1, 6),
        issues(AI_YESTERDAY, "Justice League Red", null, 1, 6),
        issues(AI_YESTERDAY, "Mr. Terrific: Year One", null, 1, 6),
        issues(AI_YESTERDAY, "Zatanna", 2025, 1, 6, note = "It's Showtime!"),

        issues(AI_LANTERNS, GREEN_LANTERN_2023, 2023, 16, 18, note = "Civil Corps"),
        oneShot(AI_LANTERNS, "Green Lantern: Civil Corps Special"),
        oneShot(AI_LANTERNS, "Green Lantern: Fractured Spectrum Special"),
        issues(AI_LANTERNS, GL_CORPS_2025, 2025, 1, 5, note = "New World Rising"),
        issues(AI_LANTERNS, GREEN_LANTERN_2023, 2023, 19, 24, note = "Fractured Spectrum"),
        issues(AI_LANTERNS, GREEN_LANTERN_2023, 2023, 25, 27, note = "The Starbreaker Supremacy, part 1"),
        issues(AI_LANTERNS, GL_CORPS_2025, 2025, 7, 9, note = "The Starbreaker Supremacy, part 2"),

        issues(AI_NEW_GODS, "The New Gods", 2024, 1, 12),

        issues(AI_BLUDHAVEN, NIGHTWING_2016, 2016, 119, 124, note = "On with the Show"),
        issues(AI_BLUDHAVEN, TITANS_2023, 2023, 16, 20, note = "Hard Feelings"),
        issues(AI_BLUDHAVEN, NIGHTWING_2016, 2016, 125, 129, note = "Death Traps"),
        oneShot(AI_BLUDHAVEN, "Nightwing 2025 Annual"),
        issues(AI_BLUDHAVEN, NIGHTWING_2016, 2016, 130, 135, note = "The Cirque du Sin"),
        issues(AI_BLUDHAVEN, BATMAN, 2016, 158, 163, note = "Hush 2"),
        issues(AI_BLUDHAVEN, DETECTIVE_2016, 2016, 1097, 1102, note = "Elixir"),
        issues(AI_BLUDHAVEN, BATMAN, 2025, 1, 6, note = "Daylight; the new Batman #1"),
        issues(AI_BLUDHAVEN, BATMAN_ROBIN_2023, 2023, 25, 28, note = "The Quiet Man"),

        issues(AI_FLASH, FLASH_2023, 2023, 14, 25, note = "As Above; Bad Moon Rising"),
        oneShot(AI_FLASH, "The Flash: Bad Moon Rising Special"),
        issues(AI_FLASH, "JSA", 2024, 1, 12),

        issues(AI_BEFORE_BATTLE, WONDER_WOMAN_2023, 2023, 23, 28, note = "The Island of Mice and Men"),
        issues(AI_BEFORE_BATTLE, TITANS_2023, 2023, 22, 27, note = "Terminated"),
        issues(AI_BEFORE_BATTLE, GREEN_ARROW_2023, 2023, 25, 31, note = "Crimson Sands"),

        // DC K.O., interleaved with its tie-ins (ComicBookTreasury's DC K.O. order).
        issues(KO_LEAD_IN, JLU_2024, 2024, 9, 11, note = "The Omega Act"),
        oneShot(KO_LEAD_IN, "Justice League: Dark Tomorrow Special"),
        oneShot(KO_LEAD_IN, "Justice League: Legion of Darkseid Special"),
        issues(KO_LEAD_IN, SUPERMAN_2023, 2023, 28, 30, note = "Superman vs. the Darkseid Legion"),
        issues(KO_LEAD_IN, AQUAMAN_2025, 2025, 11),
        oneShot(KO_LEAD_IN, "Justice League: The Omega Act", note = "Leads straight into K.O. #1"),

        issues(KO_ROUND_1, KO, 2025, 1),
        oneShot(KO_ROUND_1, "DC K.O.: Green Lantern Galactic Slam"),
        issues(KO_ROUND_1, TITANS_2023, 2023, 28, 29),
        issues(KO_ROUND_1, SUPERMAN_2023, 2023, 31),
        issues(KO_ROUND_1, FLASH_2023, 2023, 26, 27),
        issues(KO_ROUND_1, KNIGHTFIGHT, 2025, 1, 2),
        issues(KO_ROUND_1, KO, 2025, 2),
        issues(KO_ROUND_1, SUPERMAN_2023, 2023, 32),

        oneShot(KO_FIGHT_MONTH, "DC K.O.: Superman vs. Captain Atom"),
        issues(KO_FIGHT_MONTH, AQUAMAN_2025, 2025, 12),
        oneShot(KO_FIGHT_MONTH, "DC K.O.: Wonder Woman vs. Lobo"),
        issues(KO_FIGHT_MONTH, TITANS_2023, 2023, 30),
        oneShot(KO_FIGHT_MONTH, "DC K.O.: Harley Quinn vs. Zatanna"),
        issues(KO_FIGHT_MONTH, FLASH_2023, 2023, 28),
        issues(KO_FIGHT_MONTH, SUPERMAN_2023, 2023, 33),
        oneShot(KO_FIGHT_MONTH, "DC K.O.: Red Hood vs. The Joker"),

        issues(KO_FINAL, JLU_2024, 2024, 12, 16),
        oneShot(KO_FINAL, "DC K.O.: The Kids Are All Fight Special"),
        issues(KO_FINAL, TITANS_2023, 2023, 31, 32),
        issues(KO_FINAL, FLASH_2023, 2023, 29, 30),
        issues(KO_FINAL, KO, 2025, 3),
        oneShot(KO_FINAL, "DC K.O.: Boss Battle"),
        issues(KO_FINAL, KNIGHTFIGHT, 2025, 3, 4),
        issues(KO_FINAL, AQUAMAN_2025, 2025, 13, 14),
        issues(KO_FINAL, SUPERMAN_2023, 2023, 34),
        issues(KO_FINAL, KO, 2025, 4),
        issues(KO_FINAL, SUPERMAN_2023, 2023, 35),
        issues(KO_FINAL, KO, 2025, 5, note = "Finale of Act I"),
    ),
)

val STARTER_READING_ORDERS = listOf(
    DARK_NIGHTS_METAL,
    DC_ALL_IN,
    MARVEL_ULTIMATE,
    DC_BRIDGE,
    SNYDER_BATMAN,
    DARK_MULTIVERSE,
    FLASHPOINT,
    HICKMAN_AVENGERS,
    ORIGINAL_ULTIMATE,
    SPIDER_VERSE,
    SPIDER_VERSE_SEQUELS,
    KNIGHTFALL_HUSH,
    WHITE_KNIGHT,
    ELSEWORLDS,
    INJUSTICE,
    SUPERIOR_SPIDER_MAN,
    THANOS_WINS,
    FUTURE_IMPERFECT,
    OLD_MAN_LOGAN,
    DARKSEID_WAR,
    AVENGERS_VS_X_MEN,
    DARK_PHOENIX,
    PARALLAX,
    BLACKEST_NIGHT,
    INFINITY_GAUNTLET,
    INFINITE_CRISIS,
    DOOMSDAY_CLOCK,
    SECRET_EMPIRE,
    AXIS,
    VENOMVERSE,
    IRREDEEMABLE,
    LAST_KNIGHT_ON_EARTH,
    DARK_KNIGHTS_OF_STEEL,
    FLASHPOINT_BEYOND,
    MARVEL_1602,
    DARK_KNIGHT_RETURNS,
    IMMORTAL_HULK,
    WATCHMEN,
    SINESTRO_CORPS_WAR,
    HOUSE_OF_M,
    VENOM_CATES,
    FINAL_CRISIS,
    KILLING_JOKE,
    TOWER_OF_BABEL,
    BLACK_MIRROR,
    SPIDER_MAN_REIGN,
    PLANET_HULK,
    INVINCIBLE,
    MARVEL_ZOMBIES,
    CRISIS_ON_INFINITE_EARTHS,
    HOUSE_OF_X,
    AGE_OF_APOCALYPSE,
    THE_BOYS,
)

