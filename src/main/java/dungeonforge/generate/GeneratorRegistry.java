package dungeonforge.generate;

import java.util.ArrayList;
import java.util.List;

public class GeneratorRegistry {
    private final List<LevelGenerator> shapes = new ArrayList<>();

    public GeneratorRegistry() {
        shapes.add(new CorridorGenerator());
        shapes.add(new CaveGenerator());
        shapes.add(new RingGenerator());
    }

    public LevelGenerator forDepth(int depth) { return shapes.get((depth - 1) % shapes.size()); }

    public int size() { return shapes.size(); }

    public List<String> shapeNames() {
        List<String> names = new ArrayList<>();
        for (LevelGenerator g : shapes) {
            names.add(g.shapeName());
        }
        return names;
    }
}
