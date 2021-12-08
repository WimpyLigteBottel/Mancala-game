package nel.marco.mancala.controller.model;

import java.util.Arrays;

public enum PIT {

    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5),
    SIX(6),
    PLAYER_1_BOARD(100),
    PLAYER_2_BOARD(200);

    private int pitIndex;

    PIT(int i) {
        this.pitIndex = i;
    }

    public int getPitIndex() {
        return pitIndex;
    }


    public static PIT valueOf(int i) {
        return Arrays.stream(PIT.values())
                .filter(pit -> pit.pitIndex == i)
                .findAny()
                .orElseThrow(() -> new RuntimeException("invalid pit selection [i=" + i + "]"));

    }


}
