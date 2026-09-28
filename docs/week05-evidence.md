# Week 5 Evidence

## 1. BEFORE — the problem

On `main`, before any code:

```bash
mvn -q exec:java > before.txt
```

**Open `Combat.monsterActs()`. Paste it:**

```java

```

**Every monster in the game does the same thing. Paste four consecutive combat lines from
`before.txt` involving different species, showing they're indistinguishable:**

```

```

**Count the `System.out` calls in `Combat.java`:** ____

**In one sentence: if I asked you to add an achievement system today, which file would you
have to edit?**


## 2. AFTER — US-3.1, monsters fight differently

**Paste one line each showing a ranged, a healer, and a skittish monster acting:**

```

```

**Which file decides that a Bone Priest is a healer? Is it a `.java` file?**


## 3. AFTER — US-3.2, the runtime swap

**Paste a "changes tactics" line from your log:**

```

```

**Paste the line of code in `Combat` that causes it:**

```java

```

## 4. AFTER — US-3.3 and US-3.4, listeners

```bash
grep -n "QuestTracker\|AchievementSystem\|CombatLog\|System.out" src/main/java/dungeonforge/core/Combat.java
```

**Paste the output. It should be empty:**

```

```

**Paste your quests and achievements output:**

```

```

**Paste `git diff --stat` for the commit that added `AchievementSystem` and `CombatLog`.
Did `Combat.java` appear in it?**

```

```

## 5. Tests and CI

**`mvn test` summary:**

```

```

**Green CI check URL:**


## 6. Sprint review — one sentence

> What can the project do now that it could not do last week?


