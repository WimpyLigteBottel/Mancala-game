package nel.marco.mancala.service;

import nel.marco.mancala.controller.model.PlayerModel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MancalaServiceTest {

    MancalaService mancalaService;

    public void setup() {
        mancalaService = new MancalaService();
    }

    @Test
    void createBoard_expectBoardLengthToBeCorrect() {

        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        mancalaService.createBoard(playerA, playerB);

        assertEquals(6, playerA.getPits().length);
        assertEquals(6, playerB.getPits().length);
    }

    @Test
    void createBoard_expectStartingPitsToBeEqual() {

        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        mancalaService.createBoard(playerA, playerB);

        Arrays.stream(playerA.getPits()).forEach(marbles -> {
            assertEquals(6, marbles, "The amount of marbles in pit should be 6");
        });


        Arrays.stream(playerB.getPits()).forEach(marbles -> {
            assertEquals(6, marbles, "The amount of marbles in pit should be 6");
        });

    }

    @Test
    void executeCommand() {
    }

    @Test
    void movingStones() {
    }
}