package dungeonforge.items;

public class Hourglass implements Item {
    private int charges;

    public Hourglass(int charges) {
        this.charges = charges;
    }

    public boolean hasCharges() { return charges > 0; }
    public int getCharges() { return charges; }
    public void spendCharge() {if (charges > 0) charges--;}

    @Override
    public String getName() { return "Chronomaster's Hourglass"; }

    @Override
    public double getWeight() { return 0.5; }

    @Override
    public int getValue() { return 250; }

    @Override
    public String describe() {return getName() + " [" + charges + " charges] (rewinds one turn)";}
}
