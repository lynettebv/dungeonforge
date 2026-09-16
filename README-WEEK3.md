# Lab 3 — Singleton — Starter

## What you're getting

The DungeonForge **core domain** from Week 1, written the way a reasonable person writes
code before they know the pattern. It compiles. It runs. It works.

It also has:
- **hardcoded tunable numbers** in three different files
- **three separate `Random` objects**, none of which can be seeded

Neither is a bug today. Both become bugs the moment somebody says "I hit a crash on level 2,
here's my save file" — because you cannot reproduce their dungeon, and you cannot rebalance
the game without recompiling.

## Do this first, before any code

```bash
mvn -q exec:java > run1.txt
mvn -q exec:java > run2.txt
diff run1.txt run2.txt
```

Different every time. Save that diff — `docs/evidence.md` asks for it.

## Then

Pull **US-1.1** off your Sprint Backlog and follow `docs/sprint-01-plan.md`.

## Files you'll change

| File | Why |
|---|---|
| `core/Player.java` | starting HP, attack, defense, carry capacity |
| `core/Monster.java` | `Random` #1 |
| `core/Room.java` | `Random` #2 |
| `core/GameWorld.java` | `Random` #3, plus depth / rooms / monsters-per-room |
| `Main.java` | print the seed so it's visible |

## Files you'll add

| File | Why |
|---|---|
| `config/GameConfig.java` | US-1.1 |
| `config/RandomSource.java` | US-1.2 |
| `src/main/resources/data/config.json` | US-1.1 |
| `SingletonTest.java` | US-1.3 |

`config/Json.java` is **provided** — a tiny dependency-free JSON reader, so the project still
has zero external dependencies. Read it if you're curious; you don't need to change it.

## Reading a resource from the classpath

The one piece of Java this lab needs that you may not have seen:

```java
try (InputStream in = GameConfig.class.getResourceAsStream("/data/config.json")) {
    if (in == null) return;                  // no file: fall back to defaults
    String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    // ... parse it
}
```

Note the leading `/` and that the file lives under `src/main/resources/`, which Maven copies
onto the classpath at build time. If `in` comes back `null`, the file is not where you think.

## Definition of Done

`docs/definition-of-done.md`, unchanged from Week 2 — including the box you repaired.
