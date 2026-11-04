# Week 8 Evidence

## 1. BEFORE — the two problems

### Problem A: mode checks scattered across commands

```bash
grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
```

**Paste it. How many command classes re-implement the same check?**

```
$ grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
src/main/java/dungeonforge/commands/CommandParser.java:172:        if (room.hasLivingMonsters()) {
src/main/java/dungeonforge/commands/DropCommand.java:21:        if (room().hasLivingMonsters()) {
src/main/java/dungeonforge/commands/MoveCommand.java:19:        if (room().hasLivingMonsters()) {
src/main/java/dungeonforge/commands/TakeCommand.java:13:        if (room().hasLivingMonsters()) {
```
3 command classes: drop, move, and take

**Play a turn: start a fight, then type `north`. Paste what happens:**

```
  Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.
  [L1R0] Level 1: Crypt
  "Burial niches line the walls. Most are empty. Most."
  Exits: north

n
[HP 80/80] > 
  You go north.
  ACHIEVEMENT: Cornered -- Force an enemy to change tactics
  Ghoul changes tactics: idle -> hunting.
  Ghoul hits you for 2.

hit
[HP 78/80] > 
  You hit Ghoul for 10.
  Ghoul changes tactics: hunting -> enraged.
  Ghoul hits you for 3.

north
[HP 75/80] > 
  You cannot walk away with enemies still standing.
```

**Now type `help` during that fight. Does it list `north`?** __yes__
**Should it?**
No, because this action is unavailable at this time. As it lets me know when
I try to walk away while live enemies are still standing.


### Problem B: the monster lifecycle as a string plus conditionals

```bash
grep -n "getStateName" src/main/java/dungeonforge/core/Combat.java
```

**Paste it. Which three methods branch on the state name?**

```
$ grep -n "getStateName" src/main/java/dungeonforge/core/Combat.java
65:        String current = m.getStateName();
109:        String state = m.getStateName();
147:        if (m.getStateName().equals("enraged")) {
```
updateMonsterState()
monsterActs()
damageFrom()

**Answer before you refactor:**

1. To add a "stunned" state, which methods must you edit?

2. Reading `updateMonsterState()`, which states can follow `enraged`? How long did it take you
   to be sure?
- An enraged monster can transition to fleeing or dead. It took me a little bit to be sure
because I had to read through the conditionals to understand which transitions were possible.

3. What stops `setStateName("dancing")` from compiling?
- Nothing because the state name is stored as a String so Java
doesn't check whether it's a valid monster state.

## 2. AFTER — US-6.1 and US-6.2

**Paste `help` in Exploring and in Combat, side by side:**

```
 -- Combat
 [Combat] you can: attack, look, use, inventory, status, history, undo, 
   help, quit
 -- Exploring
 [Exploring] you can: north, south, down, look, take, loot, drop, use, 
   inventory, pack, status, quests, history, undo, help, quit
```

**Paste an attempt to move during combat:**

```
[Combat | HP 77/80] > n

  You cannot do that in Combat mode. 
Available here: attack, look, use, inventory, status, history, undo, help, quit

```

**Paste the grep proving the guards are gone:**

```bash
grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
```

```
$ grep -rn "hasLivingMonsters" src/main/java/dungeonforge/commands/
src/main/java/dungeonforge/commands/GameContext.java:58:        if (currentRoom.hasLivingMonsters()) transitionTo(new CombatState());
```

**Paste entering and leaving the pack, and the prompt in each mode:**

```
[Exploring | HP 77/80] > pack

   ---- Your pack --- ('back' to close)

back
[Pack | HP 77/80] > 

[Exploring | HP 77/80] > 
```

## 3. AFTER — US-6.3

**Paste the grep proving `Combat` no longer knows any state name:**

```bash
grep -n "getStateName\|\"enraged\"\|\"idle\"" src/main/java/dungeonforge/core/Combat.java
```

```
lynet@LynetteV MINGW64 ~/IdeaProjects/dungeonforge (feat/US-6.2)
$ grep -n "getStateName\|\"enraged\"\|\"idle\"" src/main/java/dungeonforge/core/Combat.java

lynet@LynetteV MINGW64 ~/IdeaProjects/dungeonforge (feat/US-6.2)
```

**Paste a fight showing a monster moving through its lifecycle:**

```
[Exploring | HP 80/80] > n

  You go north.
  Ghoul is now hunting.
  Combat! 1 hostile(s). Try 'attack'.

[Combat | HP 80/80] > a

  You hit Ghoul for 10.
  Ghoul hits you for 2.
  ACHIEVEMENT: Cornered -- Force an enemy to change tactics
  Ghoul changes tactics: ranged -> aggressive.
  Ghoul is now enraged.

[Combat | HP 78/80] > a

  You hit Ghoul for 10.
  ACHIEVEMENT: First Blood -- Defeat your first monster
  Ghoul dies. +12 XP.
  The room falls quiet.

[Exploring | HP 78/80] > 
```

## 4. AFTER — US-6.4

**Paste the test output (or a demo) showing a healer that becomes aggressive when enraged:**

```
[Exploring | HP 80/80] > n

  You go north.
  Bone Priest is now hunting.
  Combat! 1 hostile(s). Try 'attack'.

[Combat | HP 80/80] > a

  You hit Bone Priest for 10.
  Bone Priest hits you for 3.
  Bone Priest changes tactics: healer -> aggressive.
  Bone Priest is now enraged.

[Combat | HP 77/80] > a

  You hit Bone Priest for 10.
  Bone Priest dies. +12 XP.
  The room falls quiet.
  
  -- Unit Test also passed.
```

**In one sentence: which one changed the other, and could it have gone the other way?**

The state changed the strategy, and we proved in the unit tests that it didn't go the other way. (i.e. strategy > state)

## 5. Tests and CI

**`mvn test` summary:**

```
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dungeonforge.CommandTest
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.170 s -- in dungeonforge.CommandTest
[INFO] Running dungeonforge.FactoryTest
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.056 s -- in dungeonforge.FactoryTest
[INFO] Running dungeonforge.SingletonTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.021 s -- in dungeonforge.SingletonTest
[INFO] Running dungeonforge.SkeletonTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.006 s -- in dungeonforge.SkeletonTest
[INFO] Running dungeonforge.StateTest
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.091 s -- in dungeonforge.StateTest
[INFO] Running dungeonforge.StrategyObserverTest
[INFO] Tests run: 17, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.037 s -- in dungeonforge.StrategyObserverTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 86, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  4.002 s
[INFO] Finished at: 2026-11-04T01:32:44-08:00
```

**Green CI URL:**

## 6. Sprint review — one sentence

> What can the project do now that it could not do last week?

The game now supports different modes that control which commands are available, 
including Exploring, Combat, Pack, and Game Over.