# Week 3 Evidence — the before-and-after

> Your Definition of Done asks for evidence that the acceptance criteria are met. This file
> is where it goes. Fill it in as you work, not at the end.

## 1. BEFORE — the problem, demonstrated

Do this **before writing any code**:

```bash
mvn -q exec:java > run1.txt
mvn -q exec:java > run2.txt
diff run1.txt run2.txt
```

**Paste a few lines of the diff:**

```
Compare-Object (Get-Content run1.txt) (Get-Content run2.txt)

InputObject                                                     SideIndicator
-----------                                                     -------------
L1R0: Wight (18/18 HP, ATK 4)                                   =>           
L1R1: (empty)                                                   =>           
L1R3: Skeleton (17/17 HP, ATK 5)                                =>           
L1R4: Crypt Rat (18/18 HP, ATK 4)                               =>           
L1R5: (empty)                                                   =>           
L1R6: (empty)                                                   =>           
L1R7: Wight (17/17 HP, ATK 4)                                   =>           
L2R0: Wight (22/22 HP, ATK 5)  Crypt Rat (22/22 HP, ATK 7)      =>           
L2R1: Crypt Rat (22/22 HP, ATK 7)  Crypt Rat (18/18 HP, ATK 5)  =>           
L2R2: Skeleton (19/19 HP, ATK 7)                                =>           
L2R4: Bone Priest (20/20 HP, ATK 5)                             =>           
L2R5: Crypt Rat (20/20 HP, ATK 7)  Skeleton (21/21 HP, ATK 5)   =>           
L2R6: Bone Priest (19/19 HP, ATK 6)                             =>           
L2R7: (empty)                                                   =>           
L3R0: Skeleton (25/25 HP, ATK 8)                                =>           
L3R1: Skeleton (22/22 HP, ATK 8)                                =>           
L3R2: Wight (25/25 HP, ATK 7)  Skeleton (26/26 HP, ATK 7)       =>           
L3R4: (empty)                                                   =>           
L3R5: Skeleton (26/26 HP, ATK 8)  Skeleton (22/22 HP, ATK 8)    =>           
L3R6: Crypt Rat (24/24 HP, ATK 8)                               =>           
L3R7: Skeleton (25/25 HP, ATK 6)  Wight (24/24 HP, ATK 7)       =>           
Total monsters: 22                                              =>           
L1R0: Crypt Rat (14/14 HP, ATK 4)                               <=           
L1R1: Skeleton (16/16 HP, ATK 5)  Skeleton (15/15 HP, ATK 6)    <=           
L1R3: Wight (15/15 HP, ATK 6)  Skeleton (14/14 HP, ATK 4)       <=           
L1R4: Bone Priest (17/17 HP, ATK 5)                             <=           
L1R5: Skeleton (14/14 HP, ATK 5)                                <=           
L1R6: Crypt Rat (18/18 HP, ATK 6)  Skeleton (16/16 HP, ATK 4)   <=           
L1R7: Bone Priest (16/16 HP, ATK 5)  Skeleton (14/14 HP, ATK 5) <=           
L2R0: Crypt Rat (21/21 HP, ATK 7)  Crypt Rat (18/18 HP, ATK 5)  <=           
L2R1: Bone Priest (20/20 HP, ATK 7)                             <=           
L2R2: (empty)                                                   <=           
L2R4: Bone Priest (18/18 HP, ATK 5)                             <=           
L2R5: Wight (18/18 HP, ATK 6)  Crypt Rat (19/19 HP, ATK 5)      <=           
L2R6: Wight (21/21 HP, ATK 5)  Crypt Rat (20/20 HP, ATK 5)      <=           
L2R7: Crypt Rat (20/20 HP, ATK 5)  Crypt Rat (19/19 HP, ATK 5)  <=           
L3R0: Crypt Rat (22/22 HP, ATK 7)                               <=           
L3R1: (empty)                                                   <=           
L3R2: Crypt Rat (23/23 HP, ATK 8)                               <=           
L3R4: Bone Priest (25/25 HP, ATK 7)                             <=           
L3R5: (empty)                                                   <=           
L3R6: Skeleton (25/25 HP, ATK 8)                                <=           
L3R7: Bone Priest (22/22 HP, ATK 7)                             <=           
Total monsters: 26                 
```

**How many separate `Random` objects did you find in the starter?** __3__
(`grep -rn "new Random(" src/main/java`)
```bash
src/main/java/dungeonforge/core/GameWorld.java:19:    private final Random random = new Random();
src/main/java/dungeonforge/core/Monster.java:16:    private static final Random RNG = new Random();
src/main/java/dungeonforge/core/Room.java:15:    private final Random rng = new Random();
```
**In one sentence: why does that make a bug report like "the boss room on level 2 was empty"
impossible for me to act on?**
This would be an impossible bug to act on because you could never duplicate
that bug report in order to deal with it.

## 2. AFTER — US-1.1, settings live in one place

```bash
grep -rn "playerStartingHp\|60\|new Random(" src/main/java/dungeonforge/core
```

**Paste the output. AC2 wants zero hardcoded literals outside the config class:**

```
src/main/java/dungeonforge/core/GameWorld.java:21:    private final Random random = new Random();
src/main/java/dungeonforge/core/Monster.java:16:    private static final Random RNG = new Random();
src/main/java/dungeonforge/core/Room.java:15:    private final Random rng = new Random();
```

**Change `playerStartingHp` in `config.json` to 200, run, and paste the player line:**

```bash
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.2.0

Delver  HP 200/200  ATK 18  DEF 3  Gold 0  XP 0  Carry 60.0kg

-- Level 1 --
L1R0: Crypt Rat (17/17 HP, ATK 4)
L1R1: Skeleton (16/16 HP, ATK 5)  Crypt Rat (15/15 HP, ATK 6)
L1R2: Bone Priest (18/18 HP, ATK 5)
L1R3: (empty)
L1R4: Wight (14/14 HP, ATK 5)  Bone Priest (18/18 HP, ATK 6)
L1R5: Bone Priest (17/17 HP, ATK 5)
L1R6: Wight (14/14 HP, ATK 6)
L1R7: (empty)
```

**Rename `config.json` to `config.json.bak`, run again, and paste what happens (AC4):**

```bash
=========================================
        D U N G E O N F O R G E
  A Head First Design Patterns project
=========================================
  version 0.2.0

Delver  HP 80/80  ATK 8  DEF 2  Gold 0  XP 0  Carry 60.0kg

-- Level 1 --
L1R0: Crypt Rat (16/16 HP, ATK 5)  Wight (16/16 HP, ATK 4)
L1R1: Wight (18/18 HP, ATK 6)
L1R2: Wight (16/16 HP, ATK 6)
L1R3: Wight (15/15 HP, ATK 4)
L1R4: Skeleton (17/17 HP, ATK 5)
L1R5: Bone Priest (14/14 HP, ATK 5)
L1R6: Bone Priest (15/15 HP, ATK 5)  Bone Priest (17/17 HP, ATK 6)
L1R7: Skeleton (16/16 HP, ATK 4)
```

## 3. AFTER — US-1.2, the same seed produces the same dungeon

```bash
mvn -q exec:java > after1.txt
mvn -q exec:java > after2.txt
diff after1.txt after2.txt && echo "IDENTICAL"
```

**Result:**

```bash
$ diff after1.txt after2.txt && echo "IDENTICAL"
IDENTICAL
```

**Now a different seed (AC4). Paste enough to show the world changed:**

```bash
$ diff after2.txt after3.txt                    
10,13c10,13
< L1R0: Bone Priest (17/17 HP, ATK 6)
< L1R1: Wight (16/16 HP, ATK 5)  Crypt Rat (15/15 HP, ATK 5)
< L1R2: (empty)
< L1R3: Crypt Rat (14/14 HP, ATK 6)  Skeleton (17/17 HP, ATK 4)
---
> L1R0: Wight (16/16 HP, ATK 5)
> L1R1: Skeleton (14/14 HP, ATK 4)  Bone Priest (15/15 HP, ATK 5)
> L1R2: Crypt Rat (16/16 HP, ATK 5)  Crypt Rat (14/14 HP, ATK 4)
> L1R3: (empty)
```

## 4. AFTER — US-1.3, the rule is enforced

**Paste your `mvn test` summary:**

```
 mvn test
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------< edu.redwoods.cis18:dungeonforge >-------------------
[INFO] Building DungeonForge 0.2.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.4.0:resources (default-resources) @ dungeonforge ---
[INFO] Copying 2 resources from src\main\resources to target\classes
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ dungeonforge ---
[INFO] Nothing to compile - all classes are up to date.
[INFO] 
[INFO] --- resources:3.4.0:testResources (default-testResources) @ dungeonforge ---
[INFO] skip non existing resourceDirectory C:\Users\lynet\IdeaProjects\dungeonforge\src\test\resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ dungeonforge ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 2 source files with javac [debug release 21] to target\test-classes
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ dungeonforge ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dungeonforge.SingletonTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.062 s -- in dungeonforge.SingletonTest
[INFO] Running dungeonforge.SkeletonTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s -- in dungeonforge.SkeletonTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.079 s
[INFO] Finished at: 2026-09-17T01:48:46-07:00
[INFO] ------------------------------------------------------------------------
```

**Paste the URL of the green CI check on your pull request:**


## 5. The one-line summary for your Sprint Review

> What can the project do now that it could not do last week?


