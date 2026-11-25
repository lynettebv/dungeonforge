# Week 12 Evidence

Fill this in as you go. Parts 1–3 and Part 5 are graded on the **measurement**, not on the code.

**Every counting command here pipes through `scripts/codeonly.py`.** The reason is in that
file's header: a bare grep for `ctx.syncState()` finds the javadoc that *mentions*
`ctx.syncState()`. Grep the code, not the commentary.

---

## 0. The Sprint 8 carry-over (US-8.5)

```bash
grep -rn 'MONSTER_SATE_CHANGED' src/main/java | wc -l
```

**How many files share the misspelling?** ___3___

**It works perfectly. In one sentence: what does it cost, and when?**
> 

**Now the other one.** Before changing anything, write a test asserting
`EnchantmentRegistry.isKnown("FLAMING")`. Run it.

**Paste the failure.**

```

```

**One sentence: 137 tests passed and this did not work. What were those 137 tests about?**


---

## 1. BEFORE — count the listeners

```bash
ls src/main/java/dungeonforge/events/*.java | xargs grep -l 'implements GameEventListener' | wc -l
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'bus.subscribe('
```

**Listener classes in `events/`:** ___4___  **`bus.subscribe` calls in `Main`:** ___4___

**Those two numbers are equal and that is not reassuring. Why not?** *(Look at what the fourth
subscription actually subscribes, and where that class lives.)*

**Which listener class is never subscribed?** ______

**Open it. What does it do, and when was it written?**


**Now the sentence. Put both counts and the consequence in one sentence.** *(This is the most
important sentence you will write this week. Two points.)*

> Even though the counts are equal, the sets aren't. One of the four subscriptions is ConsolePrinter, which lives in Main
> and not in events. Only 3 of the 4 listener classes are subscribed, while DamageMeter is the one that isn't.

**Nobody broke it. In one sentence: what kind of mistake is this, if it is not a bug?**

> It's a maintainability issue because the event subscriptions have to be updated manually whenever a new listener is added.
---

## 2. BEFORE — the two games

```bash
printf 'north\nlook\nnorth\nlook\n' > demo.txt
mvn -q exec:java -Dexec.args="--script=demo.txt"
```

**Paste the scripted output:**

```
$ mvn -q exec:java -Dexec.args="--script=demo.txt"
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.12.0   seed 20260818

  Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.
  [L1R0] Level 1: Crypt
  "Burial niches line the walls. Most are empty. Most."
  Exits: north

[script] > north
  You go north.
  Ghoul is now hunting.

[script] > look
  [L1R1] Level 1: Crypt
  "Damp stone. Something drips in the dark, patiently."
  Hostile: Ghoul (18/18 HP, ATK 2, hunting)
  Iron-bound Chest:
    - Vial of Still Water [heals 21] (0.4kg, 25g)
    - Vampiric Bone Shortsword [dmg 6] [drain 1] (2.0kg, 140g
    - Grave Shroud [def 3] (3.0kg, 43g)
  Exits: south, north

[script] > north
  You go north.

  Delver  HP 80/80  ATK 10  DEF 3  Gold 0  XP 0  Carry 60.0kg  (unarmed)
  Actions this session: 3   |   undo stack depth: 2
```

Now run `mvn -q exec:java` and type the same four commands by hand.

**Paste that output:**

```
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.12.0   seed 20260818

  Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.
  [L1R0] Level 1: Crypt
  "Burial niches line the walls. Most are empty. Most."
  Exits: north

[Exploring | HP 80/80] > north

  You go north.
  Ghoul is now hunting.
  Combat! 1 hostile(s). Try 'attack'.

[Combat | HP 80/80] > look 

  [L1R1] Level 1: Crypt
  "Damp stone. Something drips in the dark, patiently."
  Hostile: Ghoul (18/18 HP, ATK 2, hunting)
  Iron-bound Chest:
    - Vial of Still Water [heals 21] (0.4kg, 25g)
    - Vampiric Bone Shortsword [dmg 6] [drain 1] (2.0kg, 140g
    - Grave Shroud [def 3] (3.0kg, 43g)
  Exits: south, north

[Combat | HP 80/80] > north

  You cannot do that in Combat mode. 
    Available here: attack, look, use, equip, inventory, status, history, undo, help, quit

[Combat | HP 80/80] > 
```

**They differ. Name the file, the method and the line that is the difference.** __File = Main.java, Method = runScript(),
 difference = line 136: ctx.syncState() is missing from runScript()____

**Put the two turn sequences side by side:**

| | the turn |
|---|---|
| `gameLoop()` | parse() -> execute() -> history.push() -> ctx.sncState() |
| `runScript()` | parse() -> execute() -> history.push() |

**What can a script do that a player cannot?**

> The script allows movement during Combat because runScript() doesn't call ctx.syncState()
> after it executes each command. So we never enter Combat mode, where leaving the room isn't an option.

**`runScript`'s javadoc has been promising something since Week 7. Quote it, and say whether it
is true.**

> The Javadoc promises that "the bug reproduces exactly," but this isn't true because runScript() doesn't call 
> ctx.syncState() after each command. As a result, scripted gameplay can behave differently from interactive gameplay, 
> even when using the same commands and seed.

---

## 3. BEFORE — the counting

```bash
py scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'ctx.syncState()'
py scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'history.push('
py scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'DamageMeter'
awk '/public static void main/{on=1} on{n++} on&&/^    }$/&&n>3{print n; exit}' src/main/java/dungeonforge/Main.java
```

| | Before | After |
|---|---|---|
| `main()` lines | 191 | |
| `ctx.syncState()` in `Main` | 3 | |
| copies of the turn sequence | 3 | |
| `DamageMeter` mentioned outside its own file | 0 | |
| listener classes subscribed, of 4 | 3 | |
| monster blueprints available | 79 | |
| a script can walk past a live hostile | yes | |

*(Fill the After column at the end of the sprint. **Nothing in it may be a guess.**)*

---

## 4. AFTER — the two receipts

### US-9.1, the Adapter

```bash
git diff --stat HEAD~1          # on the us-9.1 commit
diff src/main/java/dungeonforge/legacy/LegacyBestiary.java  <the file you were given>
diff src/main/java/dungeonforge/factory/MonsterFactory.java <the file you started with>
py scripts/codeonly.py src/main/java/dungeonforge/factory/MonsterFactory.java \
  | grep -ciE 'bestiary|csv|MOB_ID|HIT_DICE'
```

**Paste all four.**

```
1. 

lynet@LynetteV MINGW64 ~/IdeaProjects/dungeonforge (feat/US-9.2)
$ git diff --stat HEAD~1 
 docs/evidence.week12.md                            |  38 ++++-
 src/main/java/dungeonforge/Main.java               |  10 ++
 .../java/dungeonforge/factory/MonsterSource.java   |   9 +
 .../dungeonforge/legacy/LegacyBestiaryAdapter.java | 111 +++++++++++++
 src/test/java/dungeonforge/AdapterFacadeTest.java  | 185 ++++++++++++++++++++-
 5 files changed, 343 insertions(+), 10 deletions(-)
 
2.
diff -u \
  src/main/java/dungeonforge/legacy/LegacyBestiary.java \
  <(unzip -p ~/Downloads/lab12-adapter-facade-starter.zip \
  lab12-adapter-facade-starter/src/main/java/dungeonforge/legacy/LegacyBestiary.java)
  
 3. 
 diff -u \
  src/main/java/dungeonforge/factory/MonsterFactory.java \
  <(unzip -p ~/Downloads/lab12-adapter-facade-starter.zip \
  lab12-adapter-facade-starter/src/main/java/dungeonforge/factory/MonsterFactory.java)

4.
$ py scripts/codeonly.py src/main/java/dungeonforge/factory/MonsterFactory.java \
  | grep -ciE 'bestiary|csv|MOB_ID|HIT_DICE'
0
```

**Two of those commands are the deliverable. Which two, and why?**

> The two diff commands, #2 and #3, are the deliverable because they produce no output at all.
> This proves that neither MonsterFactory nor LegacyBestiary were modified.
> Adapter allows these two classes to work together without changing their original code.

**`blueprintCount()` before:** ___19___ **after:** ___29___

### US-9.2, the Facade

```bash
git diff --stat HEAD~1          # on the us-9.2 commit
```

**Paste it.**

```

```

**One sentence each on what the two numbers mean.** *(They mean different things. One is about a
format; one is about an order.)*


---

## 5. AFTER — what you did not have to change

**List every file that learned a CSV exists.** ______

**Is `MonsterFactory` one of them?** ______

**`ThemeKit` picks which monsters appear in a room. How many lines of it did you change to make
ten new monsters appear?** ______

**In two or three sentences: explain how a class can use a blueprint that arrived from a file
format it has never heard of.**


---

## 6. AFTER — the honest accounting

### The Adapter's honest half

**`bestiary.csv` has no xp column. Which of the three options did you take?** ______

**Where in your code does a reader find out that the number is derived?** ______

**One sentence: what would have gone wrong if you had silently defaulted it to 5?**


**`SAPPER` has no counterpart. What did you do, and what happens when the eleventh unknown AI
name arrives next semester?**


### The Facade's honest half

**Complete this honestly:**

> `GameSession` now knows about eight collaborators and the order they go together in, which
> means the coupling ______________________________________________

**Name one thing that got HARDER because `Main` no longer knows those eight classes.** *(There
is a real answer. Think about what a test needs.)*


---

## 7. Retro line

**One sentence on something each pattern gave you, and one on something each one took away.**
Four sentences, and the two costs are not the same cost.

| Pattern | Gave | Took |
|---|---|---|
| Adapter | | |
| Facade | | |

**And the one-sentence distinction, in your own words, having now written both:** Adapter and
Facade are both "a class in front of other classes." What made them different in practice?


