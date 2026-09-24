package dungeonforge.factory;

import dungeonforge.core.Monster;
import dungeonforge.items.Item;

public interface ThemeKit {
    String themeName();
    Monster createMonster(int depth);
    Monster createBoss(int depth);
    Item createLoot(int depth);
    String createRoomFlavor();
}
