package dungeonforge.items;

/**
 * WEEK 7 -- the item that only exists because of the COMMAND pattern.
 *
 * The Chronomancer's Hourglass rewinds the last turn. There is no way to write this feature
 * against a game whose actions are if-branches, because an if-branch leaves nothing behind to
 * reverse. It took one small class and three lines in UseItemCommand.
 *
 * When someone asks whether a pattern was worth the extra classes, this is the answer:
 * it is not that the code got tidier, it is that a feature became possible.
 *
 * KNOWN LIMITATION, and a good extension exercise: using the hourglass spends a charge, and
 * that spend is not itself undoable. Rewind twice in a row and you cannot get the charges
 * back. Fixing it properly means the hourglass needs its own memento -- which is the same
 * lesson TurnSnapshot teaches, one level down.
 */
public class Hourglass implements Item {

    private int charges;

    public Hourglass(int charges) { this.charges = charges; }

    public boolean hasCharges() { return charges > 0; }
    public int getCharges()     { return charges; }
    public void spendCharge()   { if (charges > 0) charges--; }

    @Override public String getName()   { return "Chronomancer's Hourglass"; }
    @Override public double getWeight() { return 0.5; }
    @Override public int getValue()     { return 250; }

    @Override
    public String describe() {
        return getName() + " [" + charges + " charges] (rewinds one turn)";
    }
}
