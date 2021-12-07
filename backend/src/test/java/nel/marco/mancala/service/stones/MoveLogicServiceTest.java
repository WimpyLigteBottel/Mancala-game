package nel.marco.mancala.service.stones;

import nel.marco.mancala.controller.model.PIT;
import nel.marco.mancala.controller.model.PlayerModel;
import nel.marco.mancala.service.MancalaService;
import nel.marco.mancala.service.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MoveLogicServiceTest {

    MoveLogicService moveLogicService;

    @BeforeEach
    void setUp() {
        moveLogicService = new MoveLogicService();
    }

    @Test
    @DisplayName("Testing player 1 starting turn on pit 1")
    void movingStones_playerA_movingFromFirstPit_with6Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<Integer, Integer> pits = match.getPlayerModelA().getPits();

        assertEquals(0, pits.get(1));
        assertEquals(7, pits.get(2));
        assertEquals(7, pits.get(3));
        assertEquals(7, pits.get(4));
        assertEquals(7, pits.get(5));
        assertEquals(7, pits.get(6));
        assertEquals(1, match.getPlayerModelA().getTotalScore());
    }


    @Test
    @DisplayName("Testing different pit being moved for player A")
    void movingStones_playerA_movingFromFirstPit_with5Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(1, 5);

        assertEquals(5, match.getPlayerModelA().getPits().get(1), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<Integer, Integer> pits = match.getPlayerModelA().getPits();

        assertEquals(0, pits.get(1));
        assertEquals(7, pits.get(2));
        assertEquals(7, pits.get(3));
        assertEquals(7, pits.get(4));
        assertEquals(7, pits.get(5));
        assertEquals(7, pits.get(6));
        assertEquals(0, match.getPlayerModelA().getTotalScore());
    }


    @Test
    @DisplayName("Testing moving pits around the board")
    void movingStones_playerA_movingFromFirstPit_with12Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);

        match.getPlayerModelA().getPits().put(1, 12);
        assertEquals(12, match.getPlayerModelA().getPits().get(1), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<Integer, Integer> playerAPits = match.getPlayerModelA().getPits();

        assertEquals(0, playerAPits.get(1));
        assertEquals(7, playerAPits.get(2));
        assertEquals(7, playerAPits.get(3));
        assertEquals(7, playerAPits.get(4));
        assertEquals(7, playerAPits.get(5));
        assertEquals(7, playerAPits.get(6));
        assertEquals(1, match.getPlayerModelA().getTotalScore());

        Map<Integer, Integer> playerBPits = match.getPlayerModelB().getPits();

        assertEquals(7, playerBPits.get(6));
        assertEquals(7, playerBPits.get(5));
        assertEquals(7, playerBPits.get(4));
        assertEquals(7, playerBPits.get(3));
        assertEquals(7, playerBPits.get(2));
        assertEquals(7, playerBPits.get(1));
        assertEquals(0, match.getPlayerModelB().getTotalScore());
    }


    @Test
    @DisplayName("Testing if moving handles large amount of stones")
    void movingStones_playerA_movingFromFirstPit_with13Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(1, 13);

        assertEquals(13, match.getPlayerModelA().getPits().get(1), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<Integer, Integer> playerAPits = match.getPlayerModelA().getPits();

        assertEquals(1, playerAPits.get(1));
        assertEquals(7, playerAPits.get(2));
        assertEquals(7, playerAPits.get(3));
        assertEquals(7, playerAPits.get(4));
        assertEquals(7, playerAPits.get(5));
        assertEquals(7, playerAPits.get(6));
        assertEquals(1, match.getPlayerModelA().getTotalScore());

        Map<Integer, Integer> playerBPits = match.getPlayerModelB().getPits();

        assertEquals(7, playerBPits.get(6));
        assertEquals(7, playerBPits.get(5));
        assertEquals(7, playerBPits.get(4));
        assertEquals(7, playerBPits.get(3));
        assertEquals(7, playerBPits.get(2));
        assertEquals(7, playerBPits.get(1));
        assertEquals(0, match.getPlayerModelB().getTotalScore());
    }


    @Test
    @DisplayName("Player A makes move from 2nd pit")
    void movingStones_playerA_movingFromSecondPit_with6Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(2, 6);

        assertEquals(6, match.getPlayerModelA().getPits().get(2), "The pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.SECOND, match);

        Map<Integer, Integer> playerAPits = match.getPlayerModelA().getPits();

        assertEquals(6, playerAPits.get(1));
        assertEquals(0, playerAPits.get(2));
        assertEquals(7, playerAPits.get(3));
        assertEquals(7, playerAPits.get(4));
        assertEquals(7, playerAPits.get(5));
        assertEquals(7, playerAPits.get(6));
        assertEquals(1, match.getPlayerModelA().getTotalScore());

        Map<Integer, Integer> playerBPits = match.getPlayerModelB().getPits();

        assertEquals(7, playerBPits.get(6));
        assertEquals(6, playerBPits.get(5));
        assertEquals(6, playerBPits.get(4));
        assertEquals(6, playerBPits.get(3));
        assertEquals(6, playerBPits.get(2));
        assertEquals(6, playerBPits.get(1));
        assertEquals(0, match.getPlayerModelB().getTotalScore());
    }


    @Test
    @DisplayName("Player B making move from 1st pit")
    void movingStones_playerB_movingFromFirstPit_with6Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(false);
        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        PlayerModel playerModelB = match.getPlayerModelB();
        Map<Integer, Integer> pits = playerModelB.getPits();

        assertEquals(0, pits.get(1));
        assertEquals(7, pits.get(2));
        assertEquals(7, pits.get(3));
        assertEquals(7, pits.get(4));
        assertEquals(7, pits.get(5));
        assertEquals(7, pits.get(6));
        assertEquals(1, playerModelB.getTotalScore());
    }

    private Match createDefaultMatch(boolean isPlayerAStarting) {
        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        Match match = new MancalaService(moveLogicService).createMatch(playerA, playerB);
        match.setPlayerATurn(isPlayerAStarting);
        return match;
    }
}