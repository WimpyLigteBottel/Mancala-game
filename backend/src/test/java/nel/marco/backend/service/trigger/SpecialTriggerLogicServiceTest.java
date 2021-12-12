package nel.marco.backend.service.trigger;

import nel.marco.backend.controller.v1.model.Match;
import nel.marco.backend.controller.v1.model.PIT;
import nel.marco.backend.controller.v1.model.Player;
import nel.marco.backend.controller.v1.model.PlayerModel;
import nel.marco.backend.service.MancalaService;
import nel.marco.backend.service.stones.MoveLogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SpecialTriggerLogicServiceTest {

    MancalaService mancalaService;
    SpecialTriggerLogicService specialTriggerLogicService;

    @BeforeEach
    void setUp() {
        specialTriggerLogicService = new SpecialTriggerLogicService();
        mancalaService = new MancalaService(Mockito.mock(MoveLogicService.class), specialTriggerLogicService);
    }

    @Test
    @DisplayName("Player A gets extra turn")
    void hasSpecialLogicTriggered_playerA_extraTurn() {

        Match defaultMatch = createDefaultMatch(false);

        defaultMatch.setLastStoneLocation(PIT.PLAYER_1_BOARD);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER1);

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);

        assertTrue(actual.isPlayerATurn());
    }

    @Test
    @DisplayName("Player B gets extra turn")
    void hasSpecialLogicTriggered_playerB_extraTurn() {

        Match defaultMatch = createDefaultMatch(true);

        //Cheating my indicating last location was on player1 board
        defaultMatch.setLastStoneLocation(PIT.PLAYER_2_BOARD);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER2);

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);

        assertFalse(actual.isPlayerATurn());
    }

    @Test
    @DisplayName("Player A captures Player B stones")
    void hasSpecialLogicTriggered_playerA_captureStones_expectStonesToBeCaptured() {

        Match defaultMatch = createDefaultMatch(true);

        //Cheating my indicating last location was on player1 board and one stone
        defaultMatch.getPlayerModelA().getPits().put(PIT.SIX, 1);
        defaultMatch.setLastStoneLocation(PIT.SIX);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER1);
        defaultMatch.setStealable(true);

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);


        assertEquals(7, actual.getPlayerModelA().getTotalScore());
    }

    @Test
    @DisplayName("Player B captures Player A stones")
    void hasSpecialLogicTriggered_playerB_captureStones_expectStonesToBeCaptured() {

        Match defaultMatch = createDefaultMatch(false);

        //Cheating my indicating last location was on player1 board and one stone
        defaultMatch.getPlayerModelB().getPits().put(PIT.SIX, 1);
        defaultMatch.setLastStoneLocation(PIT.SIX);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER2);
        defaultMatch.setStealable(true);

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);


        assertEquals(7, actual.getPlayerModelB().getTotalScore());
    }


    @Test
    @DisplayName("Game is over == Player B field is clear")
    void hasSpecialLogicTriggered_playerBFieldIsClear_expectGameOver() {
        Match defaultMatch = createDefaultMatch(true);


        assertEquals(0, defaultMatch.getPlayerModelA().getTotalScore(), "Game started and expect score to be 0");

        HashMap<PIT, Integer> copy = new HashMap<>(defaultMatch.getPlayerModelB().getPits());

        //Clearing Player B board
        copy.forEach((pit, integer) -> {
            defaultMatch.getPlayerModelB().getPits().put(pit, 0);
        });

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);


        assertEquals(0, actual.getPlayerModelB().getTotalScore());
        assertEquals(36, actual.getPlayerModelA().getTotalScore());
    }

    @Test
    @DisplayName("Game is over == Player A field is clear")
    void hasSpecialLogicTriggered_playerAFieldIsClear_expectGameOver() {
        Match defaultMatch = createDefaultMatch(true);

        assertEquals(0, defaultMatch.getPlayerModelB().getTotalScore(), "Game started and expect score to be 0");

        HashMap<PIT, Integer> copy = new HashMap<>(defaultMatch.getPlayerModelA().getPits());

        //Clearing Player A board
        copy.forEach((pit, integer) -> {
            defaultMatch.getPlayerModelA().getPits().put(pit, 0);
        });

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);


        assertEquals(36, actual.getPlayerModelB().getTotalScore());
        assertEquals(0, actual.getPlayerModelA().getTotalScore());
    }

    private Match createDefaultMatch(boolean isPlayerAStarting) {
        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        Match match = mancalaService.createMatch(playerA, playerB);
        match.setPlayerATurn(isPlayerAStarting);
        return match;
    }


}