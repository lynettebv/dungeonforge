#!/usr/bin/env bash
#
# seed-backlog.sh - creates every label, all 14 epics, and both sprints of user stories
#                   in your GitHub repository, so your board is populated in one command.
#
# You do not need to understand this script to use it. If you are curious later, it is
# about 150 lines of `gh issue create` calls.
#
# PREREQUISITES
#   1. GitHub CLI installed:  https://cli.github.com/
#        macOS     brew install gh
#        Windows   winget install --id GitHub.cli    (then run this from Git Bash)
#        Linux     see https://github.com/cli/cli#installation
#   2. Authenticated:         gh auth login
#   3. Run from inside your cloned dungeonforge repository:
#        bash scripts/seed-backlog.sh
#
# No gh? Every issue is written out in docs/backlog.md. Create them by hand from the issue
# templates - about twenty minutes, and you lose no points.

set -euo pipefail

# ---------- sanity checks ----------

if ! command -v gh >/dev/null 2>&1; then
  echo "ERROR: the GitHub CLI ('gh') is not installed."
  echo "       Install it from https://cli.github.com/ or create the issues by hand"
  echo "       from docs/backlog.md."
  exit 1
fi

if ! gh auth status >/dev/null 2>&1; then
  echo "ERROR: you are not logged in. Run:  gh auth login"
  exit 1
fi

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  echo "ERROR: this is not a git repository. cd into your dungeonforge clone first."
  exit 1
fi

REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)

echo
echo "This will create labels and 21 issues in:  $REPO"
echo "  - 14 epics"
echo "  -  3 Sprint 0 user stories (this week)"
echo "  -  4 Sprint 1 user stories (Week 3)"
echo
read -r -p "Proceed? [y/N] " reply
case "$reply" in
  [yY]|[yY][eE][sS]) ;;
  *) echo "Cancelled. Nothing was created."; exit 0 ;;
esac

# ---------- labels ----------

make_label() {
  gh label create "$1" --color "$2" --description "$3" --force >/dev/null 2>&1 \
    && echo "  label: $1" \
    || echo "  label: $1 (already existed)"
}

echo
echo "Creating labels..."
make_label "epic"       "5319E7" "A pattern-sized body of work"
make_label "user-story" "0E8A16" "A story with acceptance criteria"
make_label "sub-task"   "C2E0C6" "A step inside a story"
make_label "sprint-00"  "1D76DB" "Week 2 sprint"
make_label "sprint-01"  "1D76DB" "Week 3 sprint"
make_label "blocked"    "B60205" "Waiting on something"
make_label "dod-failed" "E99695" "Returned from review"
make_label "pattern:singleton" "FBCA04" "Touches the Singleton pattern"

# ---------- helper ----------

new_issue() {           # new_issue <title> <labels> <body>
  gh issue create --title "$1" --label "$2" --body "$3" >/dev/null
  echo "  issue: $1"
}

# ---------- epics ----------

echo
echo "Creating the 14 epics..."
new_issue "[EPIC] E1 Core domain" "epic" \
"**Week 1 - no pattern**

Entities, items and rooms exist as plain objects, with deliberate flaws that later patterns will fix.

_Completed in Lab 1._"

new_issue "[EPIC] E2 Process infrastructure" "epic" \
"**Week 2 - no pattern**

A repo, board, CI pipeline and Definition of Done that carry the whole semester.

Sprint 0 delivers this epic."

new_issue "[EPIC] E3 Configuration and randomness" "epic" \
"**Week 3 - Singleton**

Every tunable value and every random roll comes from a single, seeded source.

Sprint 1 delivers this epic."

new_issue "[EPIC] E4 Content creation" "epic" \
"**Week 4 - Factory Method, Abstract Factory**

Monsters, loot and room contents are created without \`new\` appearing outside a factory."

new_issue "[EPIC] E5 Monster behaviour and events" "epic" \
"**Week 5 - Strategy, Observer**

Monsters change tactics at runtime, and quests react to combat without combat knowing quests exist."

new_issue "[EPIC] E6 Player actions" "epic" \
"**Week 7 - Command**

Every action becomes an object, giving undo, macros and a replay log."

new_issue "[EPIC] E7 Game modes" "epic" \
"**Week 8 - State**

Exploring, combat and inventory each allow only their own verbs."

new_issue "[EPIC] E8 Item enchantment" "epic" \
"**Week 9 - Decorator**

Enchantments stack in any combination without a class explosion."

new_issue "[EPIC] E9 Turn and level algorithms" "epic" \
"**Week 10 - Template Method**

Turn order and level generation have fixed skeletons with varying steps."

new_issue "[EPIC] E10 Legacy data and simplified API" "epic" \
"**Week 12 - Adapter, Facade**

A hostile third-party bestiary is consumed unchanged, and \`main()\` shrinks to a handful of lines."

new_issue "[EPIC] E11 Nested inventory and traversal" "epic" \
"**Week 13 - Composite, Iterator**

Bags go inside bags, and callers loop over them without knowing that."

new_issue "[EPIC] E12 Damage calculation and access control" "epic" \
"**Week 14 - Pipeline, Proxy**

Damage becomes a chain of independently testable stages; expensive and privileged things are guarded."

new_issue "[EPIC] E13 Presentation layer" "epic" \
"**Week 15 - MVC (Observer + Strategy + Composite)**

The model performs no I/O, and a second view can be added without touching it."

new_issue "[EPIC] E14 Release and documentation" "epic" \
"**Week 16 - all patterns**

A tagged release with UML, a README, and a pattern-to-class map."

# ---------- Sprint 0 ----------

echo
echo "Creating Sprint 0 user stories (this week)..."

new_issue "[STORY] S0.1 The project builds and tests itself" "user-story,sprint-00" \
"**Epic:** E2 Process infrastructure
**Estimate:** 3 points

As a **developer**, I want a minimal program that compiles and passes tests, so that any future failure is my code rather than my setup.

## Acceptance Criteria
- Given a clone of the repo, when I run \`mvn test\`, then the build succeeds and at least one test runs.
- Given a clone of the repo, when I run \`mvn exec:java\`, then a banner containing the project name is printed to standard output.
- Given \`pom.xml\`, when I inspect the compiler configuration, then the release version is 21.

## Sub-tasks
- [ ] Add \`pom.xml\` with JUnit 5 and JDK 21
- [ ] Add \`Main.java\` with a banner and a version constant
- [ ] Add \`SkeletonTest.java\`
- [ ] Confirm \`mvn test\` is green locally"

new_issue "[STORY] S0.2 A broken build blocks a merge" "user-story,sprint-00" \
"**Epic:** E2 Process infrastructure
**Estimate:** 2 points

As a **developer**, I want continuous integration to fail loudly on a broken test, so that I cannot merge work that does not run.

## Acceptance Criteria
- Given a push to any branch with an open pull request, when the workflow runs, then a check named \`build\` appears on the pull request.
- Given a test that fails, when I push it, then the check reports failure and the log names the failing test.
- Given the failing test is repaired and pushed, when the workflow re-runs, then the check reports success.

## Sub-tasks
- [ ] Add \`.github/workflows/ci.yml\`
- [ ] Deliberately break a test and push it
- [ ] Screenshot the red check and read the Actions log
- [ ] Repair, push, screenshot the green check"

new_issue "[STORY] S0.3 The board shows the truth" "user-story,sprint-00" \
"**Epic:** E2 Process infrastructure
**Estimate:** 2 points

As a **developer**, I want a board whose columns reflect what is actually happening, so that I never have to reconstruct my own progress from memory.

## Acceptance Criteria
- Given the project board, when I open it, then it has exactly the columns Backlog, Sprint Backlog, In Progress, In Review, Done.
- Given all 14 epics, when the board is seeded, then each appears as an issue labelled \`epic\` in Backlog.
- Given the In Progress column, when I count its cards at any moment, then there are no more than two.
- Given a merged pull request containing \`Closes #N\`, when the merge completes, then issue N is closed.

## Sub-tasks
- [ ] Create the project board with five columns
- [ ] Run \`scripts/seed-backlog.sh\`
- [ ] Place epics and both sprints in the right columns
- [ ] Record the WIP limit in the board description"

# ---------- Sprint 1 ----------

echo
echo "Creating Sprint 1 user stories (Week 3)..."

new_issue "[STORY] US-1.1 Settings live in one place" "user-story,sprint-01,pattern:singleton" \
"**Epic:** E3 Configuration and randomness
**Estimate:** 3 points

As a **game designer**, I want every tunable number to live in one configuration file, so that I can rebalance the game without recompiling it.

## Acceptance Criteria
- Given \`config.json\` sets \`playerStartingHp\` to 80, when a new game starts, then the player has 80 hit points.
- Given the source tree, when I search for a hardcoded starting-HP literal, then there are zero matches outside the configuration class.
- Given two separate calls to \`GameConfig.getInstance()\`, when I compare the two references, then they are the same object.
- Given a missing or unreadable \`config.json\`, when the game starts, then documented default values are used and the game still runs.

## Sub-tasks
- [ ] Create \`GameConfig\` with a private constructor
- [ ] Add a static \`getInstance()\`
- [ ] Load \`config.json\` from the classpath, falling back to defaults
- [ ] Replace hardcoded values in \`Entity\` and \`GameWorld\`"

new_issue "[STORY] US-1.2 The same seed produces the same dungeon" "user-story,sprint-01,pattern:singleton" \
"**Epic:** E3 Configuration and randomness
**Estimate:** 3 points

As a **developer**, I want one seeded source of randomness, so that a bug someone reports can be reproduced exactly on my machine.

## Acceptance Criteria
- Given the seed 12345, when I generate a sequence of ten random values twice, then both sequences are identical.
- Given two separate calls to \`RandomSource.getInstance()\`, when I compare the references, then they are the same object.
- Given the running program, when I search for \`new Random(\` anywhere outside \`RandomSource\`, then there are zero matches.
- Given a new seed passed at startup, when the game runs, then the generated content differs from the previous seed's.

## Sub-tasks
- [ ] Create \`RandomSource\` wrapping a single seeded \`Random\`
- [ ] Read the seed from \`GameConfig\`
- [ ] Add \`reseed(long)\` for tests
- [ ] Remove every other \`new Random(\`"

new_issue "[STORY] US-1.3 The one-instance rule is enforced, not hoped for" "user-story,sprint-01,pattern:singleton" \
"**Epic:** E3 Configuration and randomness
**Estimate:** 2 points

As a **developer**, I want automated tests proving only one instance of each singleton can exist, so that a future refactor cannot quietly break the guarantee.

## Acceptance Criteria
- Given \`SingletonTest\`, when it runs, then it asserts that two \`getInstance()\` calls return the same reference for both singletons.
- Given \`GameConfig\` and \`RandomSource\`, when I inspect their constructors, then both are private.
- Given the test suite, when I run \`mvn test\`, then all tests pass and CI reports green.

## Sub-tasks
- [ ] Write \`SingletonTest\` for \`GameConfig\`
- [ ] Write \`SingletonTest\` for \`RandomSource\`
- [ ] Add a determinism test for the same seed"

new_issue "[STORY] US-1.4 Improve the configuration code" "user-story,sprint-01,pattern:singleton" \
"**Epic:** E3 Configuration and randomness
**Estimate:** 5 points

As a **developer**, I want to refactor \`GameConfig\` to use a static \`HashMap\` with double-checked locking, so that the code is better and more professional.

## Acceptance Criteria
- Given the code, when it is reviewed, then it looks clean and professional.
- Given the class, when another developer reads it, then they find it easy to follow.

## Sub-tasks
- [ ] Refactor the internals
- [ ] Make sure it still works"

echo
echo "Done. 21 issues created in $REPO"
echo
echo "Next:"
echo "  1. Build your project board with five columns"
echo "  2. Put the 14 epics in Backlog"
echo "  3. Put the 3 sprint-00 stories in Sprint Backlog - those are this week's work"
echo "  4. Leave the 4 sprint-01 stories in Backlog - they are Week 3's"
echo
echo "Then read docs/backlog.md carefully. One of those stories is not like the others."
