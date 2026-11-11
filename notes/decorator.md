# Decorator — Week 9 Notes

**Attach new behaviour to an object by wrapping it in another object of the same type.**

Both halves matter. *Wrapping* is the mechanism. *Of the same type* is what makes the wrapper
invisible to everyone holding a reference.

## The problem it solves

You have a thing, and a set of optional features that can be combined. Model the combinations
as classes and you need one class per **subset**: 2ᴺ − 1 of them. Each combination has to
re-implement every feature it contains, because Java gives a class one parent and a class that
needs two behaviours can inherit only one of them.

**The duplication is not carelessness. It is the single-inheritance rule showing through.**
That is why the answer is not "be more disciplined" but "stop using inheritance for this."

## The four parts

| Part | In DungeonForge | In Head First |
|---|---|---|
| Component | `Weapon` (interface) | `Beverage` |
| Concrete Component | `BasicWeapon` | `HouseBlend`, `DarkRoast` |
| Decorator | `WeaponDecorator` (abstract, HAS-A `Weapon`) | `CondimentDecorator` |
| Concrete Decorator | `FlamingWeapon`, `FrostWeapon`, `VampiricWeapon`, `KeenWeapon` | `Mocha`, `Whip`, `Soy` |

## The question everyone asks

> "`WeaponDecorator` implements `Weapon` **and** holds a `Weapon`. Isn't that just inheritance
> again?"

No, and the answer is a number.

In the old design, five files knew the fire bonus was 4, because behaviour was **inherited**
and therefore had to be copied into every class that needed it. In the new design, one file
knows, because behaviour is **delegated** at runtime.

> **The inheritance buys the TYPE. The composition does the WORK.**

The type is what lets a decorator be passed to `Combat`, stored in an inventory, handed to
`equip()`. Nobody outside the `items` package can tell a decorated weapon from a plain one,
and that is exactly the point — Head First calls it *transparency*.

## Interface or abstract class?

Head First uses an abstract class for `Beverage`; we use an interface for `Weapon`. Both are
correct. The pattern requires a **shared supertype**, not specifically a class.

Use an abstract class when the component has shared state or shared implementation worth
inheriting. Use an interface when it doesn't. Ours doesn't, and we have been programming to
interfaces since Week 1.

Notice that the refactor *from* a concrete class *to* an interface is part of the story: the
class was concrete so people could extend it, and extending it was the problem.

## Delegate by default, override by exception

This is the practical rule, and it is what keeps concrete decorators to five lines.

`WeaponDecorator` implements **every** method by forwarding to `wrapped`. A concrete decorator
overrides only what it actually changes:

- `FlamingWeapon` → `getDamage()` only
- `FrostWeapon` → `getDamage()` only
- `VampiricWeapon` → `lifesteal()` only
- `KeenWeapon` → `getDamage()` only

If you find yourself writing `return wrapped.getDamage();` in a concrete decorator, delete the
method. The parent already said that.

## Order is semantics

A decorator can see the thing underneath it. It cannot see anything above it. Everything about
ordering follows from that one sentence.

| Stack | Damage | Why |
|---|---|---|
| `Flaming(Keen(sword))` | 8 → 12 → **16** | Keen multiplies the base, then fire adds |
| `Keen(Flaming(sword))` | 8 → 12 → **18** | fire adds first, then Keen multiplies the total |

| Stack | Lifesteal | Why |
|---|---|---|
| `Vampiric(Flaming(sword))` | **3** | it drinks from 8 + 4 |
| `Flaming(Vampiric(sword))` | **2** | it drinks from 8; the fire is added above it |

This is not a defect to be engineered away. It is a real modelling decision — *does the
enchantment drink from the whole blow, or from the steel?* — and the nesting order is how you
state your answer.

## What the pattern costs

Head First is honest about this on pp. 110–111, and so should you be.

1. **Lots of small classes.** Four files instead of seven is a win here; in `java.io` it is why
   nobody can remember which stream wraps which.
2. **Code that depends on concrete types breaks.** Anything doing `if (w instanceof
   FlamingWeapon)` will not see a `FlamingWeapon` wrapped inside a `KeenWeapon`. If you need
   to ask "is this flaming?", the pattern is fighting you — ask the object a question instead
   of asking about its class.
3. **Construction gets noisy.** `new VampiricWeapon(new FrostWeapon(new BasicWeapon(...)))` is
   a mouthful. That is what `EnchantmentRegistry` is for, and it is why Builder and Factory
   turn up so often alongside Decorator.

## What the pattern does not do

Enchanting produces a new object, so the equipped slot can go stale. **Decorator did not fix
that.** Two lines in `EnchantCommand` fixed it, and those same two lines would have worked
before the refactor.

Patterns reorganise structure. They do not find your bugs. Reach for a pattern when the shape
of the code is wrong, not when the behaviour is.

## Decorator versus the ones it looks like

| | Changes | Keeps the same interface? | We built it |
|---|---|---|---|
| **Decorator** | what an object *does* | **yes** — that's the point | Week 9 |
| **Strategy** | *how* one operation is done | n/a, it's a field | Week 5 |
| **State** | *what is allowed* right now | n/a, it's a field | Week 8 |
| **Adapter** | the *interface*, not the behaviour | **no** — that's the point | Week 12 |
| **Proxy** | *access* to the object | yes | Week 14 |

The pair people confuse is **Decorator and Proxy** — both wrap, both keep the interface. The
difference is intent: a decorator **adds behaviour**; a proxy **controls access** to behaviour
that is already there. Week 14 puts them side by side.

**Decorator and Adapter** are easy once you ask: did the interface change? Adapter exists
because it did. Decorator exists because it didn't.

## Python note

Python has a language feature called decorators, spelled `@something`. It was **named after
this pattern** and it is not the same thing.

```python
@lru_cache(maxsize=None)
def expensive(n): ...
```

That replaces the function object with a wrapper that calls the original — which is genuinely
the Decorator idea applied to callables. But:

- Python's decorators wrap **functions and classes**, at definition time. The GoF pattern
  wraps **object instances**, at runtime, as many times as you like.
- Python doesn't need the pattern for our problem at all, because it has multiple inheritance:
  `class FlamingFrostWeapon(FlamingWeapon, FrostWeapon)` compiles. That works — *and it is
  still usually worse*, because you get MRO ambiguity, you still need a class per combination,
  and you cannot enchant an object that already exists.
- The closest Python equivalent of what we built is plain composition with `__getattr__`
  forwarding, or `functools.wraps` on callables.

**Good interview answer:** "Python's `@decorator` syntax is function-level and applied at
definition; the GoF Decorator is object-level and applied at runtime. Same idea, different
granularity."

## Vocabulary to own by Friday

component · concrete component · decorator · concrete decorator · wrapping · delegation ·
transparency · Open/Closed Principle · composition over inheritance · single inheritance ·
combinatorial explosion · 2ᴺ − 1

---
