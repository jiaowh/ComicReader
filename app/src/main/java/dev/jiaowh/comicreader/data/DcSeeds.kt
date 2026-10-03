package dev.jiaowh.comicreader.data

// --- The bridge: Death Metal to DC All In ---

private const val BR_FRONTIER = "Infinite Frontier"
private const val BR_DARK_CRISIS = "Dark Crisis on Infinite Earths"
private const val BR_LAZARUS = "Lazarus Planet"
private const val BR_KNIGHT_TERRORS = "Knight Terrors"
private const val BR_GOTHAM_WAR = "The Gotham War"
private const val BR_BEAST_WORLD = "Titans: Beast World"
private const val BR_BRAINIAC = "Superman: House of Brainiac"
private const val BR_ABSOLUTE_POWER = "Absolute Power"

private const val DARK_CRISIS = "Dark Crisis on Infinite Earths"
private const val KNIGHT_TERRORS_BATMAN = "Knight Terrors: Batman"
private const val ACTION = "Action Comics"
private const val SUPERMAN = "Superman"
private const val BATMAN = "Batman"

/**
 * The event spine from the end of Death Metal (2021) to the DC All In Special (2024): Infinite
 * Frontier, Dark Crisis, then Dawn of DC's events ending with Absolute Power. Ongoing series are
 * left out except where an event runs through them. Event orders follow How To Love Comics and
 * ComicBookTreasury's Infinite Frontier / Dawn of DC guides.
 */
val DC_BRIDGE = StarterReadingOrder(
    key = "dc_bridge_order_v1",
    name = "DC: Death Metal to All In",
    steps = listOf(
        issues(BR_FRONTIER, "Infinite Frontier", 2021, 0, note = "Picks up right after Death Metal"),
        issues(BR_FRONTIER, "Infinite Frontier", 2021, 1, 6),
        issues(BR_FRONTIER, "Justice League Incarnate", null, 1, 5, note = "Leads straight into Dark Crisis"),

        issues(BR_DARK_CRISIS, "Justice League", 2018, 75, note = "The Death of the Justice League"),
        oneShot(BR_DARK_CRISIS, "Justice League: Road to Dark Crisis"),
        oneShot(BR_DARK_CRISIS, "Dark Crisis Free Comic Book Day Special Edition", optional = true),
        issues(BR_DARK_CRISIS, DARK_CRISIS, 2022, 1, 3),
        issues(BR_DARK_CRISIS, "Dark Crisis: Young Justice", null, 1, 6, optional = true),
        oneShot(BR_DARK_CRISIS, "Dark Crisis: Worlds Without a Justice League - Batman", optional = true),
        issues(BR_DARK_CRISIS, DARK_CRISIS, 2022, 4, 7, note = "Finale"),

        oneShot(BR_LAZARUS, "Lazarus Planet: Alpha"),
        oneShot(BR_LAZARUS, "Lazarus Planet: Assault on Krypton", optional = true),
        oneShot(BR_LAZARUS, "Lazarus Planet: We Once Were Gods", optional = true),
        oneShot(BR_LAZARUS, "Lazarus Planet: Legends Reborn", optional = true),
        oneShot(BR_LAZARUS, "Lazarus Planet: Next Evolution", optional = true),
        oneShot(BR_LAZARUS, "Lazarus Planet: Dark Fate", optional = true),
        oneShot(BR_LAZARUS, "Lazarus Planet: Omega"),
        issues(BR_LAZARUS, "Lazarus Planet: Revenge of the Gods", null, 1, 4, optional = true),

        oneShot(BR_KNIGHT_TERRORS, "Knight Terrors: First Blood", note = "Every hero is trapped in their nightmares"),
        issues(BR_KNIGHT_TERRORS, KNIGHT_TERRORS_BATMAN, null, 1),
        issues(BR_KNIGHT_TERRORS, "Knight Terrors", 2023, 1, 4),
        issues(BR_KNIGHT_TERRORS, KNIGHT_TERRORS_BATMAN, null, 2),
        oneShot(BR_KNIGHT_TERRORS, "Knight Terrors: Night's End"),

        oneShot(BR_GOTHAM_WAR, "Batman/Catwoman: The Gotham War: Battle Lines", note = "Batman vs. Catwoman for Gotham's crime"),
        issues(BR_GOTHAM_WAR, BATMAN, 2016, 137),
        issues(BR_GOTHAM_WAR, "Catwoman", 2018, 57),
        issues(BR_GOTHAM_WAR, BATMAN, 2016, 138),
        issues(BR_GOTHAM_WAR, "Catwoman", 2018, 58),
        oneShot(BR_GOTHAM_WAR, "Batman/Catwoman: The Gotham War: Red Hood", optional = true),
        oneShot(BR_GOTHAM_WAR, "Batman/Catwoman: The Gotham War: Scorched Earth"),

        issues(BR_BEAST_WORLD, "Titans: Beast World", null, 1, 6),

        issues(BR_BRAINIAC, ACTION, 2016, 1064),
        issues(BR_BRAINIAC, SUPERMAN, 2023, 13),
        oneShot(BR_BRAINIAC, "Superman: House of Brainiac Special"),
        issues(BR_BRAINIAC, ACTION, 2016, 1065),
        issues(BR_BRAINIAC, SUPERMAN, 2023, 14),
        issues(BR_BRAINIAC, ACTION, 2016, 1066),
        issues(BR_BRAINIAC, SUPERMAN, 2023, 15),

        oneShot(BR_ABSOLUTE_POWER, "Absolute Power: Ground Zero"),
        issues(BR_ABSOLUTE_POWER, "Absolute Power", 2024, 1, 4, note = "Amanda Waller takes the heroes' powers"),
        issues(BR_ABSOLUTE_POWER, "Absolute Power: Task Force VII", null, 1, 7, optional = true),
        issues(BR_ABSOLUTE_POWER, "Absolute Power: Origins", null, 1, 3, optional = true, note = "Continues in the DC All In order"),
    ),
)

// --- Snyder & Capullo's Batman ---

private const val SC_OWLS = "The Court of Owls"
private const val SC_DEATH_FAMILY = "Death of the Family"
private const val SC_ZERO_YEAR = "Zero Year"
private const val SC_ENDGAME = "Endgame"
private const val SC_SUPERHEAVY = "Superheavy"
private const val BATMAN_2011 = "Batman"

/** Scott Snyder and Greg Capullo's New 52 run, the team behind Dark Nights: Metal. */
val SNYDER_BATMAN = StarterReadingOrder(
    key = "snyder_batman_order_v1",
    name = "Batman by Snyder & Capullo",
    steps = listOf(
        issues(SC_OWLS, BATMAN_2011, 2011, 1, 7, note = "The Court of Owls"),
        issues(SC_OWLS, BATMAN_2011, 2011, 8, 11, note = "The City of Owls"),
        issues(SC_OWLS, BATMAN_2011, 2011, 0, optional = true, note = "Zero issue; a Zero Year teaser"),
        issues(SC_OWLS, BATMAN_2011, 2011, 12, optional = true, note = "Harper Row standalone"),

        issues(SC_DEATH_FAMILY, BATMAN_2011, 2011, 13, 17, note = "The Joker returns"),
        issues(SC_DEATH_FAMILY, BATMAN_2011, 2011, 18, 20, note = "Aftermath and Clayface"),

        issues(SC_ZERO_YEAR, BATMAN_2011, 2011, 21, 24, note = "Secret City"),
        issues(SC_ZERO_YEAR, BATMAN_2011, 2011, 25, 27, note = "Dark City"),
        issues(SC_ZERO_YEAR, BATMAN_2011, 2011, 28, optional = true, note = "Batman Eternal preview"),
        issues(SC_ZERO_YEAR, BATMAN_2011, 2011, 29, 33, note = "Savage City"),
        issues(SC_ZERO_YEAR, BATMAN_2011, 2011, 34, optional = true),

        issues(SC_ENDGAME, BATMAN_2011, 2011, 35, 40, note = "The final Batman vs. Joker"),

        issues(SC_SUPERHEAVY, BATMAN_2011, 2011, 41, 50, note = "Jim Gordon becomes Batman"),
        issues(SC_SUPERHEAVY, BATMAN_2011, 2011, 51, note = "Epilogue"),
    ),
)

// --- Tales from the Dark Multiverse ---

private const val TDM_2019 = "First wave (2019)"
private const val TDM_2020 = "Second wave (2020–21)"
private const val TDM = "Tales from the Dark Multiverse"

/** Standalone "what if it went wrong" retellings of famous DC stories, in release order. */
val DARK_MULTIVERSE = StarterReadingOrder(
    key = "dark_multiverse_order_v1",
    name = "Tales from the Dark Multiverse",
    steps = listOf(
        oneShot(TDM_2019, "$TDM: Batman: Knightfall", note = "Azrael wins"),
        oneShot(TDM_2019, "$TDM: The Death of Superman"),
        oneShot(TDM_2019, "$TDM: Blackest Night"),
        oneShot(TDM_2019, "$TDM: Infinite Crisis"),
        oneShot(TDM_2019, "$TDM: Teen Titans: The Judas Contract"),
        oneShot(TDM_2020, "$TDM: Crisis on Infinite Earths"),
        oneShot(TDM_2020, "$TDM: Dark Nights: Metal", note = "Metal, if the Batman Who Laughs had won"),
        oneShot(TDM_2020, "$TDM: Flashpoint"),
        oneShot(TDM_2020, "$TDM: Hush"),
        oneShot(TDM_2020, "$TDM: Wonder Woman: War of the Gods"),
    ),
)

// --- Flashpoint ---

private const val FP_ROAD = "The road to Flashpoint"
private const val FP_EVENT = "Flashpoint"

/** The 2011 alternate-timeline event, with Thomas Wayne's Batman mini where it belongs. */
val FLASHPOINT = StarterReadingOrder(
    key = "flashpoint_order_v1",
    name = "Flashpoint",
    steps = listOf(
        issues(FP_ROAD, "The Flash", 2010, 8, 12, optional = true, note = "Sets up Barry's mistake"),
        issues(FP_EVENT, "Flashpoint", 2011, 1, 2),
        issues(FP_EVENT, "Flashpoint: Batman: Knight of Vengeance", null, 1, 3, note = "Thomas Wayne's Batman; takes place here"),
        issues(FP_EVENT, "Flashpoint: Emperor Aquaman", null, 1, 3, optional = true),
        issues(FP_EVENT, "Flashpoint: Wonder Woman and the Furies", null, 1, 3, optional = true),
        issues(FP_EVENT, "Flashpoint", 2011, 3, 5, note = "Leads into the New 52"),
    ),
)

// --- Knightfall and Hush ---

private const val KF_PRELUDE = "Prelude"
private const val KF_BROKEN_BAT = "Knightfall, part 1: Broken Bat"
private const val KF_WHO_RULES = "Knightfall, part 2: Who Rules the Night"
private const val KF_CRUSADE = "Knightquest: The Crusade"
private const val KF_SEARCH = "Knightquest: The Search"
private const val KF_KNIGHTSEND = "KnightsEnd"
private const val KF_PRODIGAL = "Prodigal (optional)"
private const val HUSH = "Hush"

private const val BATMAN_1940 = "Batman"
private const val DETECTIVE_1937 = "Detective Comics"
private const val SHADOW_OF_THE_BAT = "Batman: Shadow of the Bat"
private const val LOTDK = "Batman: Legends of the Dark Knight"
private const val ROBIN_1993 = "Robin"
private const val CATWOMAN_1993 = "Catwoman"

/**
 * The Knightfall saga (Bane breaks Batman, Azrael takes over, Bruce takes the cowl back), then
 * Hush. Tales from the Dark Multiverse: Knightfall retells this one. Issue order follows
 * ComicBookTreasury's Knightfall guide; Catwoman, Showcase and Prodigal are optional.
 */
val KNIGHTFALL_HUSH = StarterReadingOrder(
    key = "knightfall_hush_order_v1",
    name = "Batman: Knightfall & Hush",
    steps = listOf(
        oneShot(KF_PRELUDE, "Batman: Vengeance of Bane", note = "Bane's origin"),

        issues(KF_BROKEN_BAT, BATMAN_1940, 1940, 491, 492, note = "Bane breaks open Arkham"),
        issues(KF_BROKEN_BAT, DETECTIVE_1937, 1937, 659),
        issues(KF_BROKEN_BAT, BATMAN_1940, 1940, 493),
        issues(KF_BROKEN_BAT, DETECTIVE_1937, 1937, 660),
        issues(KF_BROKEN_BAT, BATMAN_1940, 1940, 494),
        issues(KF_BROKEN_BAT, DETECTIVE_1937, 1937, 661),
        issues(KF_BROKEN_BAT, BATMAN_1940, 1940, 495),
        issues(KF_BROKEN_BAT, DETECTIVE_1937, 1937, 662),
        issues(KF_BROKEN_BAT, BATMAN_1940, 1940, 496),
        issues(KF_BROKEN_BAT, DETECTIVE_1937, 1937, 663),
        issues(KF_BROKEN_BAT, BATMAN_1940, 1940, 497, note = "The broken bat"),

        issues(KF_WHO_RULES, DETECTIVE_1937, 1937, 664),
        issues(KF_WHO_RULES, "Showcase '93", null, 7, 8, optional = true),
        issues(KF_WHO_RULES, BATMAN_1940, 1940, 498),
        issues(KF_WHO_RULES, SHADOW_OF_THE_BAT, 1992, 16, 18),
        issues(KF_WHO_RULES, DETECTIVE_1937, 1937, 665),
        issues(KF_WHO_RULES, BATMAN_1940, 1940, 499),
        issues(KF_WHO_RULES, DETECTIVE_1937, 1937, 666),
        issues(KF_WHO_RULES, BATMAN_1940, 1940, 500, note = "Azrael's Batman vs. Bane"),

        issues(KF_CRUSADE, ROBIN_1993, 1993, 1, 2, optional = true),
        issues(KF_CRUSADE, DETECTIVE_1937, 1937, 667, 668),
        issues(KF_CRUSADE, SHADOW_OF_THE_BAT, 1992, 19, 20),
        issues(KF_CRUSADE, BATMAN_1940, 1940, 501, 502),
        issues(KF_CRUSADE, DETECTIVE_1937, 1937, 669, 670),
        issues(KF_CRUSADE, CATWOMAN_1993, 1993, 6, optional = true),
        issues(KF_CRUSADE, BATMAN_1940, 1940, 503, 504),
        issues(KF_CRUSADE, CATWOMAN_1993, 1993, 7, optional = true),
        issues(KF_CRUSADE, DETECTIVE_1937, 1937, 671, 673),
        issues(KF_CRUSADE, SHADOW_OF_THE_BAT, 1992, 24),
        issues(KF_CRUSADE, BATMAN_1940, 1940, 505),
        issues(KF_CRUSADE, SHADOW_OF_THE_BAT, 1992, 25),
        issues(KF_CRUSADE, BATMAN_1940, 1940, 506, 507),
        issues(KF_CRUSADE, SHADOW_OF_THE_BAT, 1992, 26, 27),
        issues(KF_CRUSADE, DETECTIVE_1937, 1937, 674),
        issues(KF_CRUSADE, BATMAN_1940, 1940, 508),
        issues(KF_CRUSADE, SHADOW_OF_THE_BAT, 1992, 28),
        issues(KF_CRUSADE, DETECTIVE_1937, 1937, 675),
        issues(KF_CRUSADE, "Showcase '94", null, 7, optional = true),

        issues(KF_SEARCH, "Justice League Task Force", 1993, 5, 6, note = "Meanwhile, Bruce searches for a cure"),
        issues(KF_SEARCH, SHADOW_OF_THE_BAT, 1992, 21, 23),
        issues(KF_SEARCH, LOTDK, 1989, 59, 61),
        issues(KF_SEARCH, ROBIN_1993, 1993, 7),

        issues(KF_KNIGHTSEND, BATMAN_1940, 1940, 509, note = "Bruce takes the cowl back"),
        issues(KF_KNIGHTSEND, SHADOW_OF_THE_BAT, 1992, 29),
        issues(KF_KNIGHTSEND, DETECTIVE_1937, 1937, 676),
        issues(KF_KNIGHTSEND, LOTDK, 1989, 62),
        issues(KF_KNIGHTSEND, ROBIN_1993, 1993, 8),
        issues(KF_KNIGHTSEND, CATWOMAN_1993, 1993, 12),
        issues(KF_KNIGHTSEND, BATMAN_1940, 1940, 510),
        issues(KF_KNIGHTSEND, SHADOW_OF_THE_BAT, 1992, 30),
        issues(KF_KNIGHTSEND, DETECTIVE_1937, 1937, 677),
        issues(KF_KNIGHTSEND, LOTDK, 1989, 63),
        issues(KF_KNIGHTSEND, ROBIN_1993, 1993, 9),
        issues(KF_KNIGHTSEND, CATWOMAN_1993, 1993, 13),
        issues(KF_KNIGHTSEND, "Showcase '94", null, 10, optional = true),

        issues(KF_PRODIGAL, ROBIN_1993, 1993, 0, optional = true),
        issues(KF_PRODIGAL, BATMAN_1940, 1940, 512, optional = true, note = "Dick Grayson as Batman"),
        issues(KF_PRODIGAL, SHADOW_OF_THE_BAT, 1992, 32, optional = true),
        issues(KF_PRODIGAL, DETECTIVE_1937, 1937, 679, optional = true),
        issues(KF_PRODIGAL, ROBIN_1993, 1993, 11, optional = true),
        issues(KF_PRODIGAL, BATMAN_1940, 1940, 513, optional = true),
        issues(KF_PRODIGAL, SHADOW_OF_THE_BAT, 1992, 33, optional = true),
        issues(KF_PRODIGAL, DETECTIVE_1937, 1937, 680, optional = true),
        issues(KF_PRODIGAL, ROBIN_1993, 1993, 12, optional = true),
        issues(KF_PRODIGAL, BATMAN_1940, 1940, 514, optional = true),
        issues(KF_PRODIGAL, SHADOW_OF_THE_BAT, 1992, 34, optional = true),
        issues(KF_PRODIGAL, DETECTIVE_1937, 1937, 681, optional = true),
        issues(KF_PRODIGAL, ROBIN_1993, 1993, 13, optional = true),

        issues(HUSH, BATMAN_1940, 1940, 608, 613, note = "Jeph Loeb & Jim Lee"),
        issues(HUSH, BATMAN_1940, 1940, 614, 619, note = "Hush 2 in the DC All In order is the sequel"),
    ),
)

// --- Batman: White Knight (the Murphyverse) ---

private const val WK_MAIN = "The White Knight saga"
private const val WK_PRESENTS = "White Knight Presents"

/** Sean Murphy's Black Label universe, in publication order (Wikipedia's Murphyverse list). */
val WHITE_KNIGHT = StarterReadingOrder(
    key = "white_knight_order_v1",
    name = "Batman: White Knight",
    steps = listOf(
        issues(WK_MAIN, "Batman: White Knight", null, 1, 8, note = "The Joker is cured; Batman is the problem"),
        issues(WK_MAIN, "Batman: Curse of the White Knight", null, 1, 8),
        oneShot(WK_PRESENTS, "Batman: White Knight Presents: Von Freeze", note = "Collected with Curse; Mr. Freeze's past"),
        issues(WK_PRESENTS, "Batman: White Knight Presents: Harley Quinn", null, 1, 6),
        issues(WK_MAIN, "Batman: Beyond the White Knight", null, 1, 8, note = "Twelve years later"),
        issues(WK_PRESENTS, "Batman: White Knight Presents: Red Hood", null, 1, 2),
        issues(WK_PRESENTS, "Batman: White Knight Presents: Generation Joker", null, 1, 6),
    ),
)

// --- Elseworlds ---

private const val EW_EVIL = "Evil twists"
private const val EW_SETTINGS = "Different settings"
private const val EW_CLASSICS = "The big ones"

/**
 * DC's 1989–2000s "what if?" label: standalone stories, readable in any order. A curated
 * checklist of the best, leaning toward the darker ones.
 */
val ELSEWORLDS = StarterReadingOrder(
    key = "elseworlds_order_v1",
    name = "Elseworlds",
    steps = listOf(
        oneShot(EW_EVIL, "Batman & Dracula: Red Rain", note = "Red Rain trilogy, part 1: Batman becomes a vampire"),
        oneShot(EW_EVIL, "Batman: Bloodstorm", note = "Red Rain trilogy, part 2"),
        oneShot(EW_EVIL, "Batman: Crimson Mist", note = "Red Rain trilogy, part 3"),
        issues(EW_EVIL, "Superman: The Dark Side", null, 1, 3, note = "Kal-El raised by Darkseid"),
        issues(EW_EVIL, "Batman: The Doom That Came to Gotham", null, 1, 3, note = "Mignola's Lovecraftian Batman"),
        issues(EW_EVIL, "Superman: Red Son", null, 1, 3, note = "Superman lands in Soviet Russia"),

        oneShot(EW_SETTINGS, "Batman: Gotham by Gaslight", note = "Victorian Batman vs. Jack the Ripper; the first Elseworlds"),
        oneShot(EW_SETTINGS, "Batman: Master of the Future", note = "Sequel to Gotham by Gaslight"),
        oneShot(EW_SETTINGS, "Superman: Speeding Bullets", note = "The Waynes adopt baby Kal-El"),
        oneShot(EW_SETTINGS, "Batman: In Darkest Knight", note = "Bruce gets the Green Lantern ring"),

        issues(EW_CLASSICS, "Kingdom Come", null, 1, 4, note = "Waid & Ross; an aging Superman returns"),
        issues(EW_CLASSICS, "The Kingdom", null, 1, 2, optional = true, note = "Sequel to Kingdom Come"),
        issues(EW_CLASSICS, "JLA: The Nail", null, 1, 3, note = "The Kents never find Kal-El"),
        issues(EW_CLASSICS, "JLA: Another Nail", null, 1, 3),
    ),
)

// --- Injustice ---

private const val INJ_PREQUEL = "Prequel"
private const val INJ_REGIME = "Gods Among Us: the Regime rises"
private const val INJ_GROUND_ZERO = "Ground Zero: the game's story"
private const val INJ_TWO = "Injustice 2"
private const val INJ_EXTRA = "Extra (optional)"

/**
 * Tom Taylor's Injustice saga: the Joker tricks Superman into destroying Metropolis and Superman
 * becomes a tyrant. Year-by-year order per Comic Book Herald / How To Love Comics, each year's
 * annual after its issues.
 */
val INJUSTICE = StarterReadingOrder(
    key = "injustice_order_v1",
    name = "Injustice",
    steps = listOf(
        issues(INJ_PREQUEL, "Injustice: Year Zero", null, 1, 14, note = "Published in 2020; the Joker vs. the JSA"),

        issues(INJ_REGIME, "Injustice: Gods Among Us", 2013, 1, 12, note = "Year One: Metropolis falls"),
        oneShot(INJ_REGIME, "Injustice: Gods Among Us Annual"),
        issues(INJ_REGIME, "Injustice: Gods Among Us: Year Two", null, 1, 12, note = "The Green Lantern war"),
        oneShot(INJ_REGIME, "Injustice: Gods Among Us: Year Two Annual"),
        issues(INJ_REGIME, "Injustice: Gods Among Us: Year Three", null, 1, 12, note = "Magic"),
        oneShot(INJ_REGIME, "Injustice: Gods Among Us: Year Three Annual"),
        issues(INJ_REGIME, "Injustice: Gods Among Us: Year Four", null, 1, 12, note = "The gods"),
        oneShot(INJ_REGIME, "Injustice: Gods Among Us: Year Four Annual"),
        issues(INJ_REGIME, "Injustice: Gods Among Us: Year Five", null, 1, 20, note = "The Regime recruits villains"),
        oneShot(INJ_REGIME, "Injustice: Gods Among Us: Year Five Annual"),

        issues(INJ_GROUND_ZERO, "Injustice: Ground Zero", null, 1, 12, note = "The first game, told by Harley"),

        issues(INJ_TWO, "Injustice 2", null, 1, 12),
        issues(INJ_TWO, "Injustice 2 Annual", null, 1),
        issues(INJ_TWO, "Injustice 2", null, 13, 24),
        issues(INJ_TWO, "Injustice 2 Annual", null, 2),
        issues(INJ_TWO, "Injustice 2", null, 25, 36),

        issues(INJ_EXTRA, "Injustice vs. Masters of the Universe", null, 1, 6, optional = true),
    ),
)

// --- Heroes made gods ---

private const val DW_WAR = "The Darkseid War"
private const val DW_GODS = "The new gods of the Justice League"
private const val DW_AFTER = "Afterward (optional)"
private const val JL_2011 = "Justice League"

/**
 * Geoff Johns's Darkseid War (Justice League #40–50): Batman sits in the Möbius Chair and becomes
 * the God of Knowledge. One-shots go between #45 and #46, per ComicBookTreasury.
 */
val DARKSEID_WAR = StarterReadingOrder(
    key = "darkseid_war_order_v1",
    name = "Justice League: The Darkseid War",
    steps = listOf(
        issues(DW_WAR, JL_2011, 2011, 40, note = "Prologue"),
        issues(DW_WAR, JL_2011, 2011, 41, 45),
        oneShot(DW_GODS, "Justice League: Darkseid War: Batman", note = "The God of Knowledge"),
        oneShot(DW_GODS, "Justice League: Darkseid War: The Flash", note = "The God of Death"),
        oneShot(DW_GODS, "Justice League: Darkseid War: Superman", note = "The God of Strength"),
        oneShot(DW_GODS, "Justice League: Darkseid War: Green Lantern", note = "The God of Light"),
        oneShot(DW_GODS, "Justice League: Darkseid War: Shazam", note = "The God of Gods"),
        oneShot(DW_GODS, "Justice League: Darkseid War: Lex Luthor", note = "The God of Apokolips"),
        oneShot(DW_GODS, "Justice League: Darkseid War: Power Ring", optional = true),
        issues(DW_WAR, JL_2011, 2011, 46, 48),
        oneShot(DW_WAR, "Justice League: Darkseid War Special"),
        issues(DW_WAR, JL_2011, 2011, 49, 50, note = "Finale"),
        issues(DW_AFTER, "Batman: Three Jokers", null, 1, 3, optional = true, note = "Pays off Batman's question to the chair"),
    ),
)

private const val PX_FALL = "Emerald Twilight"
private const val PX_TIME = "Zero Hour"
private const val PX_REDEMPTION = "Redemption"

/** Hal Jordan's fall into Parallax, his attempt to remake time, and his return. */
val PARALLAX = StarterReadingOrder(
    key = "parallax_order_v1",
    name = "Green Lantern: Parallax",
    steps = listOf(
        issues(PX_FALL, "Green Lantern", 1990, 48, 50, note = "Hal destroys the Corps and becomes Parallax"),
        oneShot(PX_TIME, "Zero Hour: Crisis in Time", note = "Numbered backwards, #4 down to #0"),
        issues(PX_REDEMPTION, "The Final Night", null, 1, 4, note = "Parallax's sacrifice"),
        oneShot(PX_REDEMPTION, "Parallax: Emerald Night", optional = true),
        issues(PX_REDEMPTION, "Green Lantern: Rebirth", null, 1, 6, note = "The truth about Parallax"),
    ),
)

private const val BN_PRELUDE = "Prelude"
private const val BN_EVENT = "Blackest Night"
private const val BN_EXTRA = "Tie-ins (optional)"
private const val BN = "Blackest Night"
private const val GL_2005 = "Green Lantern"
private const val GLC_2006 = "Green Lantern Corps"

/**
 * The dead rise as Black Lanterns. The event interleaved with Green Lantern and Green Lantern
 * Corps, after Comic Book Herald / ComicBookTreasury; the Corps placement is approximate.
 */
val BLACKEST_NIGHT = StarterReadingOrder(
    key = "blackest_night_order_v1",
    name = "Blackest Night",
    steps = listOf(
        issues(BN_PRELUDE, GL_2005, 2005, 43, note = "Black Hand's origin"),
        issues(BN_PRELUDE, "Blackest Night: Tales of the Corps", null, 1, 3, optional = true),
        issues(BN_EVENT, BN, 2009, 0),
        issues(BN_EVENT, BN, 2009, 1),
        issues(BN_EVENT, GL_2005, 2005, 44),
        issues(BN_EVENT, GLC_2006, 2006, 39),
        issues(BN_EVENT, BN, 2009, 2),
        issues(BN_EVENT, GL_2005, 2005, 45, 46),
        issues(BN_EVENT, GLC_2006, 2006, 40, 41),
        issues(BN_EVENT, BN, 2009, 3),
        issues(BN_EVENT, GL_2005, 2005, 47),
        issues(BN_EVENT, GLC_2006, 2006, 42),
        issues(BN_EVENT, BN, 2009, 4),
        issues(BN_EVENT, GL_2005, 2005, 48),
        issues(BN_EVENT, GLC_2006, 2006, 43),
        issues(BN_EVENT, BN, 2009, 5),
        issues(BN_EVENT, GL_2005, 2005, 49),
        issues(BN_EVENT, GLC_2006, 2006, 44),
        issues(BN_EVENT, BN, 2009, 6),
        issues(BN_EVENT, GL_2005, 2005, 50, 51),
        issues(BN_EVENT, GLC_2006, 2006, 45),
        issues(BN_EVENT, BN, 2009, 7),
        issues(BN_EVENT, GLC_2006, 2006, 46),
        issues(BN_EVENT, GL_2005, 2005, 52),
        issues(BN_EVENT, BN, 2009, 8, note = "Finale"),
        issues(BN_EVENT, GLC_2006, 2006, 47),
        issues(BN_EXTRA, "Blackest Night: Batman", null, 1, 3, optional = true, note = "Bruce's parents rise"),
        issues("Aftermath (optional)", "Brightest Day", 2010, 0, 24, optional = true, note = "The resurrected heroes"),
    ),
)

private const val IC_PRELUDE = "Countdown"
private const val IC_EVENT = "Infinite Crisis"

/** Superboy-Prime turns murderous; the four lead-in minis are optional. */
val INFINITE_CRISIS = StarterReadingOrder(
    key = "infinite_crisis_order_v1",
    name = "Infinite Crisis",
    steps = listOf(
        oneShot(IC_PRELUDE, "Countdown to Infinite Crisis"),
        issues(IC_PRELUDE, "The OMAC Project", null, 1, 6, optional = true),
        issues(IC_PRELUDE, "Villains United", null, 1, 6, optional = true),
        issues(IC_PRELUDE, "Day of Vengeance", null, 1, 6, optional = true),
        issues(IC_PRELUDE, "Rann-Thanagar War", null, 1, 6, optional = true),
        issues(IC_EVENT, "Infinite Crisis", 2005, 1, 7, note = "Superboy-Prime breaks"),
        issues("Aftermath (optional)", "52", 2006, 1, 52, optional = true, note = "The year without Superman, Batman and Wonder Woman"),
    ),
)

private const val DC_PRELUDE = "Rebirth and The Button"
private const val DC_EVENT = "Doomsday Clock"

/** Doctor Manhattan has been rewriting the DC universe. */
val DOOMSDAY_CLOCK = StarterReadingOrder(
    key = "doomsday_clock_order_v1",
    name = "Doomsday Clock",
    steps = listOf(
        oneShot(DC_PRELUDE, "DC Universe: Rebirth", note = "The first sign of Manhattan's meddling"),
        issues(DC_PRELUDE, "Batman", 2016, 21, note = "The Button, part 1"),
        issues(DC_PRELUDE, "The Flash", 2016, 21),
        issues(DC_PRELUDE, "Batman", 2016, 22),
        issues(DC_PRELUDE, "The Flash", 2016, 22),
        issues(DC_EVENT, "Doomsday Clock", null, 1, 12),
    ),
)

// --- Dark futures and other worlds ---

val LAST_KNIGHT_ON_EARTH = StarterReadingOrder(
    key = "last_knight_order_v1",
    name = "Batman: Last Knight on Earth",
    steps = listOf(
        issues("Last Knight on Earth", "Batman: Last Knight on Earth", null, 1, 3, note = "Snyder & Capullo's final Batman story"),
    ),
)

val DARK_KNIGHTS_OF_STEEL = StarterReadingOrder(
    key = "dark_knights_of_steel_order_v1",
    name = "Dark Knights of Steel",
    steps = listOf(
        issues("Dark Knights of Steel", "Dark Knights of Steel", null, 1, 12, note = "Tom Taylor's medieval DC"),
        oneShot("Extras (optional)", "Dark Knights of Steel: Tales from the Three Kingdoms", optional = true),
        issues("Extras (optional)", "Dark Knights of Steel: Allwinter", null, 1, 6, optional = true),
    ),
)

val FLASHPOINT_BEYOND = StarterReadingOrder(
    key = "flashpoint_beyond_order_v1",
    name = "Flashpoint Beyond",
    steps = listOf(
        issues("Flashpoint Beyond", "Flashpoint Beyond", null, 0, 6, note = "Thomas Wayne investigates; read after Flashpoint"),
    ),
)

val DARK_KNIGHT_RETURNS = StarterReadingOrder(
    key = "dark_knight_returns_order_v1",
    name = "The Dark Knight Returns",
    steps = listOf(
        issues("The Dark Knight Returns", "Batman: The Dark Knight Returns", null, 1, 4, note = "Frank Miller, 1986"),
        issues("Sequels (optional)", "Batman: The Dark Knight Strikes Again", null, 1, 3, optional = true),
        issues("Sequels (optional)", "Dark Knight III: The Master Race", null, 1, 9, optional = true),
        oneShot("Sequels (optional)", "Dark Knight Returns: The Golden Child", optional = true),
    ),
)

// --- Watchmen ---

private const val BW = "Prequels: Before Watchmen (optional)"

/** Moore & Gibbons's Watchmen; Doomsday Clock (its own order) is the sequel. */
val WATCHMEN = StarterReadingOrder(
    key = "watchmen_order_v1",
    name = "Watchmen",
    steps = listOf(
        issues("Watchmen", "Watchmen", 1986, 1, 12, note = "Read before Doomsday Clock"),
        issues(BW, "Before Watchmen: Minutemen", null, 1, 6, optional = true),
        issues(BW, "Before Watchmen: Comedian", null, 1, 6, optional = true),
        issues(BW, "Before Watchmen: Rorschach", null, 1, 4, optional = true),
        issues(BW, "Before Watchmen: Nite Owl", null, 1, 4, optional = true),
        issues(BW, "Before Watchmen: Silk Spectre", null, 1, 4, optional = true),
        issues(BW, "Before Watchmen: Ozymandias", null, 1, 6, optional = true),
        issues(BW, "Before Watchmen: Dr. Manhattan", null, 1, 4, optional = true),
        issues(BW, "Before Watchmen: Moloch", null, 1, 2, optional = true),
        issues("Sequels", "Rorschach", 2020, 1, 12, optional = true, note = "Tom King's follow-up"),
    ),
)

// --- Sinestro Corps War ---

private const val SCW = "The Sinestro Corps War"
private const val SCW_TALES = "Tales of the Sinestro Corps (optional)"
private const val SCW_AFTER = "Road to Blackest Night"

/**
 * Parallax and Superboy-Prime return, now fear-powered. Alternates Green Lantern and Green Lantern
 * Corps per Comic Book Herald; the aftermath arcs lead into the Blackest Night order.
 */
val SINESTRO_CORPS_WAR = StarterReadingOrder(
    key = "sinestro_corps_war_order_v1",
    name = "Sinestro Corps War",
    steps = listOf(
        oneShot(SCW, "Green Lantern: Sinestro Corps Special"),
        issues(SCW, GL_2005, 2005, 21),
        issues(SCW, GLC_2006, 2006, 14),
        issues(SCW, GL_2005, 2005, 22),
        issues(SCW, GLC_2006, 2006, 15),
        issues(SCW, GL_2005, 2005, 23),
        issues(SCW, GLC_2006, 2006, 16),
        issues(SCW, GL_2005, 2005, 24),
        issues(SCW, GLC_2006, 2006, 17, 18),
        issues(SCW, GL_2005, 2005, 25, note = "Finale"),
        issues(SCW, GLC_2006, 2006, 19, note = "Epilogue"),
        oneShot(SCW_TALES, "Tales of the Sinestro Corps: Parallax", optional = true),
        oneShot(SCW_TALES, "Tales of the Sinestro Corps: Cyborg-Superman", optional = true),
        oneShot(SCW_TALES, "Tales of the Sinestro Corps: Superman-Prime", optional = true),
        oneShot(SCW_TALES, "Tales of the Sinestro Corps: Ion", optional = true),
        issues(SCW_AFTER, GL_2005, 2005, 29, 35, optional = true, note = "Secret Origin, a flashback"),
        issues(SCW_AFTER, GL_2005, 2005, 36, 38, note = "Rage of the Red Lanterns"),
        issues(SCW_AFTER, GL_2005, 2005, 39, 42, note = "Agent Orange; continues in Blackest Night"),
    ),
)

// --- Final Crisis ---

private const val FC_PRELUDE = "Prelude"
private const val FC_EVENT = "Final Crisis"
private const val FC_AFTER = "Aftermath"

/** Darkseid wins: Earth falls to the Anti-Life Equation. Grant Morrison, 2008. */
val FINAL_CRISIS = StarterReadingOrder(
    key = "final_crisis_order_v1",
    name = "Final Crisis",
    steps = listOf(
        issues(FC_PRELUDE, "DC Universe", 2008, 0),
        issues(FC_PRELUDE, BATMAN_1940, 1940, 676, 681, optional = true, note = "Batman R.I.P."),
        issues(FC_EVENT, "Final Crisis", null, 1, 3),
        issues(FC_EVENT, "Final Crisis: Superman Beyond", null, 1, 2),
        issues(FC_EVENT, BATMAN_1940, 1940, 682, 683, optional = true, note = "Last Rites"),
        oneShot(FC_EVENT, "Final Crisis: Submit", optional = true),
        issues(FC_EVENT, "Final Crisis", null, 4, 7, note = "Darkseid's victory and fall"),
        issues(FC_AFTER, "Batman: The Return of Bruce Wayne", null, 1, 6, note = "Bruce fights his way back through time"),
    ),
)

// --- The Joker ---

/** The Joker at his worst: his possible origin, Azzarello & Bermejo's Joker, and its sequel. */
val KILLING_JOKE = StarterReadingOrder(
    key = "killing_joke_order_v1",
    name = "The Killing Joke & Joker",
    steps = listOf(
        oneShot("The Joker", "Batman: The Killing Joke", note = "Moore & Bolland, 1988"),
        oneShot("The Joker", "Joker", year = 2008, note = "Azzarello & Bermejo graphic novel"),
        issues("Aftermath", "Batman: Damned", null, 1, 3, note = "Azzarello & Bermejo's follow-up; horror"),
    ),
)

// --- Tower of Babel ---

/** Ra's al Ghul uses Batman's contingency plans against the League; Identity Crisis deepens the distrust. */
val TOWER_OF_BABEL = StarterReadingOrder(
    key = "tower_of_babel_order_v1",
    name = "JLA: Tower of Babel",
    steps = listOf(
        issues("Tower of Babel", "JLA", 1997, 43, 46, note = "Batman's plans for taking down the League"),
        issues("Aftermath", "JLA", 1997, 47, optional = true),
        issues("Aftermath", "Identity Crisis", null, 1, 7, note = "The League's secret mind-wipes; leads into Infinite Crisis"),
    ),
)

// --- The Black Mirror ---

/** Snyder's darkest Batman story, starring Dick Grayson, before the New 52. */
val BLACK_MIRROR = StarterReadingOrder(
    key = "black_mirror_order_v1",
    name = "Batman: The Black Mirror",
    steps = listOf(
        issues("The Black Mirror", DETECTIVE_1937, 1937, 871, 881),
        issues("Follow-up (optional)", "Batman: Gates of Gotham", null, 1, 5, optional = true),
    ),
)

// --- Crisis on Infinite Earths ---

/** The original multiverse-ending event, which Infinite Crisis and Death Metal both look back on. */
val CRISIS_ON_INFINITE_EARTHS = StarterReadingOrder(
    key = "crisis_order_v1",
    name = "Crisis on Infinite Earths",
    steps = listOf(
        issues("Crisis on Infinite Earths", "Crisis on Infinite Earths", 1985, 1, 12),
        issues("Aftermath (optional)", "History of the DC Universe", null, 1, 2, optional = true),
    ),
)
