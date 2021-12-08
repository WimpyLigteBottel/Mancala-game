package nel.marco.mancala.service.trigger;

import nel.marco.mancala.controller.model.PIT;
import nel.marco.mancala.controller.model.Player;
import nel.marco.mancala.controller.model.PlayerModel;
import nel.marco.mancala.service.MancalaService;
import nel.marco.mancala.service.Match;
import nel.marco.mancala.service.stones.MoveLogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void hasSpecialLogicTriggered_playerA_extraTurn() {

        Match defaultMatch = createDefaultMatch(false);

        //Cheating my indicating last location was on player1 board
        defaultMatch.setLastStoneLocation(PIT.PLAYER_1_BOARD);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER1);

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);

        assertEquals(true, actual.isPlayerATurn());
    }

    @Test
    void hasSpecialLogicTriggered_playerB_extraTurn() {

        Match defaultMatch = createDefaultMatch(true);

        //Cheating my indicating last location was on player1 board
        defaultMatch.setLastStoneLocation(PIT.PLAYER_2_BOARD);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER2);

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);

        assertEquals(false, actual.isPlayerATurn());
    }

    @Test
    void hasSpecialLogicTriggered_playerA_captureStones_expectStonesToBeCaptured() {

        Match defaultMatch = createDefaultMatch(true);

        //Cheating my indicating last location was on player1 board and one stone
        defaultMatch.getPlayerModelA().getPits().put(PIT.SIX, 1);
        defaultMatch.setLastStoneLocation(PIT.SIX);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER1);

        Match actual = specialTriggerLogicService.hasSpecialLogicTriggered(defaultMatch);


        assertEquals(7, actual.getPlayerModelA().getTotalScore());
    }

    @Test
    void hasSpecialLogicTriggered_playerB_captureStones_expectStonesToBeCaptured() {

        Match defaultMatch = createDefaultMatch(true);

        //Cheating my indicating last location was on player1 board and one stone
        defaultMatch.getPlayerModelB().getPits().put(PIT.SIX, 1);
        defaultMatch.setLastStoneLocation(PIT.SIX);
        defaultMatch.setLastStonePlayerBoard(Player.PLAYER2);

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