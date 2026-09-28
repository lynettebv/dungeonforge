# Sprint 3 Plan — Week 5 — Strategy & Observer — SUPPLIED

**Epic:** E5, Monster behaviour & events · **Patterns:** Strategy, Observer

> **Last supplied sprint plan.** In Week 6 you write your own, using the five you have now read
> as models and your own velocity data as the budget.

## Sprint Goal

> A monster's behaviour is a thing it *holds*, not a thing it *is* — and the game's systems
> find out what happened by listening, not by being called.

## Capacity — from your own numbers

| Sprint | Committed | Completed |
|---|---|---|
| Sprint 0 (Week 2) | 7 | ____ |
| Sprint 1 (Week 3) | 8 | ____ |
| Sprint 2 (Week 4) | 10 | ____ |

Three data points. If your completed column reads roughly 7, 8, 9, then **10 points is an
honest commitment**. If it reads 7, 5, 6, then this sprint is over-committed *for you* and the
right move is to say so in your retro and drop US-3.4 — which is deliberately the smallest and
most droppable story on the board.

Noticing that is worth more than finishing everything.

## Committed stories

| Issue | Story | Points | Pattern |
|---|---|---|---|
| US-3.1 | Monsters fight in different ways | 3 | Strategy |
| US-3.2 | A cornered monster changes its mind | 2 | Strategy (runtime swap) |
| US-3.3 | Quests track progress without combat knowing they exist | 3 | Observer |
| US-3.4 | New game systems plug in without touching combat | 2 | Observer (the proof) |

**Capacity:** ~10 points · **Committed:** 10 points

## Suggested order

1. **US-3.1** — the `CombatStrategy` interface and its four implementations
2. **US-3.2** — the swap in `Combat`. One line, and it's the point of the week.
3. **US-3.3** — `EventBus`, `GameEvent`, `QuestTracker`
4. **US-3.4** — `AchievementSystem` and `CombatLog`, added *without editing `Combat`*

> **Do US-3.4 last and time yourself.** If it takes more than twenty minutes, your Observer
> wiring has a leak — you'll be editing `Combat`, which is exactly what the pattern was
> supposed to prevent.

## Risks

| Risk | Mitigation |
|---|---|
| Reaching for subclassing (`AggressiveMonster`) instead of composition | Part D1 asks you to do the arithmetic first. Do it first. |
| `ConcurrentModificationException` when a listener unsubscribes during notification | Iterate a **copy** of the listener list in `publish()` |
| Putting printing in `Combat` "just for now" | A test greps for it. `Combat` publishes; something else prints. |
| Strategies that mutate the world instead of returning a decision | A strategy returns an `Action`. `Combat` carries it out. |
| Confusing Strategy with Week 8's State | You should be confused — they have nearly identical UML. Week 8 resolves it; note the confusion in D4. |

## Definition of Done

`docs/definition-of-done.md`, unchanged.

---

# ↓ Fill in at the end of the week ↓

## Calibration

| Story | Estimated | Actual hours | High, low, about right? |
|---|---|---|---|
| US-3.1 | 3 | | |
| US-3.2 | 2 | | |
| US-3.3 | 3 | | |
| US-3.4 | 2 | | |

**Points completed:** ____ · **Running velocity (sprints 0–3):** ____

> **This number is your Week 6 budget.** You'll set your own capacity from it.

## Sprint Review — one sentence


