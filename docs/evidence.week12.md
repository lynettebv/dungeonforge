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

**How many files share the misspelling?** ______

**It works perfectly. In one sentence: what does it cost, and when?**


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

**Listener classes in `events/`:** ______  **`bus.subscribe` calls in `Main`:** ______

**Those two numbers are equal and that is not reassuring. Why not?** *(Look at what the fourth
subscription actually subscribes, and where that class lives.)*


**Which listener class is never subscribed?** ______

**Open it. What does it do, and when was it written?**


**Now the sentence. Put both counts and the consequence in one sentence.** *(This is the most
important sentence you will write this week. Two points.)*

> ____________________________________________________________________

**Nobody broke it. In one sentence: what kind of mistake is this, if it is not a bug?**


---

## 2. BEFORE — the two games

```bash
printf 'north\nlook\nnorth\nlook\n' > demo.txt
mvn -q exec:java -Dexec.args="--script=demo.txt"
```

**Paste the scripted output:**

```

```

Now run `mvn -q exec:java` and type the same four commands by hand.

**Paste that output:**

```

```

**They differ. Name the file, the method and the line that is the difference.** ______

**Put the two turn sequences side by side:**

| | the turn |
|---|---|
| `gameLoop()` | |
| `runScript()` | |

**What can a script do that a player cannot?**


**`runScript`'s javadoc has been promising something since Week 7. Quote it, and say whether it
is true.**


---

## 3. BEFORE — the counting

```bash
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'ctx.syncState()'
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'history.push('
python3 scripts/codeonly.py src/main/java/dungeonforge/Main.java | grep -c 'DamageMeter'
awk '/public static void main/{on=1} on{n++} on&&/^    }$/&&n>3{print n; exit}' src/main/java/dungeonforge/Main.java
```

| | Before | After |
|---|---|---|
| `main()` lines | | |
| `ctx.syncState()` in `Main` | | |
| copies of the turn sequence | | |
| `DamageMeter` mentioned outside its own file | | |
| listener classes subscribed, of 4 | | |
| monster blueprints available | | |
| a script can walk past a live hostile | | |

*(Fill the After column at the end of the sprint. **Nothing in it may be a guess.**)*

---

## 4. AFTER — the two receipts

### US-9.1, the Adapter

```bash
git diff --stat HEAD~1          # on the us-9.1 commit
diff src/main/java/dungeonforge/legacy/LegacyBestiary.java  <the file you were given>
diff src/main/java/dungeonforge/factory/MonsterFactory.java <the file you started with>
python3 scripts/codeonly.py src/main/java/dungeonforge/factory/MonsterFactory.java \
  | grep -ciE 'bestiary|csv|MOB_ID|HIT_DICE'
```

**Paste all four.**

```

```

**Two of those commands are the deliverable. Which two, and why?**


**`blueprintCount()` before:** ______ **after:** ______

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


