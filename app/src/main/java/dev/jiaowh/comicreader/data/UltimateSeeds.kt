package dev.jiaowh.comicreader.data

private const val U_SETUP = "Setup: Ultimate Invasion"
private const val U_LAUNCH = "Year One: The launch"
private const val U_YEAR_ONE = "Year One: Rising up"
private const val U_YEAR_TWO = "Year Two"
private const val U_FINAL_YEAR = "Final year: Series conclusions"
private const val U_ENDGAME = "Ultimate Endgame"
private const val U_EPILOGUE = "Epilogue (optional)"

private const val USM_2024 = "Ultimate Spider-Man"
private const val UBP_2024 = "Ultimate Black Panther"
private const val UXM_2024 = "Ultimate X-Men"
private const val ULTIMATES_2024 = "The Ultimates"
private const val WOLVERINE_2025 = "Ultimate Wolverine"
private const val ENDGAME = "Ultimate Endgame"

/**
 * Marvel's 2023–2026 Ultimate Universe (Earth-6160), from Ultimate Invasion to the Ultimate
 * Universe Finale. Based on ComicBookTreasury's "New Ultimate Universe Reading Order" (updated
 * June 2026), with each year's ongoing series split into smaller interleaved arcs. Free Comic Book
 * Day issues and the Ultimate Impact: Reborn epilogue (which continues in the main Marvel Universe)
 * are optional.
 */
val MARVEL_ULTIMATE = StarterReadingOrder(
    key = "marvel_ultimate_order_v1",
    name = "Marvel Ultimate Universe",
    steps = listOf(
        issues(U_SETUP, "Ultimate Invasion", null, 1, 4, note = "The Maker remakes a world; start here"),
        oneShot(U_SETUP, "Ultimate Universe", year = 2023, note = "Tony Stark sets the new heroes in motion; starts the Maker's countdown"),

        issues(U_LAUNCH, USM_2024, 2024, 1, 3, note = "Peter Parker, married with kids, finally gets his powers"),
        issues(U_LAUNCH, UBP_2024, null, 1, 3),
        issues(U_LAUNCH, UXM_2024, 2024, 1, 3, note = "Peach Momoko's horror-tinged mutants in Japan"),
        issues(U_LAUNCH, USM_2024, 2024, 4, 6),
        issues(U_LAUNCH, UBP_2024, null, 4, 6),
        issues(U_LAUNCH, UXM_2024, 2024, 4, 6),
        oneShot(U_LAUNCH, "Free Comic Book Day 2024: Ultimate Universe/Spider-Man", optional = true, note = "Short Ultimates prelude"),
        issues(U_LAUNCH, ULTIMATES_2024, 2024, 1, 6, note = "Tony and Doom's team starts tearing down the Maker's Council"),

        issues(U_YEAR_ONE, USM_2024, 2024, 7, 12),
        issues(U_YEAR_ONE, UBP_2024, null, 7, 12),
        issues(U_YEAR_ONE, UXM_2024, 2024, 7, 12),
        oneShot(U_YEAR_ONE, "Ultimate Universe: One Year In", note = "Status check on the whole line"),

        issues(U_YEAR_TWO, ULTIMATES_2024, 2024, 7, 12),
        issues(U_YEAR_TWO, WOLVERINE_2025, null, 1, 6, note = "The Winter Soldier and the Eurasian Republic"),
        issues(U_YEAR_TWO, USM_2024, 2024, 13, 18),
        issues(U_YEAR_TWO, UBP_2024, null, 13, 18),
        issues(U_YEAR_TWO, UXM_2024, 2024, 13, 18),
        issues(U_YEAR_TWO, "Ultimate Spider-Man: Incursion", null, 1, 5, note = "Miles Morales crosses over and takes the Origin Boxes"),
        oneShot(U_YEAR_TWO, "Free Comic Book Day 2025: Amazing Spider-Man/Ultimate Universe", optional = true),

        issues(U_FINAL_YEAR, ULTIMATES_2024, 2024, 13, 18),
        oneShot(U_FINAL_YEAR, "Ultimate Hawkeye", note = "Hawkeye's solo mission against the Maker"),
        issues(U_FINAL_YEAR, WOLVERINE_2025, null, 7, 12),
        oneShot(U_FINAL_YEAR, "Ultimate Universe: Two Years In", note = "The Maker is about to return"),
        issues(U_FINAL_YEAR, USM_2024, 2024, 19, 24, note = "Finale of the series"),
        issues(U_FINAL_YEAR, UXM_2024, 2024, 19, 24, note = "Finale of the series"),
        issues(U_FINAL_YEAR, UBP_2024, null, 19, 24, note = "Finale of the series"),
        issues(U_FINAL_YEAR, WOLVERINE_2025, null, 13, 16, note = "#14 ties into Ultimate Endgame"),

        issues(U_ENDGAME, ENDGAME, null, 1, note = "The Maker returns"),
        issues(U_ENDGAME, ULTIMATES_2024, 2024, 19, 20),
        issues(U_ENDGAME, ENDGAME, null, 2),
        issues(U_ENDGAME, ULTIMATES_2024, 2024, 21, 22),
        issues(U_ENDGAME, ENDGAME, null, 3),
        issues(U_ENDGAME, ULTIMATES_2024, 2024, 23),
        issues(U_ENDGAME, ENDGAME, null, 4),
        issues(U_ENDGAME, ULTIMATES_2024, 2024, 24),
        issues(U_ENDGAME, ENDGAME, null, 5),
        oneShot(U_ENDGAME, "Ultimate Universe Finale", note = "The end of the line; read only after Endgame"),

        issues(
            U_EPILOGUE, "Ultimate Impact: Reborn", null, 1, 5, optional = true,
            note = "The Origin Boxes hit the main Marvel Universe; leads into Armageddon",
        ),
    ),
)
