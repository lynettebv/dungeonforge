# DungeonForge

CIS-18 semester project — a turn-based dungeon crawler built one design pattern at a time.

Textbook: Freeman & Robson, *Head First Design Patterns*, 2nd ed.

## Status

**Week 2 — walking skeleton.** The program compiles, runs, prints a banner and passes its
tests. There is no game yet. That starts in Week 3 with the Singleton.

## Run it

```bash
mvn test          # should be green
mvn exec:java     # prints the banner
```

Requires **JDK 21**.

## Project process

| Thing | Where |
|---|---|
| Definition of Done | `docs/definition-of-done.md` *(supplied)* |
| Product backlog | `docs/backlog.md` *(supplied)* and GitHub Issues |
| This week's sprint | `docs/sprint-00-plan.md` *(supplied)* |
| Next week's sprint | `docs/sprint-01-plan.md` *(supplied)* |
| Board | GitHub Projects |
| Retrospectives | `docs/retro/` |
| Backlog seeding | `bash scripts/seed-backlog.sh` |

> Through Week 5 the Agile artifacts are **supplied**, and your job is to run the sprint and
> read them critically. You write your first user stories in Week 6, once you have read
> about fifteen good ones.

### Branch naming

```
week-NN-pattern     pattern work        e.g. week-03-singleton
feature/<name>      a capability
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
| 3 | Singleton | | |
| 4 | Factory Method / Abstract Factory | | |
| 5 | Strategy / Observer | | |
| 7 | Command | | |
| 8 | State | | |
| 9 | Decorator | | |
| 10 | Template Method | | |
| 12 | Adapter / Facade | | |
| 13 | Composite / Iterator | | |
| 14 | Pipeline / Proxy | | |
| 15 | MVC (compound) | | |
