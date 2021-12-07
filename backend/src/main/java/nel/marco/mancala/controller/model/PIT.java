package nel.marco.mancala.controller.model;

public enum PIT {

    FIRST(0),
    SECOND(1),
    THIRD(2),
    FOURTH(3),
    FIFTH(4),
    SIX(5);

    private int pitIndex;

    PIT(int i) {
        this.pitIndex = i;
    }

    public int getPitIndex() {
        return pitIndex;
    }


}
