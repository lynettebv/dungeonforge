package dungeonforge.commands;

import dungeonforge.config.GameConfig;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CommandHistory {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final List<String> replaying = new ArrayList<>();
    private final int maxDepth;

    public CommandHistory() {
        maxDepth = Math.max(1, GameConfig.getInstance().getInt("undoDepth")); }

    public void push(Command command) {
        replaying.add(command.getDescription());
        if (!command.isUndoable()) return;
        undoStack.push(command);
        while (undoStack.size() > maxDepth) undoStack.removeLast();
    }

    // public boolean undoLast() {
       // if (undoStack.isEmpty()) return false;
        // undoStack.pop().undo();
        // return true;

    public boolean undoLast() {
        if (undoStack.isEmpty()) return false;

        Command command = undoStack.pop();
        command.undo();
        return true;
    }

    public int depth() { return undoStack.size(); }
    public List<String> replayLog() { return new ArrayList<>(this.replaying); }
    public void clear() {undoStack.clear(); replaying.clear(); }


}
