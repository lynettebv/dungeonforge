package dungeonforge.factory;

import java.util.List;

public interface MonsterSource {
    List<MonsterDef> definitions();

    String sourceName();
}
