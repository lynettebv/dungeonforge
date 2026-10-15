package dungeonforge.commands;

import dungeonforge.behavior.CombatStrategy;
import dungeonforge.core.Monster;
import dungeonforge.core.Player;
import dungeonforge.core.Room;
import dungeonforge.items.Item;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class TurnSnapshot {
    private final Player player;

    private final Room room;

    private int hp, xp, gold;
    private final List<Item> inventory;
    private final List<Monster> monsters;
    private final List<Item> floorItems;
    private final List<Item> chestContents;

    private final Map<Monster, Integer> monsterHp = new IdentityHashMap<>();
    private final Map<Monster, CombatStrategy> monsterStrategies = new IdentityHashMap<>();

    private TurnSnapshot(Player player, Room room) {
        this.player = player;
        this.room = room;

        this.hp = player.getHp();
        this.xp = player.getXp();
        this.gold = player.getGold();
        this.inventory = new ArrayList<>(player.getInventory());

        this.monsters = new ArrayList<>(room.getMonsters());
        this.floorItems = new ArrayList<>(room.getFloorItems());
        this.chestContents = room.getChest() == null ? null : new ArrayList<>(room.getChest().getContents());

        for (Monster m : this.monsters) {
            monsterHp.put(m, m.getHp());
            monsterStrategies.put(m, m.getStrategy());
        }
    }

    public static TurnSnapshot capture(Player player, Room room) {
        return new TurnSnapshot(player, room);
    }

    public void restore() {
        player.setHp(this.hp);
        player.setXp(this.xp);
        player.setGold(this.gold);
        player.getInventory().clear();
        player.getInventory().addAll(this.inventory);

        room.getMonsters().clear();
        room.getMonsters().addAll(this.monsters);
        room.getFloorItems().clear();
        room.getFloorItems().addAll(this.floorItems);
        if (chestContents != null && room.getChest() != null) {
            room.getChest().getContents().clear();
            room.getChest().getContents().addAll(this.chestContents);
        }

        for(Map.Entry<Monster, Integer> e : this.monsterHp.entrySet()) {
            e.getKey().setHp(e.getValue());
        }

        for(Map.Entry<Monster, CombatStrategy> e : this.monsterStrategies.entrySet()) {
            e.getKey().setStrategy(e.getValue());
        }
    }

    public Player getPlayer() { return player; }
    public Room getRoom() { return room; }
}
