package nel.marco.mancala.service.trigger;

import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.Player;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.service.MancalaService;
import nel.marco.mancala.service.stones.MoveLogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpecialTriggerLogicServiceTest {
    SpecialTriggerLogicService specialTriggerLogicService;

    MancalaService mancalaService;

    MoveLogicService moveLogicService;

    @BeforeEach
    void setUp() {
        specialTriggerLogicService = new SpecialTriggerLogicService();

        mancalaService = new MancalaService(moveLogicService, specialTriggerLogicService);
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

    private Match createDefaultMatch(boolean isPlayerAStarting) {
        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        Match match = mancalaService.createMatch(playerA, playerB);
        match.setPlayerATurn(isPlayerAStarting);
        return match;
    }


}