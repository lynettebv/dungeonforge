package dungeonforge.session;

import dungeonforge.Main;
import dungeonforge.commands.Command;
import dungeonforge.commands.CommandHistory;
import dungeonforge.commands.CommandParser;
import dungeonforge.commands.GameContext;
import dungeonforge.core.Combat;
import dungeonforge.core.GameWorld;
import dungeonforge.core.Player;
import dungeonforge.events.*;
import dungeonforge.factory.MonsterDef;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.MonsterSource;
import dungeonforge.items.Hourglass;
import dungeonforge.items.Potion;
import dungeonforge.legacy.LegacyBestiary;
import dungeonforge.legacy.LegacyBestiaryAdapter;

import java.util.ArrayList;
import java.util.List;

public final class GameSession {

    private final Player player;
    private final GameWorld world;
    private final EventBus bus;
    private final CommandParser parser;
    private final CommandHistory history;
    private final GameContext ctx;
    List<String> transcript = new ArrayList<>();
    List<String> sourceReport = new ArrayList<>();

    private GameSession(Player player, GameWorld world, EventBus bus, CommandParser parser,
                        CommandHistory history, GameContext ctx) {
        this.player = player;
        this.world = world;
        this.bus = bus;
        this.parser = parser;
        this.history = history;
        this.ctx = ctx;
    }

    public static GameSession start(String playerName, GameEventListener... views) {
        Player player = new Player(playerName);
        player.addItem(new Potion("Small Healing Draught", 0.3, 20, 22));
        player.addItem(new Hourglass(2));                 // US-5.4
        player.addItem(new dungeonforge.items.BasicWeapon("Notched Shortsword", 2.5, 25, 5));

        // ORDER MATTERS, this is the only place that knows it. Every monster source has to be in the factory
        // BEFORE the world is generated, bc generation is when monsters get placed.
        MonsterFactory factory = new MonsterFactory();
        List<String> sourceReport = new ArrayList<>();
        sourceReport.add(registerInto(factory,
                new LegacyBestiaryAdapter(
                        new LegacyBestiary("bestiary.csv"),"bestiary.csv (0.4 export)")
                ));



        GameWorld world = new GameWorld(player, factory);

        EventBus bus = new EventBus();
        QuestTracker quests = new QuestTracker(bus);
        AchievementSystem achievements = new AchievementSystem(bus);
        CombatLog log = new CombatLog(200);
        DamageMeter meter = new DamageMeter(bus, player, 0.25);

        CommandHistory history = new CommandHistory();
        GameContext ctx = new GameContext(world, player, bus, new Combat(bus), history);
        CommandParser parser = new CommandParser(quests, achievements);

        GameSession session = new GameSession(player, world, bus, parser, history, ctx);
        session.sourceReport.addAll(sourceReport);

        bus.subscribe(quests);
        bus.subscribe(achievements);
        bus.subscribe(log);
        bus.subscribe(meter);
        bus.subscribe(session.new TranscriptRecorder());

        // These are game views like Main.ConsolePrinter
        for (GameEventListener view : views) bus.subscribe(view);

        bus.message("Type 'help' for what you can do. You are carrying an hourglass -- 'use hourglass' rewinds a turn.");
        session.runLine("look");
        return session;

    }

    public void runLine(String line) {
        Command command = parser.parse(line, ctx);   // text  -> object
        command.execute();                            // object -> effect
        history.push(command);                        // object -> undo stack + replay log
        ctx.syncState();
    }

    public static GameWorld buildWorldOnly(String playerName) {
        MonsterFactory factory = new MonsterFactory();
        List<String> sourceReport = new ArrayList<>();
        registerInto(factory, new LegacyBestiaryAdapter(
                        new LegacyBestiary("bestiary.csv"),"bestiary.csv (0.4 export)")
        );
        return new GameWorld(new Player(playerName), factory);
    }

    public boolean isRunning() { return ctx.isRunning(); }

    public String prompt() {
       return "\n["+ctx.getState().name()+" | HP " + ctx.getPlayer().getHp() + "/"
                + ctx.getPlayer().getMaxHp() + "] > ";
    }

    public String epilogue() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(player.describe()).append("\n");
        if (!player.isAlive()) sb.append(" You die in the dark.\n");
        sb.append(" Actions this session: ").append(history.replayLog().size())
                .append("  |  undo stack depth: ").append(history.depth());
        return sb.toString();
    }

    private static String registerInto(MonsterFactory factory, MonsterSource source) {
        int before = factory.blueprintCount();
        for (MonsterDef def : source.definitions()) factory.register(def);
        return source.sourceName() + ": " + (factory.blueprintCount() - before) + " blueprints";
    }

    public List<String> transcript() {return List.copyOf(transcript);}

    public List<String> sourceReport() { return List.copyOf(this.sourceReport); }
    public Player getPlayer() { return this.player; }
    public GameWorld getWorld() { return this.world; }
    public GameContext getContext() { return this.ctx; }
    public CommandHistory getHistory() { return this.history; }
    public EventBus getBus() { return this.bus; }
    public int listenerCount() { return bus.listenerCount(); }

    private final class TranscriptRecorder implements GameEventListener {
        private final CombatLog formatter = new CombatLog(1);

        @Override
        public void onEvent(GameEvent event) {
            formatter.onEvent(event);
            transcript.addAll(formatter.getLines());
        }
    }
}
