package dev.jiaowh.comicreader.data

// --- Hickman's Avengers to Secret Wars ---

private const val HA_START = "Avengers World"
private const val HA_INFINITY = "Infinity"
private const val HA_AFTER = "The Illuminati's last choices"
private const val HA_TIME_RUNS_OUT = "Time Runs Out"
private const val HA_SECRET_WARS = "Secret Wars"

private const val AVENGERS_2012 = "Avengers"
private const val NEW_AVENGERS_2013 = "New Avengers"
private const val INFINITY_2013 = "Infinity"

/**
 * Jonathan Hickman's Avengers and New Avengers, interleaved with Infinity, through Secret Wars
 * (2015). This is where the original Ultimate universe ends and the Maker comes from. Order follows
 * How To Love Comics / ComicBookTreasury; Time Runs Out alternates the two titles by release.
 */
val HICKMAN_AVENGERS = StarterReadingOrder(
    key = "hickman_avengers_order_v1",
    name = "Hickman's Avengers & Secret Wars",
    steps = buildList {
        add(issues(HA_START, NEW_AVENGERS_2013, 2013, 1, 3, note = "The Illuminati and the first Incursion"))
        add(issues(HA_START, AVENGERS_2012, 2012, 1, 4))
        add(issues(HA_START, NEW_AVENGERS_2013, 2013, 4))
        add(issues(HA_START, AVENGERS_2012, 2012, 5))
        add(issues(HA_START, NEW_AVENGERS_2013, 2013, 5, 6))
        add(issues(HA_START, AVENGERS_2012, 2012, 6, 13))
        add(issues(HA_START, NEW_AVENGERS_2013, 2013, 7))
        add(issues(HA_START, AVENGERS_2012, 2012, 14, 17))

        add(issues(HA_INFINITY, NEW_AVENGERS_2013, 2013, 8))
        add(issues(HA_INFINITY, INFINITY_2013, 2013, 1))
        add(issues(HA_INFINITY, AVENGERS_2012, 2012, 18))
        add(issues(HA_INFINITY, NEW_AVENGERS_2013, 2013, 9))
        add(issues(HA_INFINITY, INFINITY_2013, 2013, 2))
        add(issues(HA_INFINITY, AVENGERS_2012, 2012, 19))
        add(issues(HA_INFINITY, NEW_AVENGERS_2013, 2013, 10))
        add(issues(HA_INFINITY, INFINITY_2013, 2013, 3))
        add(issues(HA_INFINITY, AVENGERS_2012, 2012, 20))
        add(issues(HA_INFINITY, INFINITY_2013, 2013, 4))
        add(issues(HA_INFINITY, AVENGERS_2012, 2012, 21))
        add(issues(HA_INFINITY, NEW_AVENGERS_2013, 2013, 11))
        add(issues(HA_INFINITY, INFINITY_2013, 2013, 5))
        add(issues(HA_INFINITY, AVENGERS_2012, 2012, 22, 23))
        add(issues(HA_INFINITY, INFINITY_2013, 2013, 6))
        add(issues(HA_INFINITY, NEW_AVENGERS_2013, 2013, 12))

        add(issues(HA_AFTER, NEW_AVENGERS_2013, 2013, 13, 15))
        add(issues(HA_AFTER, AVENGERS_2012, 2012, 24, 28))
        add(issues(HA_AFTER, NEW_AVENGERS_2013, 2013, 16, 17))
        add(issues(HA_AFTER, AVENGERS_2012, 2012, 29, 34))
        add(issues(HA_AFTER, NEW_AVENGERS_2013, 2013, 18, 23))

        // Time Runs Out: the titles alternate, ending on Avengers #44.
        for (offset in 0..8) {
            add(issues(HA_TIME_RUNS_OUT, AVENGERS_2012, 2012, 35 + offset))
            add(issues(HA_TIME_RUNS_OUT, NEW_AVENGERS_2013, 2013, 24 + offset))
        }
        add(issues(HA_TIME_RUNS_OUT, NEW_AVENGERS_2013, 2013, 33))
        add(issues(HA_TIME_RUNS_OUT, AVENGERS_2012, 2012, 44, note = "The last Incursion"))

        add(issues(HA_SECRET_WARS, "Secret Wars", 2015, 1, 9, note = "The multiverse ends; Doom rebuilds it"))
    },
)

// --- The original Ultimate universe, highlights ---

private const val OU_BEGINNINGS = "Beginnings (2000–04)"
private const val OU_PEAK = "The peak years"
private const val OU_ULTIMATUM = "Ultimatum and the Death of Spider-Man"
private const val OU_MILES = "Miles Morales"

private const val USM_2000 = "Ultimate Spider-Man"
private const val UXM_2001 = "Ultimate X-Men"
private const val UCSM = "Ultimate Comics Spider-Man"

/**
 * The best of Marvel's original Ultimate line (Earth-1610), the world the Maker came from.
 * Picks follow Comic Book Herald's highlights: Bendis's Ultimate Spider-Man, Millar's Ultimate
 * X-Men and The Ultimates, Ellis's Galactus trilogy, then Ultimatum through Miles Morales up to
 * Secret Wars (see "Hickman's Avengers & Secret Wars").
 */
val ORIGINAL_ULTIMATE = StarterReadingOrder(
    key = "original_ultimate_order_v1",
    name = "Original Ultimate Universe",
    steps = listOf(
        issues(OU_BEGINNINGS, USM_2000, 2000, 1, 13, note = "Bendis & Bagley; Peter's origin"),
        issues(OU_BEGINNINGS, UXM_2001, 2001, 1, 12, note = "Millar's run begins"),
        issues(OU_BEGINNINGS, USM_2000, 2000, 14, 32),
        issues(OU_BEGINNINGS, "The Ultimates", 2002, 1, 13, note = "Millar & Hitch; the blueprint for the MCU"),
        issues(OU_BEGINNINGS, UXM_2001, 2001, 13, 33),
        issues(OU_BEGINNINGS, "Ultimate Fantastic Four", 2004, 1, 6),

        issues(OU_PEAK, USM_2000, 2000, 33, 59, note = "Venom, Carnage, and more"),
        issues(OU_PEAK, "The Ultimates 2", 2005, 1, 13),
        issues(OU_PEAK, "Ultimate Nightmare", null, 1, 5, note = "Galactus trilogy, part 1"),
        issues(OU_PEAK, "Ultimate Secret", null, 1, 4, note = "Galactus trilogy, part 2"),
        issues(OU_PEAK, "Ultimate Extinction", null, 1, 5, note = "Galactus trilogy, part 3"),
        issues(OU_PEAK, "Ultimate Fantastic Four", 2004, 21, 23, optional = true, note = "Introduces the Marvel Zombies"),
        issues(OU_PEAK, USM_2000, 2000, 60, 111, note = "The end of the Bendis/Bagley era"),

        issues(OU_ULTIMATUM, USM_2000, 2000, 112, 133),
        issues(OU_ULTIMATUM, "Ultimatum", null, 1, 5, note = "Infamous, but everything after builds on it"),
        issues(OU_ULTIMATUM, UCSM, 2009, 1, 15),
        issues(OU_ULTIMATUM, UCSM, 2009, 150, 160, note = "The Death of Spider-Man"),
        issues(OU_ULTIMATUM, "Ultimate Fallout", null, 1, 6, note = "#4 introduces Miles Morales"),

        issues(OU_MILES, UCSM, 2011, 1, 12, note = "Miles Morales becomes Spider-Man"),
        issues(OU_MILES, "Ultimate Comics Ultimates", null, 1, 12, optional = true, note = "Hickman's run"),
        issues(OU_MILES, "Spider-Men", 2012, 1, 5, note = "Miles meets the main universe's Peter"),
        issues(OU_MILES, UCSM, 2011, 13, 28),
        issues(OU_MILES, "Cataclysm: The Ultimates' Last Stand", null, 1, 5),
        issues(OU_MILES, "Miles Morales: Ultimate Spider-Man", null, 1, 12, note = "Continues in Secret Wars (2015)"),
    ),
)

// --- Spider-Verse (2014) ---

private const val SV_PRELUDE = "Prelude"
private const val SV_EVENT = "Spider-Verse"
private const val ASM_2014 = "The Amazing Spider-Man"
private const val SV_TEAM_UP = "Spider-Verse Team-Up"
private const val SCARLET_SPIDERS = "Scarlet Spiders"
private const val SPIDER_WOMAN_2014 = "Spider-Woman"
private const val SPIDER_MAN_2099_2014 = "Spider-Man 2099"

/**
 * Every Spider-Man in the multiverse against the Inheritors. Order follows How To Love Comics'
 * checklist; side series that only fill in the edges are optional.
 */
val SPIDER_VERSE = StarterReadingOrder(
    key = "spider_verse_order_v1",
    name = "Spider-Verse",
    steps = listOf(
        issues(SV_PRELUDE, "Superior Spider-Man", 2013, 32, 33, note = "Otto recruits a Spider-Army"),
        issues(SV_PRELUDE, "Edge of Spider-Verse", 2014, 1, 5, note = "Spider-variants, incl. Spider-Gwen's debut"),
        issues(SV_PRELUDE, ASM_2014, 2014, 7, 8),

        issues(SV_EVENT, ASM_2014, 2014, 9),
        issues(SV_EVENT, SV_TEAM_UP, null, 1, optional = true),
        issues(SV_EVENT, "Spider-Verse", 2014, 1, 2, note = "Variant anthology"),
        issues(SV_EVENT, ASM_2014, 2014, 10),
        issues(SV_EVENT, SPIDER_WOMAN_2014, 2014, 1, optional = true),
        issues(SV_EVENT, SCARLET_SPIDERS, null, 1, optional = true),
        issues(SV_EVENT, SPIDER_MAN_2099_2014, 2014, 6, optional = true),
        issues(SV_EVENT, ASM_2014, 2014, 11),
        issues(SV_EVENT, SV_TEAM_UP, null, 2, optional = true),
        issues(SV_EVENT, SCARLET_SPIDERS, null, 2, optional = true),
        issues(SV_EVENT, SPIDER_WOMAN_2014, 2014, 2, optional = true),
        issues(SV_EVENT, ASM_2014, 2014, 12),
        issues(SV_EVENT, SPIDER_MAN_2099_2014, 2014, 7, optional = true),
        issues(SV_EVENT, SV_TEAM_UP, null, 3, optional = true),
        issues(SV_EVENT, SCARLET_SPIDERS, null, 3, optional = true),
        issues(SV_EVENT, SPIDER_WOMAN_2014, 2014, 3, 4, optional = true),
        issues(SV_EVENT, ASM_2014, 2014, 13, 14, note = "The final battle"),
        issues(SV_EVENT, SPIDER_MAN_2099_2014, 2014, 8, optional = true),
        issues(SV_EVENT, ASM_2014, 2014, 15, note = "Epilogue"),
    ),
)

// --- Spider-Verse sequels ---

private const val SG_PRELUDE = "Spider-Geddon: Prelude"
private const val SG_EVENT = "Spider-Geddon (2018)"
private const val ESV = "End of the Spider-Verse (2022)"

private const val SPIDER_GEDDON = "Spider-Geddon"
private const val SPECTACULAR = "Peter Parker: The Spectacular Spider-Man"
private const val GHOST_SPIDER = "Spider-Gwen: Ghost-Spider"
private const val SPIDER_GIRLS = "Spider-Girls"
private const val SPIDER_FORCE = "Spider-Force"
private const val VAULT = "Vault of Spiders"

/**
 * The two follow-ups to Spider-Verse: Spider-Geddon (How To Love Comics' issue order; side
 * series optional) and End of the Spider-Verse, which closes the Inheritors saga.
 */
val SPIDER_VERSE_SEQUELS = StarterReadingOrder(
    key = "spider_verse_sequels_order_v1",
    name = "Spider-Verse sequels",
    steps = listOf(
        issues(SG_PRELUDE, "Edge of Spider-Geddon", null, 1, 4, note = "More spider-variants"),
        issues(SG_PRELUDE, SPIDER_GEDDON, 2018, 0),
        issues(SG_PRELUDE, "Superior Octopus", null, 1),

        issues(SG_EVENT, SPIDER_GEDDON, 2018, 1),
        issues(SG_EVENT, SPECTACULAR, 2017, 311, 312, optional = true),
        issues(SG_EVENT, SPIDER_GEDDON, 2018, 2),
        issues(SG_EVENT, GHOST_SPIDER, null, 1, optional = true),
        issues(SG_EVENT, SPIDER_GIRLS, null, 1, optional = true),
        issues(SG_EVENT, SPIDER_FORCE, null, 1, optional = true),
        issues(SG_EVENT, VAULT, null, 1, optional = true),
        issues(SG_EVENT, SPIDER_GEDDON, 2018, 3),
        issues(SG_EVENT, VAULT, null, 2, optional = true),
        issues(SG_EVENT, SPIDER_FORCE, null, 2, optional = true),
        issues(SG_EVENT, SPIDER_GEDDON, 2018, 4),
        issues(SG_EVENT, GHOST_SPIDER, null, 2, 3, optional = true),
        issues(SG_EVENT, SPIDER_GIRLS, null, 2, 3, optional = true),
        issues(SG_EVENT, SPIDER_FORCE, null, 3, optional = true),
        issues(SG_EVENT, SPECTACULAR, 2017, 313, optional = true),
        issues(SG_EVENT, SPIDER_GEDDON, 2018, 5, note = "Finale"),

        issues(ESV, "Edge of Spider-Verse", 2022, 1, 5, note = "Prelude anthology"),
        issues(ESV, "Spider-Man", 2022, 1, 7, note = "Slott & Bagley; the end of the Inheritors saga"),
    ),
)

// --- Superior Spider-Man ---

private const val SSM_DYING_WISH = "Dying Wish"
private const val SSM_SUPERIOR = "The Superior Spider-Man"
private const val SSM_GOBLIN_NATION = "Goblin Nation"
private const val SSM = "Superior Spider-Man"

/**
 * Doctor Octopus steals Peter's body and becomes a "better" Spider-Man (Dan Slott, 2013–14).
 * Arcs as collected; issues #32–33 are in the Spider-Verse order.
 */
val SUPERIOR_SPIDER_MAN = StarterReadingOrder(
    key = "superior_spider_man_order_v1",
    name = "Superior Spider-Man",
    steps = listOf(
        issues(SSM_DYING_WISH, "The Amazing Spider-Man", 1999, 698, 700, note = "Otto swaps bodies with Peter"),

        issues(SSM_SUPERIOR, SSM, 2013, 1, 5, note = "My Own Worst Enemy"),
        issues(SSM_SUPERIOR, SSM, 2013, 6, 10, note = "A Troubled Mind"),
        issues(SSM_SUPERIOR, "Superior Spider-Man Team-Up", null, 1, 12, optional = true),
        issues(SSM_SUPERIOR, SSM, 2013, 11, 16, note = "No Escape"),
        issues(SSM_SUPERIOR, SSM, 2013, 17, 21, note = "Necessary Evil"),
        issues(SSM_SUPERIOR, SSM, 2013, 22, 26, note = "Superior Venom"),
        issues(SSM_SUPERIOR, "Superior Spider-Man Annual", 2013, 1),

        issues(SSM_GOBLIN_NATION, SSM, 2013, 27, 31, note = "Finale"),
        issues(SSM_GOBLIN_NATION, "Superior Spider-Man Annual", 2013, 2, optional = true),
        issues("Sequel (optional)", SSM, 2018, 1, 12, optional = true, note = "Otto returns as the Superior Spider-Man"),
    ),
)

// --- Future tyrants: Thanos Wins, the Maestro, Old Man Logan ---

/** Donny Cates's Thanos #13–18: the future where Thanos won, and the Cosmic Ghost Rider. */
val THANOS_WINS = StarterReadingOrder(
    key = "thanos_wins_order_v1",
    name = "Thanos Wins",
    steps = listOf(
        issues("Thanos Wins", "Thanos", 2016, 13, 18, note = "King Thanos and the Cosmic Ghost Rider"),
        oneShot("Thanos Wins", "Thanos Annual", year = 2018, optional = true),
        issues("Follow-up (optional)", "Cosmic Ghost Rider", 2018, 1, 5, optional = true, note = "Frank Castle's own story"),
        issues("Follow-up (optional)", "Cosmic Ghost Rider Destroys Marvel History", null, 1, 6, optional = true),
    ),
)

/**
 * The Maestro, a future Hulk turned tyrant: the 1992 original, then Peter David's 2020s prequel
 * trilogy showing how he got there.
 */
val FUTURE_IMPERFECT = StarterReadingOrder(
    key = "future_imperfect_order_v1",
    name = "Hulk: Future Imperfect",
    steps = listOf(
        issues("The original", "Hulk: Future Imperfect", null, 1, 2, note = "Peter David & George Pérez"),
        issues("The Maestro trilogy (prequels)", "Maestro", 2020, 1, 5, note = "How Banner becomes the Maestro"),
        issues("The Maestro trilogy (prequels)", "Maestro: War and Pax", null, 1, 5),
        issues("The Maestro trilogy (prequels)", "Maestro: World War M", null, 1, 5),
    ),
)

/** Mark Millar & Steve McNiven's wasteland future where the villains won. */
val OLD_MAN_LOGAN = StarterReadingOrder(
    key = "old_man_logan_order_v1",
    name = "Old Man Logan",
    steps = listOf(
        issues("Old Man Logan", "Wolverine", 2003, 66, 72),
        oneShot("Old Man Logan", "Wolverine: Old Man Logan Giant-Size", note = "The finale"),
        issues("Sequels", "Old Man Logan", 2016, 1, 50, optional = true, note = "Logan stranded in the present"),
        issues("Sequels", "Dead Man Logan", null, 1, 12, note = "The end of Old Man Logan"),
    ),
)

// --- Heroes made gods ---

private const val AVX = "Avengers vs. X-Men"

/** The Phoenix Force splits between five X-Men, who start remaking the world as gods. */
val AVENGERS_VS_X_MEN = StarterReadingOrder(
    key = "avx_order_v1",
    name = "Avengers vs. X-Men",
    steps = listOf(
        issues("Prelude", "Avengers: X-Sanction", null, 1, 4, optional = true),
        issues("Round 1", AVX, null, 0, 3, note = "The Phoenix is coming"),
        issues("The Phoenix Five", AVX, null, 4, 6, note = "Cyclops, Emma, Colossus, Magik and Namor become gods"),
        issues("Dark Phoenix", AVX, null, 7, 12),
        issues("Extras (optional)", "AvX: Vs", null, 1, 6, optional = true, note = "The fights in full"),
        issues("Extras (optional)", "AvX: Consequences", null, 1, 5, optional = true),
    ),
)

/** The original corrupted god: Jean Grey's Phoenix turns dark (Claremont & Byrne). */
val DARK_PHOENIX = StarterReadingOrder(
    key = "dark_phoenix_order_v1",
    name = "The Dark Phoenix Saga",
    steps = listOf(
        issues("The Phoenix Saga (optional)", "Uncanny X-Men", 1963, 101, 108, optional = true, note = "Jean becomes Phoenix"),
        issues("The Dark Phoenix Saga", "Uncanny X-Men", 1963, 129, 137),
        issues("Sequels (optional)", "X-Men: Phoenix - Endsong", null, 1, 5, optional = true),
        issues("Sequels (optional)", "X-Men: Phoenix - Warsong", null, 1, 5, optional = true),
    ),
)

/** Thanos gathers the gems and becomes omnipotent; then Nebula, then Adam Warlock. */
val INFINITY_GAUNTLET = StarterReadingOrder(
    key = "infinity_gauntlet_order_v1",
    name = "The Infinity Gauntlet",
    steps = listOf(
        issues("Prelude", "Silver Surfer", 1987, 44, 50, optional = true, note = "Thanos returns"),
        issues("Prelude", "Thanos Quest", null, 1, 2, note = "Thanos collects the gems"),
        issues("The Infinity Gauntlet", "The Infinity Gauntlet", null, 1, 6),
        issues("Sequels (optional)", "Infinity War", 1992, 1, 6, optional = true),
        issues("Sequels (optional)", "Infinity Crusade", null, 1, 6, optional = true),
    ),
)

// --- Heroes turned evil ---

private const val SE_HAIL_HYDRA = "Hail Hydra"
private const val SE_EVENT = "Secret Empire"
private const val CAP_SR = "Captain America: Steve Rogers"
private const val SECRET_EMPIRE_2017 = "Secret Empire"

/** Captain America was a Hydra agent all along. Steve Rogers tie-ins beyond "Hail Hydra" are optional. */
val SECRET_EMPIRE = StarterReadingOrder(
    key = "secret_empire_order_v1",
    name = "Secret Empire",
    steps = listOf(
        issues(SE_HAIL_HYDRA, CAP_SR, 2016, 1, 6, note = "\"Hail Hydra\""),
        issues(SE_HAIL_HYDRA, CAP_SR, 2016, 7, 14, optional = true),
        issues(SE_EVENT, SECRET_EMPIRE_2017, 2017, 0),
        issues(SE_EVENT, SECRET_EMPIRE_2017, 2017, 1),
        oneShot(SE_EVENT, "Free Comic Book Day 2017: Secret Empire", optional = true),
        issues(SE_EVENT, SECRET_EMPIRE_2017, 2017, 2, 5),
        issues(SE_EVENT, CAP_SR, 2016, 15, 16, optional = true),
        issues(SE_EVENT, SECRET_EMPIRE_2017, 2017, 6, 10),
        issues(SE_EVENT, CAP_SR, 2016, 17, 19, optional = true),
        oneShot(SE_EVENT, "Secret Empire: Omega", note = "Epilogue"),
    ),
)

/** A spell inverts everyone: heroes turn villainous, villains turn heroic. */
val AXIS = StarterReadingOrder(
    key = "axis_order_v1",
    name = "AXIS",
    steps = listOf(
        issues("AXIS", "Avengers & X-Men: AXIS", null, 1, 9),
        issues("Fallout (optional)", "Superior Iron Man", null, 1, 9, optional = true, note = "Inverted, smug, evil Tony Stark"),
    ),
)

/** Symbiote-bonded versions of every hero. */
val VENOMVERSE = StarterReadingOrder(
    key = "venomverse_order_v1",
    name = "Venomverse",
    steps = listOf(
        issues("Venomverse", "Edge of Venomverse", null, 1, 5, note = "Variant origins"),
        issues("Venomverse", "Venomverse", null, 1, 5),
        issues("Sequel (optional)", "Venomized", null, 1, 5, optional = true),
    ),
)

// --- Other worlds and horror ---

/** Neil Gaiman's Marvel heroes in Elizabethan England, Marvel's answer to Elseworlds. */
val MARVEL_1602 = StarterReadingOrder(
    key = "marvel_1602_order_v1",
    name = "Marvel 1602",
    steps = listOf(
        issues("Marvel 1602", "Marvel 1602", null, 1, 8),
        issues("Sequels (optional)", "Marvel 1602: New World", null, 1, 5, optional = true),
        issues("Sequels (optional)", "Marvel 1602: Fantastick Four", null, 1, 5, optional = true),
    ),
)

/** Al Ewing's horror Hulk. */
val IMMORTAL_HULK = StarterReadingOrder(
    key = "immortal_hulk_order_v1",
    name = "The Immortal Hulk",
    steps = listOf(
        issues("Immortal Hulk", "Immortal Hulk", 2018, 1, 10),
        issues("Immortal Hulk", "Immortal Hulk", 2018, 11, 20),
        issues("Immortal Hulk", "Immortal Hulk", 2018, 21, 30),
        issues("Immortal Hulk", "Immortal Hulk", 2018, 31, 40),
        issues("Immortal Hulk", "Immortal Hulk", 2018, 41, 50, note = "Finale"),
    ),
)

// --- Beyond DC and Marvel ---

/** Mark Waid's Superman-style hero turned the world's worst villain (BOOM! Studios). */
val IRREDEEMABLE = StarterReadingOrder(
    key = "irredeemable_order_v1",
    name = "Irredeemable",
    steps = listOf(
        issues("Irredeemable", "Irredeemable", 2009, 1, 12, note = "The Plutonian snaps"),
        issues("Irredeemable", "Irredeemable", 2009, 13, 24),
        issues("Irredeemable", "Irredeemable", 2009, 25, 37, note = "Finale"),
        issues("Companion (optional)", "Incorruptible", null, 1, 30, optional = true, note = "The reverse: a villain turns hero"),
    ),
)

/** Robert Kirkman's Invincible (Image); issue #7 is the twist. */
val INVINCIBLE = StarterReadingOrder(
    key = "invincible_order_v1",
    name = "Invincible",
    steps = (0 until 12).map { block ->
        val first = block * 12 + 1
        issues(
            "Invincible", "Invincible", 2003, first, first + 11,
            note = when (block) {
                0 -> "#7 is the twist"
                11 -> "Finale"
                else -> null
            },
        )
    },
)

private const val BOYS = "The Boys"

/** Garth Ennis's The Boys (Dynamite), with each spin-off where it belongs and the Dear Becky epilogue. */
val THE_BOYS = StarterReadingOrder(
    key = "the_boys_order_v1",
    name = "The Boys",
    steps = listOf(
        issues(BOYS, BOYS, 2006, 1, 14),
        issues(BOYS, BOYS, 2006, 15, 30),
        issues(BOYS, "The Boys: Herogasm", null, 1, 6, note = "Read before #31"),
        issues(BOYS, BOYS, 2006, 31, 47),
        issues(BOYS, "The Boys: Highland Laddie", null, 1, 6, note = "Read before #48"),
        issues(BOYS, BOYS, 2006, 48, 59),
        issues(BOYS, "The Boys: Butcher, Baker, Candlestickmaker", null, 1, 6, note = "Butcher's origin"),
        issues(BOYS, BOYS, 2006, 60, 72, note = "Finale"),
        issues("Epilogue", "The Boys: Dear Becky", null, 1, 8),
    ),
)

// --- Avengers Disassembled and House of M ---

/** The Scarlet Witch breaks, then rewrites reality; her redemption comes in The Children's Crusade. */
val HOUSE_OF_M = StarterReadingOrder(
    key = "house_of_m_order_v1",
    name = "House of M",
    steps = listOf(
        issues("Avengers Disassembled", "Avengers", 1998, 500, 503, note = "The Scarlet Witch tears the Avengers apart"),
        oneShot("Avengers Disassembled", "Avengers Finale"),
        issues("Aftermath (optional)", "New Avengers", 2005, 1, 6, optional = true, note = "Breakout"),
        issues("House of M", "House of M", 2005, 1, 8, note = "\"No more mutants\""),
        issues("Aftermath (optional)", "Son of M", null, 1, 6, optional = true),
        issues("Sequel", "Avengers: The Children's Crusade", null, 1, 9, note = "The Scarlet Witch returns"),
    ),
)

// --- Venom by Donny Cates ---

private const val VC_RISE = "Venom: Rex and the Abyss"
private const val VC_CARNAGE = "Absolute Carnage"
private const val VC_KNULL = "King in Black"
private const val VENOM_2018 = "Venom"
private const val ABSOLUTE_CARNAGE = "Absolute Carnage"
private const val KING_IN_BLACK = "King in Black"

/**
 * Cates's Venom through Absolute Carnage and King in Black (ComicBookTreasury's order); the
 * event interleaving follows release order.
 */
val VENOM_CATES = StarterReadingOrder(
    key = "venom_cates_order_v1",
    name = "Venom: Absolute Carnage & King in Black",
    steps = listOf(
        issues(VC_RISE, VENOM_2018, 2018, 1, 6, note = "Knull, the god of the symbiotes"),
        oneShot(VC_RISE, "Web of Venom: Ve'Nam", optional = true),
        issues(VC_RISE, "Venom Annual", 2018, 1, optional = true),
        issues(VC_RISE, VENOM_2018, 2018, 7, 15),

        oneShot(VC_CARNAGE, "Web of Venom: Carnage Born", note = "Carnage returns"),
        issues(VC_CARNAGE, ABSOLUTE_CARNAGE, 2019, 1),
        issues(VC_CARNAGE, VENOM_2018, 2018, 16),
        issues(VC_CARNAGE, ABSOLUTE_CARNAGE, 2019, 2),
        issues(VC_CARNAGE, VENOM_2018, 2018, 17),
        issues(VC_CARNAGE, ABSOLUTE_CARNAGE, 2019, 3),
        issues(VC_CARNAGE, VENOM_2018, 2018, 18),
        issues(VC_CARNAGE, ABSOLUTE_CARNAGE, 2019, 4),
        issues(VC_CARNAGE, VENOM_2018, 2018, 19),
        issues(VC_CARNAGE, ABSOLUTE_CARNAGE, 2019, 5),
        issues(VC_CARNAGE, VENOM_2018, 2018, 20),
        issues(VC_CARNAGE, VENOM_2018, 2018, 21, 30, note = "Venom Beyond and Venom Island"),

        issues(VC_KNULL, KING_IN_BLACK, 2020, 1, note = "Knull arrives"),
        issues(VC_KNULL, VENOM_2018, 2018, 31, 32),
        issues(VC_KNULL, KING_IN_BLACK, 2020, 2),
        issues(VC_KNULL, VENOM_2018, 2018, 33),
        issues(VC_KNULL, KING_IN_BLACK, 2020, 3),
        issues(VC_KNULL, VENOM_2018, 2018, 34),
        issues(VC_KNULL, KING_IN_BLACK, 2020, 4, 5),
        issues(VC_KNULL, VENOM_2018, 2018, 35, note = "Venom #200; the end of Cates's run"),
    ),
)

// --- Other Marvel dark turns ---

/** Kaare Andrews's Marvel answer to The Dark Knight Returns. */
val SPIDER_MAN_REIGN = StarterReadingOrder(
    key = "spider_man_reign_order_v1",
    name = "Spider-Man: Reign",
    steps = listOf(issues("Reign", "Spider-Man: Reign", null, 1, 4, note = "An old, broken Peter in a dystopian New York")),
)

/** The heroes exile the Hulk; he comes back a king. */
val PLANET_HULK = StarterReadingOrder(
    key = "planet_hulk_order_v1",
    name = "Planet Hulk & World War Hulk",
    steps = listOf(
        oneShot("Prelude", "New Avengers: Illuminati", year = 2006, optional = true, note = "Why the Illuminati exile the Hulk"),
        issues("Planet Hulk", "Incredible Hulk", 2000, 92, 105, note = "Gladiator, then king"),
        issues("Planet Hulk", "Giant-Size Hulk", 2006, 1, optional = true),
        issues("World War Hulk", "World War Hulk", null, 1, 5, note = "The Hulk returns for revenge"),
        oneShot("Aftermath (optional)", "World War Hulk: Aftersmash", optional = true),
        issues("Aftermath (optional)", "Skaar: Son of Hulk", null, 1, 12, optional = true),
    ),
)

/** The undead Marvel universe, from its Ultimate Fantastic Four debut through its sequels. */
val MARVEL_ZOMBIES = StarterReadingOrder(
    key = "marvel_zombies_order_v1",
    name = "Marvel Zombies",
    steps = listOf(
        issues("Introduction", "Ultimate Fantastic Four", 2004, 21, 23, note = "The Fantastic Four meet their zombie selves"),
        oneShot("Marvel Zombies", "Marvel Zombies: Dead Days", note = "Prequel: how it started"),
        issues("Marvel Zombies", "Marvel Zombies", 2005, 1, 5),
        issues("Marvel Zombies", "Ultimate Fantastic Four", 2004, 30, 32, note = "Zombie Reed's return"),
        issues("Sequels", "Marvel Zombies 2", null, 1, 5),
        issues("Sequels", "Marvel Zombies Return", null, 1, 5),
        issues("Sequels (optional)", "Marvel Zombies 3", null, 1, 4, optional = true),
        issues("Sequels (optional)", "Marvel Zombies 4", null, 1, 4, optional = true),
    ),
)

// --- Hickman's X-Men ---

private const val HOX = "House of X"
private const val POX = "Powers of X"

/**
 * House of X / Powers of X in their printed interleaving, then Hickman's X-Men through Inferno
 * (Comic Book Herald's Hickman-only order). The X of Swords crossover is left out.
 */
val HOUSE_OF_X = StarterReadingOrder(
    key = "house_of_x_order_v1",
    name = "House of X / Powers of X",
    steps = listOf(
        issues(HOX, HOX, 2019, 1, note = "Krakoa"),
        issues(HOX, POX, 2019, 1),
        issues(HOX, HOX, 2019, 2, note = "Moira's secret"),
        issues(HOX, POX, 2019, 2, 3),
        issues(HOX, HOX, 2019, 3, 4),
        issues(HOX, POX, 2019, 4),
        issues(HOX, HOX, 2019, 5),
        issues(HOX, POX, 2019, 5),
        issues(HOX, HOX, 2019, 6),
        issues(HOX, POX, 2019, 6),
        issues("Hickman's X-Men", "X-Men", 2019, 1, 6),
        issues("Hickman's X-Men", "X-Men", 2019, 8, 21),
        issues("Hickman's X-Men", "Inferno", 2021, 1, 4, note = "Hickman's finale"),
    ),
)

// --- Age of Apocalypse ---

private const val AOA = "Age of Apocalypse"

/** The X-Men in a world Apocalypse rules; the eight replacement minis read whole, between Alpha and Omega. */
val AGE_OF_APOCALYPSE = StarterReadingOrder(
    key = "age_of_apocalypse_order_v1",
    name = "Age of Apocalypse",
    steps = listOf(
        issues("Legion Quest", "Uncanny X-Men", 1963, 320),
        issues("Legion Quest", "X-Men", 1991, 40),
        issues("Legion Quest", "Uncanny X-Men", 1963, 321),
        issues("Legion Quest", "X-Men", 1991, 41, note = "Legion kills Xavier in the past"),
        oneShot(AOA, "X-Men: Alpha"),
        issues(AOA, "Astonishing X-Men", 1995, 1, 4),
        issues(AOA, "Amazing X-Men", 1995, 1, 4),
        issues(AOA, "Generation Next", null, 1, 4),
        issues(AOA, "Gambit and the X-Ternals", null, 1, 4),
        issues(AOA, "Weapon X", 1995, 1, 4),
        issues(AOA, "X-Calibre", null, 1, 4),
        issues(AOA, "Factor X", null, 1, 4),
        issues(AOA, "X-Man", 1995, 1, 4),
        oneShot(AOA, "X-Men: Omega", note = "Finale"),
        issues("Sequels (optional)", "X-Men: Age of Apocalypse", 2005, 1, 6, optional = true),
        issues("Sequels (optional)", "Age of Apocalypse", 2012, 1, 14, optional = true),
    ),
)

// --- Villains in charge ---

private const val TB_BUSIEK = "Thunderbolts (Kurt Busiek)"

/** A new hero team that is secretly the Masters of Evil; Busiek & Bagley's run, #1–33. */
val THUNDERBOLTS = StarterReadingOrder(
    key = "thunderbolts_order_v1",
    name = "Thunderbolts",
    steps = listOf(
        issues(TB_BUSIEK, "Thunderbolts", 1997, 1, note = "Read to the last page"),
        issues(TB_BUSIEK, "Thunderbolts", 1997, 2, 12),
        issues(TB_BUSIEK, "Thunderbolts", 1997, 13, 24),
        issues(TB_BUSIEK, "Thunderbolts", 1997, 25, 33, note = "End of Busiek's run"),
    ),
)

private const val DR_AVENGERS = "Dark Avengers"
private const val DR_SIEGE = "Siege"
private const val DARK_AVENGERS_2009 = "Dark Avengers"
private const val SIEGE_2010 = "Siege"

/**
 * Norman Osborn's Avengers, villains in hero costumes, through Siege. Utopia crossover issues are
 * optional; Siege interleaves with Dark Avengers #13–16 by release.
 */
val DARK_REIGN = StarterReadingOrder(
    key = "dark_reign_order_v1",
    name = "Dark Reign: Dark Avengers & Siege",
    steps = listOf(
        issues(DR_AVENGERS, DARK_AVENGERS_2009, 2009, 1, 6, note = "Venom as Spider-Man, Daken as Wolverine"),
        issues(DR_AVENGERS, DARK_AVENGERS_2009, 2009, 7, 8, optional = true, note = "Utopia crossover"),
        issues(DR_AVENGERS, DARK_AVENGERS_2009, 2009, 9, 12),
        issues(DR_AVENGERS, "Dark Avengers Annual", 2009, 1, optional = true),
        oneShot(DR_SIEGE, "Siege: The Cabal", note = "Prologue"),
        issues(DR_SIEGE, SIEGE_2010, 2010, 1),
        issues(DR_SIEGE, DARK_AVENGERS_2009, 2009, 13),
        issues(DR_SIEGE, SIEGE_2010, 2010, 2),
        issues(DR_SIEGE, DARK_AVENGERS_2009, 2009, 14),
        issues(DR_SIEGE, SIEGE_2010, 2010, 3),
        issues(DR_SIEGE, DARK_AVENGERS_2009, 2009, 15),
        issues(DR_SIEGE, SIEGE_2010, 2010, 4, note = "The fall of the Dark Avengers"),
        issues(DR_SIEGE, DARK_AVENGERS_2009, 2009, 16),
    ),
)

/** What if the black suit had taken Peter over for good (Chip Zdarsky). */
val SPIDERS_SHADOW = StarterReadingOrder(
    key = "spiders_shadow_order_v1",
    name = "Spider-Man: Spider's Shadow",
    steps = listOf(issues("Spider's Shadow", "Spider-Man: Spider's Shadow", null, 1, 5)),
)

/** Mark Millar's "what if Batman were the Joker" (Millarworld). */
val NEMESIS = StarterReadingOrder(
    key = "nemesis_order_v1",
    name = "Nemesis",
    steps = listOf(
        issues("Nemesis", "Nemesis", 2010, 1, 4, note = "Millar & McNiven"),
        issues("Sequel (optional)", "Nemesis: Reloaded", null, 1, 5, optional = true),
    ),
)

// --- Mashups and variants ---

private const val AM_PRELUDE = "Prelude: DC vs. Marvel (optional)"
private const val AM_1996 = "Amalgam, wave 1 (1996)"
private const val AM_1997 = "Amalgam, wave 2 (1997)"

/** DC and Marvel heroes fused into one universe: all 24 one-shots, in publication order (Wikipedia). */
val AMALGAM = StarterReadingOrder(
    key = "amalgam_order_v1",
    name = "Amalgam Comics",
    steps = listOf(
        issues(AM_PRELUDE, "DC Versus Marvel", null, 1, optional = true, note = "The crossover that creates Amalgam"),
        issues(AM_PRELUDE, "Marvel Versus DC", null, 2, 3, optional = true),
        issues(AM_PRELUDE, "DC Versus Marvel", null, 4, optional = true),
    ) + listOf(
        "Doctor Strangefate" to "Doctor Strange + Doctor Fate",
        "Super Soldier" to "Superman + Captain America",
        "X-Patrol" to null,
        "Bullets and Bracelets" to "Punisher + Wonder Woman",
        "Spider-Boy" to "Superboy + Spider-Man",
        "Bruce Wayne: Agent of S.H.I.E.L.D." to null,
        "Amazon" to "Storm + Wonder Woman",
        "Legends of the Dark Claw" to "Batman + Wolverine",
        "Assassins" to "Daredevil + Catwoman",
        "Magneto and the Magnetic Men" to null,
        "JLX" to "Justice League + X-Men",
        "Speed Demon" to "Flash + Ghost Rider",
    ).map { (title, note) -> oneShot(AM_1996, title, note = note) } + listOf(
        "Generation Hex", "Super-Soldier: Man of War", "Bat-Thing", "Lobo the Duck", "Spider-Boy Team-Up",
        "Challengers of the Fantastic", "Exciting X-Patrol", "Dark Claw Adventures", "Thorion of the New Asgods",
        "Iron Lantern", "Magnetic Men featuring Magneto", "JLX Unleashed",
    ).map { oneShot(AM_1997, it) },
)

private const val IW_COUNTDOWN = "Infinity Countdown"
private const val IW_WARS = "Infinity Wars"
private const val IW_WARPS = "Infinity Warps"
private const val INFINITY_WARS = "Infinity Wars"
private val WARPS = listOf(
    "Infinity Wars: Soldier Supreme" to "Captain America + Doctor Strange",
    "Infinity Wars: Arachknight" to "Spider-Man + Moon Knight",
    "Infinity Wars: Iron Hammer" to "Iron Man + Thor",
    "Infinity Wars: Weapon Hex" to "X-23 + Scarlet Witch",
    "Infinity Wars: Ghost Panther" to "Ghost Rider + Black Panther",
    "Infinity Wars: Infinity Warps" to null,
)

/**
 * Gerry Duggan's 2018 Infinity saga: Gamora takes the stones and folds the universe, fusing heroes
 * into "Infinity Warps". The two-issue Warps minis are interleaved with the main series by release.
 */
val INFINITY_WARS_2018 = StarterReadingOrder(
    key = "infinity_wars_order_v1",
    name = "Infinity Wars (2018)",
    steps = buildList {
        add(oneShot(IW_COUNTDOWN, "Infinity Countdown Prime"))
        add(issues(IW_COUNTDOWN, IW_COUNTDOWN, 2018, 1, 5))
        add(oneShot(IW_COUNTDOWN, "Infinity Countdown: Adam Warlock", optional = true))
        add(oneShot(IW_WARS, "Infinity Wars Prime"))
        add(issues(IW_WARS, INFINITY_WARS, 2018, 1, 2, note = "Gamora folds the universe"))
        WARPS.forEach { (title, note) -> add(issues(IW_WARPS, title, null, 1, note = note)) }
        add(issues(IW_WARS, INFINITY_WARS, 2018, 3, 4))
        WARPS.forEach { (title, _) -> add(issues(IW_WARPS, title, null, 2)) }
        add(issues(IW_WARS, INFINITY_WARS, 2018, 5, 6))
        add(oneShot(IW_WARS, "Infinity Wars: Infinity", optional = true, note = "Epilogue"))
    },
)

// --- Alternate histories ---

/** Peter Parker ages in real time from 1962 (Chip Zdarsky & Mark Bagley). */
val LIFE_STORY = StarterReadingOrder(
    key = "life_story_order_v1",
    name = "Spider-Man: Life Story",
    steps = listOf(
        issues("Life Story", "Spider-Man: Life Story", null, 1, 6),
        oneShot("More Life Stories (optional)", "Spider-Man: Life Story Annual", optional = true),
        issues("More Life Stories (optional)", "Fantastic Four: Life Story", null, 1, 6, optional = true),
    ),
)

/**
 * Alex Ross & Jim Krueger's future Marvel universe. Each book is read whole (BatCave lists the
 * #0, #½ and #X issues its own way); specials are optional.
 */
val EARTH_X = StarterReadingOrder(
    key = "earth_x_order_v1",
    name = "Earth X Trilogy",
    steps = listOf(
        oneShot("Earth X", "Earth X", note = "Everyone on Earth gains powers"),
        oneShot("Universe X", "Universe X", note = "Includes #0 and #X"),
        oneShot("Universe X", "Universe X Special", optional = true),
        oneShot("Paradise X", "Paradise X", note = "The finale"),
        issues("Paradise X", "Paradise X: Heralds", null, 1, 3, optional = true),
        issues("Paradise X", "Paradise X: Ragnarok", null, 1, 2, optional = true),
    ),
)
