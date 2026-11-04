package dungeonforge.commands;

import dungeonforge.config.GameConfig;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * WEEK 7 -- the undo stack, and the replay log, from the same list of objects.
 *
 * The replay log is the capability nobody asked for. Because every action is an object with a
 * description, "what did the player actually do?" is answerable -- which is exactly what you
 * want attached to a bug report. Part D3 has you record a session and replay it.
 */
public class CommandHistory {

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final List<String> replayLog = new ArrayList<>();
    private final int maxDepth;

    public CommandHistory() {
        this.maxDepth = Math.max(1, GameConfig.getInstance().getInt("undoDepth"));
    }

    /** Every command that RAN gets logged; only undoable ones join the stack. */
    public void push(Command command) {
        replayLog.add(command.getDescription());
        if (!command.isUndoable()) return;
        undoStack.push(command);
        while (undoStack.size() > maxDepth) undoStack.removeLast();
    }

    public boolean undoLast() {
        if (undoStack.isEmpty()) return false;
        undoStack.pop().undo();
        return true;
    }

    public int depth()              { return undoStack.size(); }
    public List<String> replayLog() { return new ArrayList<>(replayLog); }
    public void clear()             { undoStack.clear(); replayLog.clear(); }
}
