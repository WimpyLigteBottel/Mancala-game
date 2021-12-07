package nel.marco.mancala.controller.model;

public enum PIT {

    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5),
    SIX(6);

    private int pitIndex;

    PIT(int i) {
        this.pitIndex = i;
    }

    public int getPitIndex() {
        return pitIndex;
    }


}
