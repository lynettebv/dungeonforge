# Lab 4 — Factory & Abstract Factory — Starter

## What you're getting

Your Week 3 code, with Week 4's problem now visible.

`GameWorld.spawn()` is eight lines that contain the entire week:

```java
private Monster spawn(int depth) {
    String[] species = {"Skeleton", "Crypt Rat", "Wight", "Bone Priest"};
    String pick = RandomSource.getInstance().pick(species);
    return new Monster(pick, 12 + depth * 4, 4 + depth, 6 + depth * 3);
}
```

- the species list is **here**, so adding a monster means editing `GameWorld`
- the stat formula is **here**, so rebalancing means editing `GameWorld`
- construction is **here**, so `GameWorld` is coupled to a concrete class
- every level draws from the same four species, so **themes are impossible**

And `Room` has crypt prose hardcoded into it, so a forge level sounds like a crypt.

## Provided for you

| File | Why |
|---|---|
| `src/main/resources/data/monsters.json` | 15 monster blueprints across 3 themes. **Content, not code** — you shouldn't spend this week inventing lore. |
| `items/` — `Item`, `Weapon`, `Armor`, `Potion`, `Treasure`, `Chest` | So a `ThemeKit` has loot to hand back. Deliberately minimal; Week 9 decorates these and Week 13 turns them into a Composite. |
| `GameConfig.readResource(...)` | Now public, so your factory can load `monsters.json` the same way config is loaded |

## Do this first

```bash
mvn -q exec:java > before.txt
grep -rn "new Monster(" src/main/java
```

Fill in `docs/evidence.md` §1. **It's graded**, and it matters: if you can't show the problem,
you're applying the pattern because you were told to.

## What you'll build

| Package | Class | Pattern |
|---|---|---|
| `factory` | `MonsterDef`, `MonsterFactory` | Simple Factory *(an idiom, NOT one of the GoF patterns)* |
| `factory` | `ThemeKit` + `Crypt`/`Forge`/`Frost` kits | **Abstract Factory** |
| `factory` | `ThemeRegistry` | the one place that knows which themes exist |
| `factory` | `RoomPopulator` + `Standard`/`Treasure`/`Boss` | **Factory Method** |

Then delete `spawn()` and the `FLAVORS` array. **The deletions are the deliverable.**

## The one design rule for this week

> `GameWorld` must not name a single monster species, item, or piece of prose.

If it does, the patterns aren't finished. Part D asks you to prove it by adding a fourth theme
and counting the files you had to touch.

## Definition of Done

`docs/definition-of-done.md`, unchanged.
