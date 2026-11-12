# DungeonForge

CIS-18 semester project — a turn-based dungeon crawler built one design pattern at a time.

Textbook: Freeman & Robson, *Head First Design Patterns*, 2nd ed.

## Status

**Week 9 — Decorator Pattern.** DungeonForge is now a playable turn-based dungeon crawler with exploration, combat, inventory management, quests, and weapon enchantments.

The game uses multiple design patterns to organize its functionality. The latest addition is the Decorator Pattern, which allows weapon enchantments to be stacked without creating separate classes for every possible combination.

## Run it

```bash
mvn test          # should be green, runs automated tests
mvn exec:java     # launches DungeonForge
```

Requires **JDK 21**.

## Project process

| Thing | Where |
|---|---|
| Definition of Done | `docs/definition-of-done.md` *(supplied)* |
| Product backlog | `docs/backlog.md` *(supplied)* and GitHub Issues |
| Board | GitHub Projects |
| Retrospectives | `docs/retro/` |
| Sprint evidence | `docs/evidence.week9.md` |
| Backlog seeding | `bash scripts/seed-backlog.sh` |

> Through Week 5 the Agile artifacts are **supplied**, and your job is to run the sprint and
> read them critically. You write your first user stories in Week 6, once you have read
> about fifteen good ones.

### Branch naming

```
week-NN-pattern     pattern work        e.g. week-09-decorator
feature/<name>      a capability
feat/<name>         a feature or user story
fix/<name>          a defect
chore/<name>        build, CI, tooling
docs/<name>         documentation
```

### Commit messages

[Conventional Commits](https://www.conventionalcommits.org/): `feat:` `fix:` `docs:`
`test:` `refactor:` `chore:`

### The weekly loop

1. Pull the top story off the Sprint Backlog
2. `git switch -c week-NN-pattern`
3. Build it in small commits
4. Open a PR — the template *is* the Definition of Done
5. Wait for CI to go green
6. Self-review, merge, tag
7. Retro

## Pattern map

Filled in as the semester goes. By Week 16 this table is the portfolio piece.

| Week | Pattern | Where it lives | Why it fits |
|---|---|---|---|
| 3 | Singleton | `GameConfig`, `RandomSource` | Ensures shared configuration and random number generation use a single instance. |
| 4 | Factory Method / Abstract Factory | `MonsterFactory`, `RoomPopulator`, `ThemeKit` | Creates different monsters and themed rooms without depending on specific classes. |
| 5 | Strategy / Observer | `CombatStrategy`, `EventBus`, `QuestTracker` | Allows monsters to change combat strategies and other systems to respond to game events. |
| 7 | Command | `Command`, `CommandHistory`, command classes | Encapsulates player actions as objects and supports undo and macros. |
| 8 | State | `GameState`, `MonsterState`, concrete state classes | Changes available commands and monster behavior based on the current state. |
| 9 | Decorator | `WeaponDecorator`, `FlamingWeapon`, `FrostWeapon`, `VampiricWeapon`, `KeenWeapon` | Allows enchantments to be stacked without needing separate classes for every combination. |
| 10 | Template Method | | |
| 12 | Adapter / Facade | | |
| 13 | Composite / Iterator | | |
| 14 | Pipeline / Proxy | | |
| 15 | MVC (compound) | | |
| 12 | Adapter / Facade | | |
| 13 | Composite / Iterator | | |
| 14 | Pipeline / Proxy | | |
| 15 | MVC (compound) | | |
