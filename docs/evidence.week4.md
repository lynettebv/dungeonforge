# Week 4 Evidence — before and after

## 1. BEFORE — the problem, demonstrated

Do this **before writing any code**, on `main`:

```bash
mvn -q exec:java > before.txt
grep -rn "new Monster(" src/main/java
```

**How many places construct a Monster?** __1__   **Which class?** __GameWorld.java__

**Look at `before.txt`. List the species that appear on level 1, and on level 3:**

- Level 1: Skeleton, Bone Priest, Crypt Rat, & Wight
- Level 3: Bone Priest & Wight

**In one sentence: what is the player's experience of descending from level 1 to level 3?**

The experience doesn't change. All levels have the same Monsters. Only Attack numbers change.

**Open `Room.java`. Whose prose is in the `FLAVORS` array — and what would a forge level
sound like today?**


## 2. AFTER — US-2.1, monsters come from data

```bash
grep -rn "new Monster(" src/main/java
```

**Paste it. There should be exactly one, inside the factory:**

```

```

**Add a monster to `monsters.json` — invent one. Paste the JSON line, and confirm: did you
change any `.java` file to make it appear in the game?**

```

```

## 3. AFTER — US-2.2, levels have character

**Paste the level headers from your run:**

```

```

**Paste one room from each level, showing monsters and loot:**

```

```

**Search your output for a crypt monster on the forge level. Paste the result (it should find
nothing):**

```

```

## 4. AFTER — US-2.3, rooms differ

**Paste one standard room, one treasure room, and the boss room:**

```

```

**In `RoomPopulator`, which method is `final` and which is `abstract`? Why that way round and
not the other?**


## 5. Tests and CI

**`mvn test` summary:**

```

```

**Green CI check URL:**


## 6. The sprint review sentence

> What can the project do now that it could not do last week?


