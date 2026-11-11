package dungeonforge.commands;

import dungeonforge.behavior.CombatStrategy;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.items.Item;
import dungeonforge.items.Weapon;
import dungeonforge.states.MonsterState;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * WEEK 7 -- the BEFORE-state a turn-ending Command captures so undo() can work.
 *
 * ==================== READ THIS, IT IS THE POINT OF PART D2 ====================
 * The textbook version of undo is "remember the one field I changed." For
 * AttackCommand that would be "remember the monster's hit points."
 *
 * Try it and it does not work. Attacking ends your turn, so the monsters retaliate. Undoing
 * the attack must also undo the damage they did to you, the XP you gained, the gold, and any
 * monster that changed tactics or fled. One remembered field cannot do that.
 *
 * The honest fix is to snapshot everything a turn can touch. That is a small MEMENTO --
 * Head First Ch. 14's appendix -- and NOTICING WHY IT WAS NECESSARY is worth more than the
 * class itself. Command gives you the hook for undo; it does not tell you what to remember.
 * ==============================================================================
 */
public final class TurnSnapshot {

    private final Player player;
    private final Room room;

    private final int hp, gold, xp;
    private final List<Item> inventory;
    private final List<Monster> monsters;
    private final List<Item> floorItems;
    private final List<Item> chestContents;
    private final Weapon equipped;

    private final Map<Monster, Integer> monsterHp = new IdentityHashMap<>();
    private final Map<Monster, CombatStrategy> monsterStrategies = new IdentityHashMap<>();
    // ADDED
    private final Map<Monster, MonsterState> monsterStates = new IdentityHashMap<>();

    private TurnSnapshot(Player player, Room room) {
        this.player = player;
        this.room = room;

        this.hp = player.getHp();
        this.gold = player.getGold();
        this.xp = player.getXp();
        this.inventory = new ArrayList<>(player.getInventory());
        this.equipped = player.getEquippedWeapon();

        this.monsters = new ArrayList<>(room.getMonsters());
        this.floorItems = new ArrayList<>(room.getFloorItems());
        this.chestContents = room.getChest() == null
                ? null : new ArrayList<>(room.getChest().getContents());

        for (Monster m : room.getMonsters()) {
            monsterHp.put(m, m.getHp());
            monsterStrategies.put(m, m.getStrategy());
            monsterStates.put(m, m.getState());
        }
    }

    public static TurnSnapshot capture(Player player, Room room) {
        return new TurnSnapshot(player, room);
    }

    public void restore() {
        player.setHp(hp);
        player.setGold(gold);
        player.setXp(xp);
        player.getInventory().clear();
        player.getInventory().addAll(inventory);
        player.equip(equipped);

        room.getMonsters().clear();
        room.getMonsters().addAll(monsters);          // brings back anything that fled
        room.getFloorItems().clear();
        room.getFloorItems().addAll(floorItems);
        if (chestContents != null && room.getChest() != null) {
            room.getChest().getContents().clear();
            room.getChest().getContents().addAll(chestContents);
        }

        for (Map.Entry<Monster, Integer> e : monsterHp.entrySet()) {
            e.getKey().setHp(e.getValue());
        }
        for (Map.Entry<Monster, CombatStrategy> e : monsterStrategies.entrySet()) {
            e.getKey().setStrategy(e.getValue());     // un-panics anything that turned skittish
        }
        for (Map.Entry<Monster, MonsterState> e : monsterStates.entrySet()) {
            e.getKey().restoreState(e.getValue());    // un-notices, un-enrages, un-kills
        }
    }

    public Room getRoom() { return room; }
}