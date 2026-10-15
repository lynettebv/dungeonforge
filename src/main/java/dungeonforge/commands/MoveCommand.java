package dungeonforge.commands;

import dungeonforge.core.Combat;
import dungeonforge.core.Room;
import dungeonforge.events.GameEvent;

public class MoveCommand extends AbstractCommand {

    private final String direction;

    public MoveCommand(GameContext ctx, String direction) {
        super(ctx, direction);
        this.direction = direction;
    }

    @Override
    protected boolean doExecute() {
        if (room().hasLivingMonsters()) {
            bus().message("You cannot walk away with enemies still standing.");
            return false;
        }
        Room next = room().getExit(direction);
        if (next == null) {
            bus().message("There is no way " + direction + ".");
            return false;
        }
        ctx.setCurrentRoom(next);
        Combat.restAfterRoom(player());
        bus().publish(GameEvent.message("You go " + direction + "."));
        return true;
    }

    @Override
    public String getDescription() { return direction; }
}
