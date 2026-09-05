# Product Backlog — seed

Create one GitHub issue per row below, labelled `epic`. Paste the goal into the body.

**Read each one before you paste it.** In Week 16 you will be asked which epic you
under-estimated most, and the answer is more interesting if you looked at them in Week 2.

| # | Epic | Week | Pattern(s) | Goal (one sentence) |
|---|---|---|---|---|
| E1 | Core domain | 1 | none | Entities, items and rooms exist as plain objects, with deliberate flaws. |
| E2 | Process infrastructure | 2 | none | A repo, board, CI pipeline and Definition of Done that carry the whole semester. |
| E3 | Configuration & randomness | 3 | Singleton | Every tunable value and every random roll comes from one seeded source. |
| E4 | Content creation | 4 | Factory Method, Abstract Factory | Monsters, loot and room contents are created without `new` appearing outside a factory. |
| E5 | Monster behaviour & events | 5 | Strategy, Observer | Monsters change tactics at runtime, and quests react to combat without combat knowing quests exist. |
| E6 | Player actions | 7 | Command | Every action is an object, giving undo, macros and a replay log. |
| E7 | Game modes | 8 | State | Exploring, combat and inventory each allow only their own verbs. |
| E8 | Item enchantment | 9 | Decorator | Enchantments stack in any combination without a class explosion. |
| E9 | Turn & level algorithms | 10 | Template Method | Turn order and level generation have fixed skeletons with varying steps. |
| E10 | Legacy data & simplified API | 12 | Adapter, Facade | A hostile third-party bestiary is consumed unchanged, and `main()` shrinks to a handful of lines. |
| E11 | Nested inventory & traversal | 13 | Composite, Iterator | Bags go inside bags, and callers loop over them without knowing that. |
| E12 | Damage calculation & access control | 14 | Pipeline, Proxy | Damage is a chain of testable stages; expensive and privileged things are guarded. |
| E13 | Presentation layer | 15 | MVC (Observer + Strategy + Composite) | The model performs no I/O, and a second view can be added without touching it. |
| E14 | Release & documentation | 16 | all | A tagged release with UML, a README and a pattern-to-class map. |

> Weeks 6 and 11 are exam and refactor sprints. They carry no new epic on purpose — they are
> the only slack in the semester. Use them to pay down anything that slipped.

## How to decompose an epic

1. Ask: **what can a user or a designer DO afterwards that they could not before?** Each
   distinct answer is a candidate story.
2. Check it against INVEST.
3. Break each story into sub-tasks — those *may* name classes and methods. Stories may not.
4. Estimate the stories, not the sub-tasks.

## Worked example — E3 decomposed

- **US-3.1** As a game designer, I want tunable numbers in one config file, so that I can rebalance without recompiling. *(3 pts)*
- **US-3.2** As a developer, I want the same seed to produce the same dungeon, so that bugs are reproducible. *(3 pts)*
- **US-3.3** As a developer, I want a test proving only one config instance can exist, so that the constraint is enforced rather than hoped for. *(2 pts)*

Write your own. Yours do not have to match these.
