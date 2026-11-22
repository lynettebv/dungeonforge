# Sprint 9 Plan — Week 12 — Adapter and Facade — SUPPLIED

**Epics:** E10, Legacy content · E11, Session assembly · **Patterns:** Adapter and Facade
(Head First Ch. 7)

> **Stories, acceptance criteria and sub-tasks are supplied for the rest of the semester**, each
> sub-task tagged `[AC3]` for the criterion it serves. Eleven worked sprints now. Read the tags.
>
> **This sprint opens with a carry-over, US-8.5**, and it is the smallest one yet: two defects
> that 137 passing tests never noticed.

## Sprint Goal

> A monster blueprint can arrive from a file format nobody anticipated, and starting a game is
> one call that cannot forget a step — because there is only one copy of the steps.

## Capacity

| Sprint | Committed | Completed |
|---|---|---|
| Sprint 3 (Wk 5) | 10 | ____ |
| Sprint 5 (Wk 7) | 10 | ____ |
| Sprint 6 (Wk 8) | 12 | ____ |
| Sprint 7 (Wk 9) | 11 | ____ |
| Sprint 8 (Wk 10) | 12 | ____ |
| **Sprint 9 (Wk 12)** | **10** | ____ |

**Six sprints, six empty Completed columns.** "Capacity ~10" has been a guess since Week 5 and
nothing has ever checked it. This sprint commits **exactly** 10 for the first time, which makes
it the cleanest chance you will get to find out whether 10 is the right number. Part E asks for
the actual hours per story. Estimate from your commit timestamps if you did not track them; a
rough number you wrote down beats an exact number you never took.

## Committed stories

| Issue | Story | Points |
|---|---|---|
| **US-8.5** | **Carried over from Sprint 8** — two defects your green suite never noticed | **1** |
| US-9.1 | A bestiary in a format nobody here designed | 3 |
| US-9.2 | One call that starts a game | 4 |
| US-9.3 | The replay receipt | 2 |

**Capacity:** ~10 points · **Committed:** 10 points (9 new + **1 carried over**)

## Suggested order

0. **US-8.5 first.** An hour. It is debt, and it is also a warm-up in writing the failing test
   before the fix.
1. **US-9.1** — the Adapter. Self-contained, and it ends with a defect you are told to leave
   alone. Read that sub-task carefully; it is not an oversight.
2. **US-9.2** — the Facade, and most of the sprint. It fixes US-9.1's loose end as a
   side-effect, which is the point of the story.
3. **US-9.3** — the measurement. Twenty minutes, and it is where your Part D evidence comes
   from.

> **A note on what you have been given.** `scripts/codeonly.py` prints a Java file with its
> comments stripped, and **every counting command this week pipes through it.** The reason is a
> mistake your instructor has made nine times this semester: a grep for `ctx.syncState()` finds
> the javadoc that *mentions* `ctx.syncState()`. The reference solution reports two when the code
> contains one. Grep the code, not the commentary.

---

# US-8.5 — Two defects your green suite never noticed · 1 pt · CARRIED OVER FROM SPRINT 8

**As a** player
**I want** the game to accept what I type and spell its own events correctly
**So that** neither of us is punished for a typo somebody else made.

### What is wrong

**(a) An enum member is misspelled.** `EventType.MONSTER_SATE_CHANGED`. It is used in three
files and it works perfectly, because a misspelling used consistently is just an unusual name.
It will keep working until the day somebody types the correct spelling and cannot find it.

**(b) `enchant sword with FLAMING` is refused.** The keys in `EnchantmentRegistry.SPELLS` are
lower case and nothing normalises the player's input, so capitals and stray spaces produce
*"No such enchantment: FLAMING"*. The registry had `.toLowerCase().trim()` when it was written;
it was dropped somewhere in Week 9 and nothing noticed, because **no test in this project has
ever fed it a string a human actually typed.**

Both defects sat under 137 green tests. That is the lesson of the story and it is worth saying
out loud before you fix either one: **a passing suite is evidence about the things it checks.**

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **When** I grep `src/main/java` for the misspelling, **then** there are **zero** matches, and `MONSTER_STATE_CHANGED` appears in **three** files. |
| **AC2** | **Given** `EnchantmentRegistry`, **when** I pass `"FLAMING"`, `"  frost  "` or `"Vampiric"`, **then** each is recognised and applied. |
| **AC3** | **Given** an enchantment that does not exist, **then** it is still refused — normalising input must not make everything legal. |
| **AC4** | **Given** each fix, **then** a test that **failed before the fix** exists and now passes. |
| **AC5** | **When** I run `mvn test`, **then** all 137 tests that passed before still pass, unedited. |

### Sub-tasks

- [ ] **T1 [AC4]** Write the failing test for (b) first: `isKnown("FLAMING")` must be true. Run
      it. **Watch it fail.** This is the only story this semester where you are guaranteed to
      see red before green, so take the thirty seconds.
- [ ] **T2 [AC2, AC3]** Add a private `normalise(String)` to `EnchantmentRegistry` and route
      both `isKnown` and `apply` through it. Two call sites, one helper.
- [ ] **T3 [AC4]** Write the failing test for (a): `EventType.valueOf("MONSTER_STATE_CHANGED")`.
      It will throw before you fix anything.
- [ ] **T4 [AC1]** Rename the enum member. Three files: `EventType`, `CombatLog`, `Combat`. Your
      IDE's rename refactor does all three; do it that way and then verify with the grep, because
      the point of AC1 is the grep and not the rename.
- [ ] **T5 [AC1]** Add a loop test over `EventType.values()` asserting no name contains `SATE`.
      A test that outlasts this one typo.
- [ ] **T6 [AC5]** `mvn test`. Commit on `feature/us-8.5-two-quiet-defects`.

> **Why a misspelled enum is worth a point.** It is not about tidiness. It is that
> `MONSTER_SATE_CHANGED` is *load-bearing*: three files agree on it, so the program is correct.
> The cost is paid later, by whoever greps for the right spelling and concludes the event does
> not exist. Defects that cost nothing today and something later are the hardest kind to
> prioritise, which is exactly why this one has been carried for two sprints.

---

# US-9.1 — A bestiary in a format nobody here designed · 3 pts

**As a** player
**I want** the ten monsters the previous team wrote to exist in this game
**So that** four years of their content is not thrown away over a file format.

### What you have been given, and what you may not do to it

Two provided files, both in the starter:

| File | What it is |
|---|---|
| `src/main/resources/data/bestiary.csv` | the previous team's export. Pipe-delimited, ten records. |
| `src/main/java/dungeonforge/legacy/LegacyBestiary.java` | their reader. **DO NOT MODIFY.** |

Treat `LegacyBestiary` as though it came out of a jar. You can call it; you cannot change it;
nobody is going to fix it for you. Read its header before you write a line.

It is a **cursor**, not a collection:

```java
LegacyBestiary b = new LegacyBestiary("bestiary.csv");
while (b.advance()) {
    String id = b.field("MOB_ID");      // "BARROW_WIGHT"
    String hd = b.field("HIT_DICE");    // "4"      -- a String, like everything else
}
```

`MonsterFactory.register()` wants a `MonsterDef`: lower-case id, `int hp`, `int attack`,
`int xp`, a theme name, a boolean, a strategy name. Nothing in DungeonForge can consume a cursor
of strings, and **you may not change `MonsterFactory` either** — it is the client, and the
client already works.

That is the Adapter situation exactly: two interfaces you do not control and one class you
write. Head First Ch. 7, p. 243. The closest parallel in the book is the
Enumeration-to-Iterator adapter on pp. 248–250, which is also an old cursor and a new
collection.

### The translations

Six of the seven columns need converting, and one field DungeonForge needs **is not in the file
at all.**

| DungeonForge wants | The file has | Conversion |
|---|---|---|
| `id` (lower case) | `MOB_ID` = `BARROW_WIGHT` | lower-case it |
| `name` | `DISPLAY` | straight across |
| `hp` | `HIT_DICE` = `4` | hit dice × 4 + 2 |
| `attack` | `DMG` = `1d6+2` | count × floor((sides+1)/2) + bonus |
| `theme` | `ZONE` = `1` | 1 crypt · 2 forge · 3 frost · 4 winterfell |
| `boss` | `TIER` = `MINION`/`BOSS` | string to boolean |
| `strategy` | `AI` = `BRAWLER` | BRAWLER→aggressive · ARCHER→ranged · COWARD→skittish · MEDIC→healer |
| `xp` | **nothing** | see below |

**There is no xp column.** The 0.4 engine awarded experience from a table that was not exported
and no longer exists. There are exactly three honest things an adapter can do about that: refuse
the record, default it to a constant, or derive it from what *is* there. Pick one and make the
choice visible — `translationNotes()` in AC6 is how.

**And one AI name has no counterpart.** `SAPPER` monsters tunnelled through walls; this engine
has no walls to tunnel. Same rule: decide, and say so out loud.

> **This is the honest half of Adapter, and it is worth the same marks as the pattern.** An
> adapter cannot conjure information the adaptee never had. It can translate, default, derive or
> refuse — and the one thing it must never do is let a derived number look like a read one.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** the `factory` package, **then** it contains a `MonsterSource` interface declaring `List<MonsterDef> definitions()`. |
| **AC2** | **Given** `LegacyBestiaryAdapter`, **then** it **implements** `MonsterSource` and **holds** a `private final LegacyBestiary`. |
| **AC3** | **Given** `legacy/LegacyBestiary.java`, **then** it is **byte-identical** to the file you were given. |
| **AC4** | **Given** `factory/MonsterFactory.java`, **then** it is **byte-identical** to the file you started the week with. |
| **AC5** | **When** I strip comments from `MonsterFactory.java` and search it for `bestiary`, `csv`, `MOB_ID` or `HIT_DICE`, **then** there are **zero** matches. The client must not learn that a second format exists. |
| **AC6** | **Given** the adapter, **then** `translationNotes()` returns one note per derived xp value and one for the unmappable AI name. |
| **AC7** | **Given** `BARROW_WIGHT`, **then** it arrives as `barrow_wight` / "Barrow Wight" / hp **18** / attack **5** / crypt / not a boss / aggressive. |
| **AC8** | **Given** `CRYPT_WARDEN`, **then** hp is **46**, attack is **9**, and `boss` is **true**. |
| **AC9** | **Given** `definitions()` called **twice** on the same adapter, **then** both calls return ten records. (A cursor is single-use. Hiding that is the adapter's job.) |
| **AC10** | **When** I register the source with a fresh `MonsterFactory`, **then** `blueprintCount()` goes from **19 to 29** and `bossIdForTheme("crypt")` is **still `bone_tyrant`**. |
| **AC11** | **When** I run `mvn test`, **then** all 137 prior tests pass, unedited. |

### Sub-tasks

- [ ] **T1 [AC1]** Write `factory/MonsterSource.java`: `List<MonsterDef> definitions()` and
      `String sourceName()`. **Nothing in this interface may mention a file, a column or a
      format** — if it does, you have put the adaptee's vocabulary in the target.
- [ ] **T2 [AC2]** Write `legacy/LegacyBestiaryAdapter.java`: `implements MonsterSource`, one
      `private final LegacyBestiary adaptee` assigned in the constructor.
      <br><br>
      Note the shape — implements one type, holds another. **`WeaponDecorator` had exactly that
      shape in Week 9.** A decorator wraps something of *its own* type to add behaviour; an
      adapter wraps something of a *different* type to change its interface. Same mechanics,
      opposite intent. Part D1 asks you to write that distinction down.
- [ ] **T3 [AC9]** In `definitions()`, call `adaptee.rewind()` **before** you walk the cursor.
      Leave that line out and the second call returns nothing; AC9 is the test.
- [ ] **T4 [AC7]** The straightforward conversions: id to lower case, display name across, hit
      dice to hit points, zone number to theme name, tier string to boolean.
- [ ] **T5 [AC7, AC8]** The dice parser. `"2d6+3"` → 9. Count, sides, optional bonus; a `d`
      with no `+` is still valid. Keep it private and keep it small.
- [ ] **T6 [AC6]** The xp decision. Derive it, and **add a note for every record you derive
      one for** — not one note saying "some were derived."
- [ ] **T7 [AC6]** The AI mapping, with a default branch that records the unmapped name.
      `SAPPER` is the one in the file; write the branch so the eleventh AI name you have never
      seen is also recorded rather than silently flattened.
- [ ] **T8 [AC10]** Register the adapter in `Main`, at the marked `WEEK 12 (US-9.1) -- YOUR
      ADAPTER PLUGS IN HERE` comment. One line.
- [ ] **T9 [AC7, AC8, AC9, AC10]** Tests: one per named conversion, one for the double call, one
      for the blueprint count, one asserting the crypt boss did not change.
- [ ] **T10 [AC3, AC4, AC5]** Run the three greps in AC3–AC5 and paste the output into
      `docs/evidence.week12.md` §2. **The two unchanged files are the deliverable**; the adapter
      is just how you got there.
- [ ] **T11 [AC11]** `mvn test`. Commit on `feature/us-9.1-bestiary-adapter`.

> **T8 leaves a defect in place on purpose, and you are not to fix it this story.**
>
> Register the source, then start the game and look for a Barrow Wight. **There are none.**
> `blueprintCount()` says 29, and not one of the ten ever spawns.
>
> The reason is not the adapter. `new GameWorld(player)` **generates the whole dungeon in its
> constructor**, and `main()` registers your source on the line after. The blueprints arrive
> after every monster has already been placed.
>
> Write that down in evidence §2 as a sentence beginning *"the adapter works and the monsters do
> not appear, because…"*. **It is an assembly-order defect, and assembly order is what US-9.2
> exists to own.** Leaving it visible for one story is deliberate: a facade justified only by
> "fewer lines in main()" is a facade nobody remembers.

---

# US-9.2 — One call that starts a game · 4 pts

**As a** developer
**I want** one call that produces a ready-to-play session
**So that** every caller gets the complete assembly instead of whichever steps they remembered.

### Count two things before you start

```bash
ls src/main/java/dungeonforge/events/*.java | xargs grep -l 'implements GameEventListener' | wc -l
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'bus.subscribe('
```

**Four listener classes in `events/`. Four `bus.subscribe` calls** — and one of the four
subscriptions is `ConsolePrinter`, which lives in `Main` and is not one of the four classes. Do
the arithmetic.

`DamageMeter` has existed since Week 5. It is the warning that you are nearly dead. **It has
never fired for anybody**, because the only record of "these are the listeners" is a run of
`subscribe` calls in `main()` that nobody has ever counted against the contents of `events/`.

### And the thing that is actually broken

`main()` has two callers of the subsystem, and they have drifted:

| | the turn |
|---|---|
| `gameLoop()` | parse · execute · push · **`ctx.syncState()`** |
| `runScript()` | parse · execute · push |

One line, missing from one of two copies — the Week 10 lesson, in a different file. The
consequence is not cosmetic:

```
$ cat demo.txt
north
look
north

$ mvn -q exec:java -Dexec.args="--script=demo.txt"
[script] > north   You go north.
[script] > look    Hostile: Ghoul (18/18 HP, ATK 2, hunting)
[script] > north   You go north.          <-- walked out, past a live hostile
```

Interactive play refuses that move, because arriving beside a monster syncs the mode to Combat
and `CombatState` has no movement verbs. A script never syncs, so the mode never changes, so
**a script can walk the player past every monster in the dungeon.**

Which quietly falsifies the promise in `runScript`'s own Week 7 javadoc: *"attach one of these
to a bug report and the bug reproduces exactly."* It does not. Same seed, same commands, two
different games.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** a `dungeonforge.session` package, **then** it contains `GameSession` with a static `start(String playerName, GameEventListener... views)`. |
| **AC2** | **Given** `start()`, **then** it subscribes **five** listeners — quests, achievements, log, **DamageMeter**, and a transcript recorder — plus any views passed in. |
| **AC3** | **Given** a session whose player is below the damage threshold, **when** a `DAMAGE_TAKEN` event is published, **then** the warning appears in the transcript. *(A count of subscriptions is not proof that one of them works.)* |
| **AC4** | **Given** `Main.java` with comments stripped, **then** it contains **zero** matches for `ctx.syncState()` and **zero** for `history.push(`. Main does not run turns. |
| **AC5** | **Given** `GameSession` with comments stripped, **then** `history.push(command)` and `ctx.syncState()` each appear **exactly once**. One copy of the turn sequence. |
| **AC6** | **Given** `main()`, **then** it is under **60 lines** (it starts at 79). |
| **AC7** | **Given** `GameWorld`, **then** a constructor `GameWorld(Player, MonsterFactory)` exists, and `start()` registers every monster source **before** calling it. |
| **AC8** | **Given** a session started with the default seed, **then** at least one monster from `bestiary.csv` appears somewhere in the generated dungeon. **US-9.1's loose end closes here.** |
| **AC9** | **Given** the same script as above run against your build, **then** the second `north` is **refused** and the player is still in the hostile room. |
| **AC10** | **Given** `--report`, **then** it builds its world through the same assembly as play, and reports the same monster total. |
| **AC11** | **When** I run `mvn test`, **then** all 137 prior tests pass, unedited. |

### Sub-tasks

- [ ] **T1 [AC1]** Create `dungeonforge/session/` and `GameSession` with a **private**
      constructor. Callers get a session from `start()` or not at all.
- [ ] **T2 [AC7]** Add `GameWorld(Player, MonsterFactory)` and make the existing
      `GameWorld(Player)` delegate to it with `new MonsterFactory()`. **Both constructors stay**
      — 137 tests call the one-argument form and AC11 says they are not to be edited.
- [ ] **T3 [AC7, AC8]** In `start()`: build the `MonsterFactory`, register every source through
      the adapter, **then** build the `GameWorld`. Write the comment explaining why that order is
      not negotiable — you will be the person who forgets.
- [ ] **T4 [AC2]** Move the five constructions and five subscriptions out of `main()`. Count the
      classes in `events/` as you go and subscribe **all** of them.
- [ ] **T5 [AC2]** The transcript recorder: a private inner `GameEventListener` that appends
      every formatted message to a list. `transcript()` returns a copy. US-9.3 needs this.
- [ ] **T6 [AC2]** Accept `GameEventListener... views` and subscribe them **before** the welcome
      message and the first `look`, or the player sees neither. One more ordering constraint that
      used to be implicit in the order of lines in `main()`.
- [ ] **T7 [AC5]** Write `runLine(String)`: parse, execute, push, sync. Four statements. **This
      is the deliverable** — not the constructor.
- [ ] **T8 [AC4]** Rewrite `main()`: parse args, print the banner, call `start()`, then feed it
      lines from a file or from stdin. **Delete `gameLoop` and `runScript`.** Both loops become
      four lines each because the turn is no longer their business.
- [ ] **T9 [AC10]** Add `GameSession.buildWorldOnly(String)` for `--report`: the same factory,
      the same sources, the same generation order, none of the event wiring.
- [ ] **T10 [AC1]** Add the accessors the tests need — `getPlayer`, `getContext`, `getWorld`,
      `getBus`, `getHistory`, `isRunning`, `prompt`, `epilogue`, `listenerCount`. Narrow and
      read-only. **A facade that hands out a setter for everything is a god object with better
      manners.**
- [ ] **T11 [AC3, AC8, AC9]** Three tests, and they are the three that matter: the meter fires;
      a legacy monster is in the dungeon; the scripted second `north` is refused.
- [ ] **T12 [AC4, AC5, AC6]** Run the three comment-stripped greps and the `main()` line count.
      Paste them into evidence §3.
- [ ] **T13 [AC11]** `mvn test`. Commit on `feature/us-9.2-game-session`.

> **T4 is where the week's best sentence comes from.** You will subscribe `DamageMeter` and a
> feature you wrote seven weeks ago will work for the first time. Nobody broke it. Nobody fixed
> it either. It was never connected, and nothing in the language, the compiler or 137 tests had
> any opinion about that — because "the list of listeners" existed only as the order of lines in
> a method.
>
> **A facade is not primarily about convenience. It is about there being one place where a list
> like that can be wrong.**

---

# US-9.3 — The replay receipt · 2 pts

**As a** developer
**I want** to prove that a recorded session replays identically
**So that** the promise Week 7 made in a comment becomes a test.

### Acceptance criteria

| | Given / When / Then |
|---|---|
| **AC1** | **Given** the same seed, the same player name and the same list of input lines, **when** two separate sessions run them, **then** their transcripts are **equal, element for element**. |
| **AC2** | **Given** that test, **then** it also asserts the transcript is **non-empty** — two empty lists are equal and prove nothing. |
| **AC3** | **Given** a second `MonsterSource` implemented inline in a test (any format, invented on the spot), **then** registering it needs **no change** to `MonsterFactory`, `ThemeKit`, `GameWorld` or any generator. |
| **AC4** | **Given** `--report` and a played session on the same seed, **then** `totalMonsters()` agrees. |
| **AC5** | **Given** `docs/evidence.week12.md` §4, **then** it records what a second monster source cost, measured with `git diff --stat`, and what the Facade cost. |

### Sub-tasks

- [ ] **T1 [AC1, AC2]** Write `theSameInputThroughTwoSessionsProducesIdenticalTranscripts`.
      Reseed with `RandomSource.resetForTests()` before **each** session or you are comparing two
      different dungeons and the test is meaningless.
- [ ] **T2 [AC3]** Write `anotherSourceNeedsNoChangeAnywhereElse`: an anonymous `MonsterSource`
      returning one invented `MonsterDef`, registered into a fresh factory. Assert `has()` and
      `idsForTheme()` find it. **This is the Adapter's whole claim, as a test rather than a
      sentence.**
- [ ] **T3 [AC4]** Write `theDiagnosticWorldAndThePlayWorldAgree`.
- [ ] **T4 [AC5]** `git diff --stat` for the US-9.1 commit and the US-9.2 commit, pasted into
      evidence §4, with one sentence each on what the number means.
- [ ] **T5 [AC5]** Part 7 of the evidence sheet: name one thing **each** pattern cost you. They
      both cost something and the two costs are different.
- [ ] **T6** `mvn test`. Commit on `feature/us-9.3-replay-receipt`.

> **T1 has a trap in it that caught your instructor in Week 10.** A loop that does not vary its
> input is not a sample — and its mirror image is a comparison that does not *fix* its input.
> Forget the reseed and the two sessions get different dungeons; the test fails for a reason
> that has nothing to do with the thing it claims to check, and the obvious "fix" is to weaken
> the assertion. Reseed instead.

---

## Risks

| Risk | Mitigation |
|---|---|
| Editing `LegacyBestiary` to make it nicer | AC3 is a byte comparison against the file you were given. You do not own the adaptee; that is the entire premise. |
| Editing `MonsterFactory` to "just take a CSV" | AC4 and AC5. The moment the client knows the format, you have not written an adapter — you have written a second loader. |
| Putting the legacy column names into `MonsterSource` | The target speaks the client's language, never the adaptee's. T1 of US-9.1. |
| Forgetting `rewind()` in `definitions()` | The second call returns nothing. AC9 of US-9.1 is the test and it will look like a mystery for ten minutes. |
| Letting a derived xp look like a read one | AC6. An adapter that quietly invents data is indistinguishable from one that read the file correctly, until someone asks where a number came from. |
| Fixing US-9.1's spawn defect inside US-9.1 | Do not. It is an ordering defect and US-9.2 owns ordering. Write the sentence instead. |
| Deleting `GameWorld(Player)` when adding the two-argument form | 137 tests call it and AC11 says they are not to be edited. Delegate, do not replace. |
| Subscribing views after the welcome message | The player sees neither it nor the first `look`. T6 of US-9.2. |
| A facade with a getter and setter for everything | That is a god object with better manners. T10 of US-9.2: narrow and read-only. |
| Counting subscriptions and calling `DamageMeter` done | AC3 of US-9.2 makes it **fire**. A count is not a behaviour. |
| Comparing two empty transcripts | They are equal. AC2 of US-9.3. |
| A bare `grep` disagreeing with your instructor's number | Pipe through `scripts/codeonly.py`. The reference's own javadoc contains the strings being counted. |

## Definition of Done

`docs/definition-of-done.md`, unchanged.

---

# ↓ Fill in at the end of the week ↓

## Calibration

| Story | Estimated | Actual hours | High, low, about right? |
|---|---|---|---|
| **US-8.5** (carry-over) | **1** | | |
| US-9.1 | 3 | | |
| US-9.2 | 4 | | |
| US-9.3 | 2 | | |

**Points completed:** ____ · **Running velocity:** ____

**This is the sixth sprint with an empty Completed column.** Fill this one in, and fill in
Sprint 8's while the commits are still fresh. Three weeks of real numbers beats six months of
"~10".

## Sprint Review — one sentence

## Retro — one thing to keep, one thing to change

**And one specific question this week:** Adapter and Facade are both "a class that stands in
front of other classes." In one sentence each, say what made them different in practice —
not in the book.
