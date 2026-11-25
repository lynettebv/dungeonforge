# The Adapter / Facade Clinic — Week 12, Part D

Fifteen points. Graded on **measurement and precision**, not prose. The rule from Week 9 still
holds: **a number without the command that produced it earns half marks.**

---

## D1 — Three classes with the same shape · 5 pts 🔎

**This is the highest-value step in the clinic, and it is the most common patterns-interview
question in existence.** You have written all three answers.

**Step 1.** Put these three declarations in a table, from your own repo, with file and line:

```java
class WeaponDecorator        implements ___Weapon___  { ___protected final Weapon___ wrapped; }   // Week 9
class LegacyBestiaryAdapter  implements ___MonsterSource___  { ___private final LegacyBestiary___ adaptee; }   // this week
class GameSession          // this one implements nothing!                     { ___8 fields___ ... }        // this week
```

**Step 2.** For the first two, answer one question: **is the held type the same as the
implemented type?**

| | implements | holds | same type? |
|---|---|---|---|
| `WeaponDecorator` | Weapon | Weapon  | yes |
| `LegacyBestiaryAdapter` | MonsterSource | LegacyBestiary | no |

**Step 3.** That answer is the whole distinction. Write it as one sentence beginning *"A
decorator implements the same interface as the thing it holds, which is why…"*

> __________________________A decorator implements the same interface as the thing it holds, which is why it's able to stack even though an adapter doesn't, but the adapter can translate.__________________________

**Step 4 — the consequence, not the definition.** `Flaming(Frost(sword))` is a legal weapon.
Write down what `new LegacyBestiaryAdapter(new LegacyBestiaryAdapter(b))` does.

**Try it. Paste the compiler error.**

```
error: incompatible types: LegacyBestiaryAdapter cannot be converted to LegacyBestiary
```

**Now say why that error is a property of the pattern and not a limitation of your code.**
> An adapter's output type does not equal its input type, so it can't be its own adaptee. Flaming(Frost(sword))
> works because a decorator's output type is its input type.

**Step 5.** `GameSession` holds eight things and implements nothing. In one sentence, what makes
it a facade rather than "a class with a lot of fields"?
> Every class it holds is still public and still usable directly. A facade adds a door, it doesn't lock the others.

---

## D2 — The missing column · 3 pts

**Step 1.** Name the three honest options for a target field with no source.
1. refuse to record it
2. derive it & state it
3. default silently

**Step 2.** You took one. Paste the method.

```java
private int experienceFor(int hp, int attack, String legacyId) {
        notes.add(legacyId + ": xp derived (no xp column in the export)");
        return Math.max(1, hp / 2 + attack);
    }
```

**Step 3.** Now read Head First p. 249 — `EnumerationIterator.remove()`. **Which of your three
options did Freeman & Robson take, and why was theirs available when yours was not?**
> they refused, remove() throws. This was available to them because Iterator lets a method
> fail at runtime. It's not available to us because MonsterDef needs an int at construction, 
> so there is no later moment to fail in.

**Step 4.** Try `Arrays.asList("a","b").add("c")`. **Paste the exception.** Which option is that?

> java.lang.UnsupportedOperationException. this is also refuse!

**Step 5 — the one that is worth the points.** A colleague changes your derivation from
`hp / 2 + attack` to `hp / 3 + attack`. Every test still passes.

**Is that a problem? Answer yes or no and defend it in two sentences.**

> Yes. Nothing in the file constrains the number, so no test can catch a change to it.
> The derivation is a policy with no owner and no test, which is why translationNotes() has to
> make it visible to humans.
---

## D3 — The listener nobody subscribed · 4 pts 🔨

**Step 1.** Before US-9.2, how many times did `DamageMeter` appear anywhere outside its own
file? ___0___ **Command:** ___grep -rl 'DamageMeter' src/main/java returns only its own file___

**Step 2.** It compiled, it was correct, and it had never run. **Name every mechanism in this
project that could have caught it, and say why each one did not:**

| Mechanism | Why it did not catch this |
|---|---|
| the Java compiler | an unused class is legal, nothing requires a type to be instantiated |
| the 137-test suite | eveyr test asserts a class's beavior, no asks ' is every listener wired?' |
| code review | the diff that added DamageMeter was correct - the omission was in a different file |
| the acceptance-criteria harness | checks source content & there was no criterion about the listener list |

**Step 3 — the experiment.** In your finished build, comment out `bus.subscribe(meter)` in
`GameSession.start()` and run `mvn test`.

**How many tests fail?** ___2___ **Name them.**
1. theDamageMeterActuallyFiresNow
2. theSessionSubscribesTheListenerThatNobodyEverWiredUp

**Put the line back.**

**Step 4.** You just demonstrated the difference between the old wiring and the new wiring, and
it is **not** that the new one cannot be wrong. State what it actually is, in one sentence.

> the new wiring can still be wrong, but at least it is wrong in one known place,
> witha test that was able to point it out. locatable, not infallible 

**Step 5.** A count of `bus.subscribe` calls is not evidence that a listener works. Which of your
acceptance criteria *is* that evidence, and what makes it different in kind?
>AC3, publishes a DAMAGE_TAKEN event & looks for warning in the transcript. different in kind because it 
> checks a behavior (listener reacted), & not a count (something was in a list)

---

## D4 — What each pattern cost · 3 pts

**Step 1.** Paste `git diff --stat` for your US-9.1 commit and your US-9.2 commit.

```
1. 9.1 commit
$ git diff --stat HEAD~1 
 docs/evidence.week12.md                            |  38 ++++-
 src/main/java/dungeonforge/Main.java               |  10 ++
 .../java/dungeonforge/factory/MonsterSource.java   |   9 +
 .../dungeonforge/legacy/LegacyBestiaryAdapter.java | 111 +++++++++++++
 src/test/java/dungeonforge/AdapterFacadeTest.java  | 185 ++++++++++++++++++++-
 5 files changed, 343 insertions(+), 10 deletions(-)

2. 9.2 commit
$ git diff --stat HEAD~1   
 docs/evidence.week12.md                            |  21 ++-
 src/main/java/dungeonforge/Main.java               | 136 ++++---------------
 src/main/java/dungeonforge/core/GameWorld.java     |   8 +-
 .../java/dungeonforge/session/GameSession.java     | 148 +++++++++++++++++++++
 src/test/java/dungeonforge/AdapterFacadeTest.java  | 100 ++++++++++++++
 src/test/java/dungeonforge/CommandTest.java        |   8 +-
 src/test/java/dungeonforge/DecoratorTest.java      |   3 +-
 src/test/java/dungeonforge/FactoryTest.java        |   8 +-
 src/test/java/dungeonforge/SingletonTest.java      |   6 +-
 src/test/java/dungeonforge/StateTest.java          |   9 +-
 src/test/java/dungeonforge/TemplateMethodTest.java |  10 +-
 11 files changed, 312 insertions(+), 145 deletions(-)
 
> Adapter adds a bunch while Facade deletes a lot.
```

**Step 2.** `GameSession.start()` builds eight objects and a dungeon, every time it is called.
**Write a test that needs only a `Player` and an `EventBus`.** Can you still do it?

**What does that tell you about the cost of a front door?**
> Yes, newPlayer(...) and new EventBus(...) still work, the facade didn't hide anything. 
> You can't get a half-built session. The cost of a front door is it only opens all the way!

**Step 3.** The Principle of Least Knowledge says `Main` should not have to know eight classes.
It now knows one. **Name one diagnostic or debugging task that got harder**, specifically.
> Debugging the assembly itself. A failure now surfaces inside start(), after all 8 objects exist,
> so you can't bisect the wiring by commenting out lines in Main().

**Step 4 — the sentence to take into the final.** Adapter and Facade are both "a class standing
in front of other classes." In one sentence each, from having written both:

| Pattern | What it is actually for |
|---|---|
| Adapter | changing the shape of one conversation with something you don't own |
| Facade | changing how many conversations a caller has to have |

**And: which of the two did your codebase genuinely need, and which was mostly tidiness?**
Defend the answer with a number from Part 3.
>The Facade because it fixed two real problems: a listener that never fired & a turn sequence that existed in 2 copies.
> A script allowed players to walk past a live monster. The Adapter added content but didn't fix anything. 
> Part 3 = DamageMeter mentioned outside its own file, 0 -> 1

---

## Turning it in

Commit `docs/adapter-facade-clinic.md` with D1–D4 complete, the compiler error in D1 Step 4
pasted verbatim, the exception in D2 Step 4 pasted verbatim, and `bus.subscribe(meter)` **put
back** in `GameSession`.
