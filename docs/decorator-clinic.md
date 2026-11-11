# The Decorator Clinic — Week 9, Part D

Four experiments. Every one produces a number, and the numbers are the grade. Do not write
"Decorator scales better" anywhere in this document without a count next to it.

---

## D1 — Count the duplication, then explain who caused it · 8 pts

The starter has seven enchanted weapon classes for three enchantments. That number is annoying
but it is not the interesting number.

**Step 1.** Run this and record the output.

```bash
grep -rn "damage + 4" src/main/java/dungeonforge/items/
```

**Step 2.** Suppose the designer decides fire should do 5 damage instead of 4. List every file
you must edit, and every *line* within each file.

| File | Line(s) | What the edit is |
|---|---|---|
| | | |

**Step 3.** Now the question that matters. `FlamingFrostWeapon` needs the fire arithmetic because
it is a flaming weapon. It inherits from `Weapon`, which does not have it. It cannot inherit from
`FlamingWeapon` *and* `FrostWeapon`.

**In one sentence: which language rule forced the duplication?**

**Step 4.** Head First, p. 86, says inheritance is "powerful but... not necessarily the best way
to achieve flexibility." Rewrite that sentence as a specific claim about this package, with your
number from Step 1 in it.

> *Answer:* ________________________________________________

---

## D2 — The refusal that is not about the player · 7 pts

**Step 1.** In the running game, enchant a weapon with `flaming` twice. Record the message.

**Step 2.** Open `EnchantmentForge.enchant()` and find the line that produced it. Paste it.

**Step 3.** Answer three questions in one sentence each.

1. Is a twice-flaming weapon unreasonable as a *game* idea?
2. Is it unreasonable as a *code* idea?
3. What, precisely, is the refusal actually reporting?

**Step 4.** Count the branches that exist purely to say no.

```bash
grep -c "return null" src/main/java/dungeonforge/items/EnchantmentForge.java
```

______ branches. Now predict: after US-7.2, how many `return null` branches will
`EnchantmentRegistry` have? ______

**Step 5 — after you finish US-7.2.** Run the count again on the new class. **Strip comments
first** — the registry's own comment says *"apply() cannot return null"*, and a naive grep
counts it. That is the same trap as US-6.7's T8, and it catches people twice in one week.

> **The lesson to write down:** a rule the player experiences should come from a decision
> somebody made, not from a class that nobody wrote. When you catch yourself explaining a game
> rule by describing your class hierarchy, the hierarchy is wrong.

---

## D3 — The fourth enchantment, measured twice · 6 pts

This is the headline experiment. You will measure the *same feature* in both designs.

**Step 1 — the old design, on paper.** Do not write the code. Using 2^N − 1 and the structure of
`EnchantmentForge`, work out what adding `keen` would have cost the starter:

| | Count |
|---|---|
| New classes needed | |
| Existing classes needing edits | |
| New `instanceof` tests in `EnchantmentForge` | |

*(For the third row: every existing enchantment's block gains one branch per new combination,
and both `baseDamage()` and `baseValue()` gain one line per new class.)*

**Step 2 — the new design, for real.** Do US-7.3. Commit only the two files it touches. Then:

```bash
git diff --stat HEAD~1
```

**Step 3.** Put the two side by side.

| | Starter design | Decorator design |
|---|---|---|
| New files | | |
| Modified files | | |
| Lines added to modified files | | |
| `instanceof` count afterwards | | |

**Step 4.** One sentence: what is the *shape* of each design's growth curve? Use the words
"linear" and "exponential" correctly, or do not use them at all.

---

## D4 — What did not change, and what you did yourself · 4 pts

**Step 1.** List the classes that compute combat outcomes:

```bash
grep -rln "getAttackPower\|lifesteal" src/main/java/dungeonforge/core/
```

**Step 2.** For each, run `git diff main -- <file>` and record the number of changed lines.

| File | Lines changed this sprint |
|---|---|
| `core/Combat.java` | |
| `core/Player.java` | |
| `core/Entity.java` | |
| `commands/EquipCommand.java` | |

**Step 3.** Explain the small numbers. Your answer should use the phrase "programs to the
interface" and should name *which* interface.

**Step 4 — the honest half.** One thing in this sprint that Decorator did **not** fix, that you
had to fix yourself. Name it and say why the pattern was never going to help.

> There is a specific answer and it is in `EnchantCommand`. If you cannot find it, reread the
> sub-task list for US-7.2 — one of the tasks has a note under it saying exactly this.

---

## Turning it in

D1–D4 go in `docs/evidence.week9.md` sections 1, 2, 3 and 5. Paste command output, not
paraphrases of command output. **A number without the command that produced it earns half
marks.**
