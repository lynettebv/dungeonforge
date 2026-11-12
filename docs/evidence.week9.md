# Week 9 Evidence

Fill this in as you go. Parts 0–2 and Part 5 are graded on the *measurement*, not on the code.

---

## 0. The Sprint 6 carry-over (US-6.7)

Before any Decorator work. `docs/sprint-07-plan.md` has the story.

```bash
grep -n 'isRunning\|!running' src/main/java/dungeonforge/Main.java \
                              src/main/java/dungeonforge/commands/GameContext.java
```

**Paste the output. How many places spell out the game-over rule?** ___4___

```
lynet@LynetteV MINGW64 ~/IdeaProjects/dungeonforge (week-9-decorator)
$ grep -n 'isRunning\|!running' src/main/java/dungeonforge/Main.java \
                              src/main/java/dungeonforge/commands/GameContext.java
src/main/java/dungeonforge/Main.java:111:        while (ctx.isRunning() && ctx.getPlayer().isAlive()) {
src/main/java/dungeonforge/Main.java:139:            if (!ctx.isRunning() || !ctx.getPlayer().isAlive()) break;
src/main/java/dungeonforge/commands/GameContext.java:52:        if (!player.isAlive() || !running) {
src/main/java/dungeonforge/commands/GameContext.java:71:    public boolean isRunning()           { return running; }
```

**Now open `GameOverState.java`. It lists four verbs. Try to reach one in play. What happens,
and why?**

When the player dies, the game stops accepting commands even though GameOverState has commands available. 
This happens because Main.java checks if the player is alive before continuing the game loop, 
so the player never gets a chance to use those commands.

**One sentence: `running` and `GameOverState` both mean "the game is over". Do they? Say what
each one actually means.**

`running` determines whether the game session is still active, while `GameOverState` represents 
the end of the player's adventure. The player can be in GameOverState without the entire session
needing to end.

---

## 1. BEFORE — count the problem

```bash
ls src/main/java/dungeonforge/items/ | grep -E 'Weapon|Enchant'
grep -rln "damage + 4" src/main/java/dungeonforge/items/
grep -rho "instanceof" src/main/java/dungeonforge/items/*.java | wc -l
grep -c "return null" src/main/java/dungeonforge/items/EnchantmentForge.java
wc -l src/main/java/dungeonforge/items/EnchantmentForge.java
```

| | Count |
|---|---|
| Weapon/enchantment classes | 8 |
| Files containing the fire damage bonus | 4 |
| `instanceof` tests in `items/` | 34 |
| `return null` paths in `EnchantmentForge` | 13 |
| Lines in `EnchantmentForge` | 108 |

**Open `FlamingFrostVampiricWeapon.java`. Which of its methods contain arithmetic that also
appears, word for word, in another file?**

getDamage() is the method and getValue()

**You want a fourth enchantment, `keen`. Before you look anything up: how many new classes
would this package need?** ___15___

*(Write it down before reading on.)* For N enchantments the package needs 2^N − 1 classes.
N = 3 is 7. N = 4 is 15. N = 5 is 31.

---

## 2. BEFORE — reproduce all four defects

You start carrying a **Notched Shortsword**, so this works on turn one. Run
`mvn -q exec:java` and type exactly this:

```
equip shortsword
status
enchant shortsword with flaming
status
inventory
equip shortsword
status
enchant shortsword with flaming
enchant shortsword with keen
quit
```

**(a) Paste the two `status` lines from before and after the first `enchant`:**

```
[Exploring | HP 80/80] > status

  Delver  HP 80/80  ATK 15  DEF 3  Gold 0  XP 0  Carry 60.0kg  wielding Notched Shortsword [dmg 5] (2.5kg, 25g)

[Exploring | HP 80/80] > enchant shortsword with flaming

  The Notched Shortsword is now Flaming Notched Shortsword [dmg 9] (2.5kg, 85g)

[Exploring | HP 80/80] > status

  Delver  HP 80/80  ATK 15  DEF 3  Gold 0 XP 0  Carry 60.0kg  wielding Notched Shortsword [dmg 5] (2.5kg, 25g)
```

**Did your damage change? Explain in one sentence why not.**

No, my equipped weapon's damage stayed at 5 because enchanting replaced the weapon in my inventory
but didn't automatically equip it. I had to equip the Flaming Shortsword manually for the damage to increase to 9.

**(b) Paste the refusal from the second `enchant ... with flaming`:**

```
[Exploring | HP 80/80] > enchant shortsword with flaming

  That weapon cannot take another flaming enchantment.
```

**Is there anything wrong with what you asked for? Yes or no — then say what the refusal was
really reporting.**

No, there's nothing wrong with applying Flaming twice but there must be a limitation in the code
because it doesn't support stacking the same enchantment more than once.

**(c) Paste the response to `enchant ... with keen`:**

```
[Exploring | HP 80/80] > enchant shortsword with keen   

  No such enchantment: keen
```

**(d) Open `EnchantmentForge.baseDamage()`. In one sentence: why does this method exist at all?
What would have to be true for it to be unnecessary?**


The method baseDamage() exists because the current enchantment system needs to figure out
how much damage the original weapon does before adding enchantments.

**(e) Classify all four.** Two of these are the *design*; one is an ordinary *bug* that would
exist in any design. Which is which, and how do you know?

| Defect | Design, or ordinary bug? | How you can tell |
|---|---|---|
| (a) stale hand | ordinary bug | equipped weapon not updated |
| (b) no double enchantment | design | only allows specific combos |
| (c) no fourth enchantment | design | requires maniually adding new combos |
| (d) base-value recovery | design | must recover original weapon damage |

---

## 3. AFTER — US-7.3, the receipt

**Paste `git diff --stat HEAD~1` for the commit that added the fourth enchantment:**

```
lynet@LynetteV MINGW64 ~/IdeaProjects/dungeonforge (feat/US-7.2)
$ git diff --stat HEAD~1
 .../java/dungeonforge/commands/EnchantCommand.java |  27 ++-
 .../java/dungeonforge/commands/TurnSnapshot.java   |   4 +
 .../java/dungeonforge/items/EnchantmentForge.java  | 115 ------------
 .../dungeonforge/items/EnchantmentRegistry.java    |  32 ++++
 src/main/java/dungeonforge/items/KeenWeapon.java   |  20 +++
 src/test/java/dungeonforge/DecoratorTest.java      | 193 +++++++++++++++++++++
 6 files changed, 261 insertions(+), 130 deletions(-)
```

**New files:** __1____  **Modified files:** ___1___  **Lines added to modified files:** ___1___

**Now re-run the Part 1 counts on the finished package:**

| | Before | After |
|---|---|---|
| Weapon/enchantment classes | 8 | 7 |
| Files containing the fire bonus | 4 | 0 |
| `instanceof` in `items/` | 34 | 0 |
| `return null` paths in the selection class | 13 | 0 |
| Lines in the selection class | 108 | 0 |

---

## 4. AFTER — US-7.4, order

**Paste your two order tests, and their four numbers:**

```java
 @Test
    void orderChangesTheResultForMultiplyingDecorators() {
        int keenThenFire = new FlamingWeapon(new KeenWeapon(sword())).getDamage();  // 12 + 4
        int fireThenKeen = new KeenWeapon(new FlamingWeapon(sword())).getDamage();  // round(12*1.5)

        assertEquals(16, keenThenFire);
        assertEquals(18, fireThenKeen);
        assertNotEquals(keenThenFire, fireThenKeen,
                "if these were equal, order would not matter and this test would be pointless");
    }

    @Test
    void orderChangesHowMuchAVampiricWeaponDrinks() {
        assertEquals(3, new VampiricWeapon(new FlamingWeapon(sword())).lifesteal()); // (8+4)/4
        assertEquals(2, new FlamingWeapon(new VampiricWeapon(sword())).lifesteal()); // 8/4
    }
```

| Stack | Result |
|---|---|
| `Flaming(Keen(sword))` | dmg ___16___ |
| `Keen(Flaming(sword))` | dmg ___18___ |
| `Vampiric(Flaming(sword))` | drain ___3___ |
| `Flaming(Vampiric(sword))` | drain ___2___ |

**One paragraph. Why is this correct behaviour rather than a bug?** *(There is one idea to
state, and everything else follows from it.)*

The order changes the math because each decorator applies its effect to the weapon it wraps.
For example, if Flaming is applied after Keen, it adds 4 damage after Keen has already multiplied
the base damage. However, if Keen is applied after Flaming, it multiplies the damage that already
includes the fire bonus. This follows the order of operations because adding before multiplying gives
a different result than multiplying before adding. The same applies to Vampiric because its lifesteal 
depends on the damage of the weapon it wraps. This is correct behavior rather than a bug because
changing the order of the decorators changes which effects are included in the calculations.

---

## 5. The payoff — what did NOT change

```bash
git diff --stat main -- src/main/java/dungeonforge/core/
```

**Which files in `core/` changed during this entire sprint?** ___25___

**`Combat.playerStrikes()` computes the damage the player deals and the hit points they drink
back. You added four enchantments this week, two of which change damage and one of which
changes healing. How many lines of `Combat.java` did you edit?** ___0___

**Explain that number** in two or three sentences, using the phrase *programs to the
interface* and naming which interface.

I didn't need to edit any lines of Combat.java to add the new enchantments 
because it programs to the interface, more specifically the Weapon interface. 
Combat gets the damage and lifesteal values through Player without needing to know
which enchantments are applied. This makes it easier to add new enchantments without
changing the combat logic.

---

## 6. The honest accounting

**Of the four defects in Part 2, which one did Decorator fix, and which one did you fix?**

Decorator fixed the design problems: no double enchantment, no fourth enchantment, and base-value recovery.
I fixed the stale hand bug.

**Quote the lines that fixed the one Decorator did not.**

```
        wasEquipped = (player().getEquippedWeapon() == w);

        if(wasEquipped) { player().equip(after); }
```

**Would those lines have worked before the refactor?** ___Yes___

---

## 7. Retro line

**One sentence on something Decorator gave you, and one on something it did not.**

The Decorator Pattern made it easier to add and stack enchantments
without creating separate classes for every possible combination. 
However, it did not automatically fix the stale hand bug because I still had to
make sure the player's equipped weapon was updated after enchanting it.