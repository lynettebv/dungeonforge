# Product Backlog — SUPPLIED

> **You do not write these.** Every epic and user story below has been written for you,
> complete with acceptance criteria and estimates, and `scripts/seed-backlog.sh` loads them
> into your GitHub Issues automatically.
>
> Your job in Week 2 is to **read them critically** (Lab Part C) and to **run Sprint 0**.
> You'll write your own stories in Week 6, after you've read about fifteen of these.
>
> ⚠️ **One of the user stories below is deliberately defective.** Finding it and explaining
> why is worth 12 points. See `docs/artifact-review.md`.

---

## The 14 epics

| # | Epic | Week | Pattern(s) | Goal |
|---|---|---|---|---|
| E1 | Core domain | 1 | none | Entities, items and rooms exist as plain objects, with deliberate flaws. |
| E2 | Process infrastructure | 2 | none | A repo, board, CI pipeline and Definition of Done that carry the whole semester. |
| E3 | Configuration & randomness | 3 | Singleton | Every tunable value and every random roll comes from one seeded source. |
| E4 | Content creation | 4 | Factory Method, Abstract Factory | Monsters, loot and room contents are created without `new` outside a factory. |
| E5 | Monster behaviour & events | 5 | Strategy, Observer | Monsters change tactics at runtime; quests react to combat without combat knowing quests exist. |
| E6 | Player actions | 7 | Command | Every action is an object, giving undo, macros and a replay log. |
| E7 | Game modes | 8 | State | Exploring, combat and inventory each allow only their own verbs. |
| E8 | Item enchantment | 9 | Decorator | Enchantments stack in any combination without a class explosion. |
| E9 | Turn & level algorithms | 10 | Template Method | Turn order and level generation have fixed skeletons with varying steps. |
| E10 | Legacy data & simplified API | 12 | Adapter, Facade | A hostile third-party bestiary is consumed unchanged; `main()` shrinks to a few lines. |
| E11 | Nested inventory & traversal | 13 | Composite, Iterator | Bags go inside bags, and callers loop over them without knowing that. |
| E12 | Damage calculation & access control | 14 | Pipeline, Proxy | Damage is a chain of testable stages; expensive and privileged things are guarded. |
| E13 | Presentation layer | 15 | MVC | The model performs no I/O; a second view can be added without touching it. |
| E14 | Release & documentation | 16 | all | A tagged release with UML, README and a pattern-to-class map. |

> Weeks 6 and 11 carry no new epic on purpose. They are the only slack in the semester.

---

# SPRINT 0 — Week 2 — Epic E2, Process Infrastructure

**Sprint Goal:** *A repository that builds itself, tests itself, and refuses work that isn't
finished.*

**Capacity:** ~8 points · **Committed:** 7 points

---

### S0.1 — The project builds and tests itself
**3 points** · `epic: E2` · `sprint-00`

> As a **developer**, I want a minimal program that compiles and passes tests, so that any
> future failure is my code rather than my setup.

**Acceptance Criteria**
- Given a clone of the repo, when I run `mvn test`, then the build succeeds and at least one test runs.
- Given a clone of the repo, when I run `mvn exec:java`, then a banner containing the project name is printed to standard output.
- Given `pom.xml`, when I inspect the compiler configuration, then the release version is 21.

**Sub-tasks**
- [ ] Add `pom.xml` with JUnit 5 and JDK 21
- [ ] Add `Main.java` with a banner and a version constant
- [ ] Add `SkeletonTest.java`
- [ ] Confirm `mvn test` is green locally

---

### S0.2 — A broken build blocks a merge
**2 points** · `epic: E2` · `sprint-00`

> As a **developer**, I want continuous integration to fail loudly on a broken test, so that
> I can't merge work that doesn't run.

**Acceptance Criteria**
- Given a push to any branch with an open pull request, when the workflow runs, then a check named `build` appears on the pull request.
- Given a test that fails, when I push it, then the check reports failure and the log names the failing test.
- Given the failing test is repaired and pushed, when the workflow re-runs, then the check reports success.

**Sub-tasks**
- [ ] Add `.github/workflows/ci.yml`
- [ ] Deliberately break a test and push it
- [ ] Screenshot the red check and read the Actions log
- [ ] Repair, push, screenshot the green check

---

### S0.3 — The board shows the truth
**2 points** · `epic: E2` · `sprint-00`

> As a **developer**, I want a board whose columns reflect what is actually happening, so
> that I never have to reconstruct my own progress from memory.

**Acceptance Criteria**
- Given the project board, when I open it, then it has exactly the columns Backlog, Sprint Backlog, In Progress, In Review, Done.
- Given all 14 epics, when the board is seeded, then each appears as an issue labelled `epic` in Backlog.
- Given the In Progress column, when I count its cards at any moment, then there are no more than two.
- Given a merged pull request containing `Closes #N`, when the merge completes, then issue N is closed.

**Sub-tasks**
- [ ] Create the project board with five columns
- [ ] Run `scripts/seed-backlog.sh`
- [ ] Place epics and both sprints in the right columns
- [ ] Record the WIP limit in the board description

---

# SPRINT 1 — Week 3 — Epic E3, Configuration & Randomness (Singleton)

> **Do not start these this week.** They sit in Backlog and become Week 3's Sprint Backlog.
> Read them now; you will be asked about them in Part C.

**Sprint Goal:** *Every tunable value and every random roll comes from a single, seeded source.*

**Capacity:** ~10 points · **Committed:** 13 points

---

### US-1.1 — Settings live in one place
**3 points** · `epic: E3` · `sprint-01` · `pattern:singleton`

> As a **game designer**, I want every tunable number to live in one configuration file, so
> that I can rebalance the game without recompiling it.

**Acceptance Criteria**
- Given `config.json` sets `playerStartingHp` to 80, when a new game starts, then the player has 80 hit points.
- Given the source tree, when I search for a hardcoded starting-HP literal, then there are zero matches outside the configuration class.
- Given two separate calls to `GameConfig.getInstance()`, when I compare the two references, then they are the same object.
- Given a missing or unreadable `config.json`, when the game starts, then documented default values are used and the game still runs.

**Sub-tasks**
- [ ] Create `GameConfig` with a private constructor
- [ ] Add a static `getInstance()`
- [ ] Load `config.json` from the classpath, falling back to defaults
- [ ] Replace hardcoded values in `Entity` and `GameWorld`

---

### US-1.2 — The same seed produces the same dungeon
**3 points** · `epic: E3` · `sprint-01` · `pattern:singleton`

> As a **developer**, I want one seeded source of randomness, so that a bug someone reports
> can be reproduced exactly on my machine.

**Acceptance Criteria**
- Given the seed 12345, when I generate a sequence of ten random values twice, then both sequences are identical.
- Given two separate calls to `RandomSource.getInstance()`, when I compare the references, then they are the same object.
- Given the running program, when I search for `new Random(` anywhere outside `RandomSource`, then there are zero matches.
- Given a new seed passed at startup, when the game runs, then the generated content differs from the previous seed's.

**Sub-tasks**
- [ ] Create `RandomSource` wrapping a single seeded `Random`
- [ ] Read the seed from `GameConfig`
- [ ] Add `reseed(long)` for tests
- [ ] Remove every other `new Random(`

---

### US-1.3 — The one-instance rule is enforced, not hoped for
**2 points** · `epic: E3` · `sprint-01` · `pattern:singleton`

> As a **developer**, I want automated tests proving only one instance of each singleton can
> exist, so that a future refactor can't quietly break the guarantee.

**Acceptance Criteria**
- Given `SingletonTest`, when it runs, then it asserts that two `getInstance()` calls return the same reference for both singletons.
- Given `GameConfig` and `RandomSource`, when I inspect their constructors, then both are private.
- Given the test suite, when I run `mvn test`, then all tests pass and CI reports green.

**Sub-tasks**
- [ ] Write `SingletonTest` for `GameConfig`
- [ ] Write `SingletonTest` for `RandomSource`
- [ ] Add a determinism test for the same seed

---

### US-1.4 — Improve the configuration code
**5 points** · `epic: E3` · `sprint-01` · `pattern:singleton`

> As a **developer**, I want to refactor `GameConfig` to use a static `HashMap` with
> double-checked locking, so that the code is better and more professional.

**Acceptance Criteria**
- Given the code, when it is reviewed, then it looks clean and professional.
- Given the class, when another developer reads it, then they find it easy to follow.

**Sub-tasks**
- [ ] Refactor the internals
- [ ] Make sure it still works

---

> **Sprint 1 committed total: 3 + 3 + 2 + 5 = 13 points against a capacity of ~10.**
> That is worth thinking about. See `docs/artifact-review.md`, question C4.
