# The Template Method Clinic — Week 10, Part D

Four experiments. D3 is the one worth doing carefully — it is the only experiment this semester
where you deliberately break your own code to see what was holding it together.

---

## D0 — The carry-over, and the limit it exposes · 4 pts

You built `disenchant` in US-7.5. This step is about what building it told you.

**Step 1.** Before US-7.5, `WeaponDecorator.unwrap()` had how many callers? ___0___
And its javadoc claimed what? ___It might be useful for a future call to disenchant a weapon.___

**Step 2.** Write the two implementations of "is there anything to remove?" side by side:

```java
// (a) the type test
if (w instanceof WeaponDecorator d) { ... }

// (b) what you actually wrote
Weapon peeled = w.peel();
        if (peeled == w) {
bus().message("The " + w.getName() + " has nothing on it to disenchant.");
        return false;
        }
```

**One sentence on why (b) is better**, in terms of what each one would need changing if a third
kind of `Weapon` were added next semester.
> Using peel() is better because each weapon handles its own behavior, so adding a new type of weapon wouldn't
> require changing DisenchantCommand to check for another type.

**Step 3.** Start from `Flaming(Frost(sword))`. Using only `disenchant`, reach a weapon that is
Flaming but not Frost.

**What happened?** ______Hmmm, this wouldn't work because disenchant removes outermost layer by layer
so flaming would be removed first, leaving frost. Then removing frost would leave the plain sword.

**Step 4 — the one that is worth the points.** US-7.4 ended with the sentence *a decorator can
only see what is underneath it*. Rewrite that sentence so it describes what you just found out
about **removal** rather than about damage numbers.

> A decorator can only be removed from the outside, so unfortunately you can't remove an inner enchantment
> without removing the outer ones first.

**Step 5.** Name one feature a game might reasonably want that this limit makes awkward, and say
what you would have to do to support it. *(You do not have to build it. "Decorator is the wrong
tool for that" is an acceptable and correct answer, and saying it about a pattern you just spent
a week on is the point.)*

> Well, this would affect being choosy about removing enchantments. You would be stuck on removing everything
> and then adding what you want instead of selecting what you want to remove specifically.
---

## D1 — The Template Method you already wrote · 5 pts

You have been using this pattern since Week 7. You did not know its name.

**Step 1.** Open `commands/AbstractCommand.java` and read `execute()`.

**Step 2.** Fill this in from that file, not from the reading.

| | In `AbstractCommand` |
|---|---|
| The template method | execute() |
| Is it final? | yes |
| The primitive operation subclasses must implement | doExecute() |
| How many steps does the template fix the order of? | 3 |
| What happens to a subclass that forgets a step? |  It can't skip shared steps , if it doesn't implement doExecute() then it won't compile. |

**Step 3.** `AbstractCommand.execute()` snapshots, acts, and then lets the monsters respond — **in
that order, always.**

**Name one bug that ordering makes impossible.** *(Hint: think about what undo needs, and when
the snapshot has to be taken.)*
> This fixed order prevents a command from taking its snapshot after making chases,
> which woud cause undo to restore the wrong state.

**Step 4.** In one sentence: why did Week 7 need this pattern before Week 10 taught it?

> *Answer:* Week 7 needed Template Method because every command had to follow the same execution order
> while still allowing each command to perform its own specific action.

---

## D2 — Three copies, three different mistakes · 6 pts

**Step 1.** From Part 2 of your evidence sheet, reproduce the count:

| Builder | Steps it performs | Steps it skips |
|---|---|---|
| `buildCorridorLevel` | 5 | None |
| `buildCaveLevel` | 1-4 | Step 5 |
| `buildRingLevel` | 5, with error in Step 4 | None, but it populates entrance room |

**Step 2.** Notice that no two of them skip the *same* step. Finish this sentence:

> With N copies of one algorithm, the set of steps that *some* copy gets wrong grows
> ___larger___, because each copy can introduce different mistakes or accidentally skip important steps.

**Step 3.** A tempting fix: add a comment at the top of each method listing the five steps in
order. Say in one sentence why that does not work.
> Comments can't fix the problem because they simply describe what the code should do without having
> any power to stop the programmer from actually skipping the step.

**Step 4.** A second tempting fix: extract the five steps into five helper methods and have each
builder call them in order. That is genuinely better than what is there now.

**But it does not solve the problem.** Say why in one sentence, then name the thing Template
Method adds that this fix does not.

> *(This is the exam question for this week. The answer is one word long and it appears on the
> signature of `generate()`.)*

>final
---

## D3 — Remove the `final` and watch · 6 pts 🔨

**Do this after US-8.2 is finished and committed, on a scratch branch you will throw away.**

**Step 1.** Confirm you are starting clean, on both seeds.

```bash
mvn -q exec:java -Dexec.args="--report"
mvn -q exec:java -Dexec.args="--report --seed=3"
```

Record the orphan and spawn totals: default ___0___ / ___0___   seed 3 ___0___ / ___0___

**Step 2.** Remove `final` from `LevelGenerator.generate(...)`.

**Step 3.** Write one more shape — call it `SloppyGenerator` — that extends `LevelGenerator`,
implements `connectRooms` however you like, **and overrides `generate()`** to create rooms,
connect them, populate them, and stop there.

You will find you cannot call the template's private steps, so you will have to write your own
versions. **Write them badly on purpose** — populate from index 0, skip the reachability check.
That is the whole point of the experiment.

**Step 4.** Register it and run `--report` again on both seeds.

| | orphans | spawn |
|---|---|---|
| with `final` | 0 | 0 |
| without `final` | 3 | 1 |

**Step 5.** Answer three questions.

1. Which of your existing tests failed? ___theTemplateMethodIsFinalSoNoSubclassCanReorderTheSteps()___
2. Which test told you the `final` was gone? ___theTemplateMethodIsFinalSoNoSubclassCanReorderTheSteps()___
3. Did the compiler complain at any point? ______

**Step 6.** Put the `final` back, delete `SloppyGenerator`, and run `mvn test`.

**Step 7.** One paragraph. You have just demonstrated that `final` on a template method is
load-bearing. Explain what it is bearing the load *of* — and note that the thing it protects is
not a behaviour but an **agreement**.

> **Why this experiment exists.** Every year somebody argues that `final` is over-restrictive and
> that a subclass should be trusted to call the steps in order. That argument is exactly what
> produced `buildCaveLevel`. You have now run the experiment instead of having the argument.
> 
> The final keyword protects the agreement that every level generator must follow the same exact steps in the same order.
> Without this, the subclasses could override generate() and skip vital steps such as checking reachability or keeping
> the entrance safe for players. The code could sitll compile even with some issues in the level generation.
> This shows how final protects the agreement between the superclass and its subclasses, rather than just one specific behavior.

---

## D4 — What did not change, and what you did not write · 4 pts

**Step 1.** List every file outside `generate/` that you modified this sprint.

```bash
git diff --stat main -- src/main/java/dungeonforge/ ':!*generate*'
```

**Step 2.** Fill this in.

| File | Lines changed this sprint |
|---|---|
| `core/GameWorld.java` | 0 |
| `core/Room.java` | 0 |
| `core/DungeonLevel.java` | 0 |
| `core/Combat.java` | 0 |
| `commands/MoveCommand.java` | 0 |
| `core/DungeonReport.java` | 0 |

**Step 3.** `DungeonReport` does its own breadth-first search. So does `LevelGenerator`. That is
duplication, and this week was about removing duplication.

**Should `DungeonReport` call the generator's version instead? Argue one way in two sentences.**

> *(There is a defensible answer in each direction, and the marks are for the argument. The case
> against sharing: a checker that shares code with the thing it checks cannot catch that code
> being wrong. The case for sharing: two definitions of "reachable" can drift, which is the exact
> disease this week diagnosed.)*

> I personally think DungeonReport should keep its own breadth-first search because it's supposed ot independently
> check whether the generated levels are correct. If both used the same code, a mistake could get lost in it.

**Step 4.** The honest half. Head First, p. 302, is direct about what this pattern costs:
subclasses cannot change the order of the steps.

**Name one level shape you can imagine wanting, that the current template cannot express.** Then
say what you would change about `generate()` to allow it — and what you would lose.
> A maze that needs to populate rooms before connecting them might not work with this current template.
> We could make generate() allow a different order of steps, but we would lose the guarantee that every shape
> follows the same algorithm.

---

## Turning it in

D1–D4 go in `docs/evidence.week10.md` and in the PR description for the story they belong to.
Command output, not paraphrases of command output. **A number without the command that produced
it earns half marks.**
