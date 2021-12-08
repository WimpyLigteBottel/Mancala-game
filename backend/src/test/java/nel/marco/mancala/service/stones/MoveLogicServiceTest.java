package nel.marco.mancala.service.stones;

import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.Player;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.service.MancalaService;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.service.trigger.SpecialTriggerLogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Map<PIT, Integer> pits = match.getPlayerModelA().getPits();

        assertEquals(0, pits.get(PIT.FIRST));
        assertEquals(7, pits.get(PIT.SECOND));
        assertEquals(7, pits.get(PIT.THIRD));
        assertEquals(7, pits.get(PIT.FOURTH));
        assertEquals(7, pits.get(PIT.FIFTH));
        assertEquals(7, pits.get(PIT.SIX));
        assertEquals(1, match.getPlayerModelA().getTotalScore());
    }


    @Test
    @DisplayName("Testing different pit being moved for player A")
    void movingStones_playerA_movingFromFirstPit_with5Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(PIT.FIRST, 5);

        assertEquals(5, match.getPlayerModelA().getPits().get(PIT.FIRST), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<PIT, Integer> pits = match.getPlayerModelA().getPits();

        assertEquals(0, pits.get(PIT.FIRST));
        assertEquals(7, pits.get(PIT.SECOND));
        assertEquals(7, pits.get(PIT.THIRD));
        assertEquals(7, pits.get(PIT.FOURTH));
        assertEquals(7, pits.get(PIT.FIFTH));
        assertEquals(7, pits.get(PIT.SIX));
        assertEquals(0, match.getPlayerModelA().getTotalScore());
    }

    @Test
    @DisplayName("Making sure last location being set is correct")
    void movingStones_checkLastPlaceLocation_expectCorrectPlacedLocation() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(PIT.FIRST, 1);
        assertEquals(1, match.getPlayerModelA().getPits().get(PIT.FIRST), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<PIT, Integer> pits = match.getPlayerModelA().getPits();

        assertEquals(0, pits.get(PIT.FIRST));
        assertEquals(7, pits.get(PIT.SECOND));
        assertEquals(6, pits.get(PIT.THIRD));

        assertEquals(PIT.SECOND, match.getLastStoneLocation());
        assertEquals(Player.PLAYER1, match.getLastStonePlayerBoard());
    }

    @Test
    @DisplayName("Making sure last location being set is correct")
    void movingStones_checkLastPlaceLocation_scoreBoard_expectCorrectPlacedLocation() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(PIT.SIX, 1);
        assertEquals(1, match.getPlayerModelA().getPits().get(PIT.SIX), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.SIX, match);

        Map<PIT, Integer> pits = match.getPlayerModelA().getPits();

        assertEquals(0, pits.get(PIT.SIX));
        assertEquals(1, match.getPlayerModelA().getTotalScore());

        assertEquals(PIT.PLAYER_1_BOARD, match.getLastStoneLocation());
        assertEquals(Player.PLAYER1, match.getLastStonePlayerBoard());
    }


    @Test
    @DisplayName("Testing moving pits around the board")
    void movingStones_playerA_movingFromFirstPit_with12Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);

        match.getPlayerModelA().getPits().put(PIT.FIRST, 12);
        assertEquals(12, match.getPlayerModelA().getPits().get(PIT.FIRST), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<PIT, Integer> playerAPits = match.getPlayerModelA().getPits();

        assertEquals(0, playerAPits.get(PIT.FIRST));
        assertEquals(7, playerAPits.get(PIT.SECOND));
        assertEquals(7, playerAPits.get(PIT.THIRD));
        assertEquals(7, playerAPits.get(PIT.FOURTH));
        assertEquals(7, playerAPits.get(PIT.FIFTH));
        assertEquals(7, playerAPits.get(PIT.SIX));
        assertEquals(1, match.getPlayerModelA().getTotalScore());

        Map<PIT, Integer> playerBPits = match.getPlayerModelB().getPits();


        assertEquals(7, playerBPits.get(PIT.SIX));
        assertEquals(7, playerBPits.get(PIT.FIFTH));
        assertEquals(7, playerBPits.get(PIT.FOURTH));
        assertEquals(7, playerBPits.get(PIT.THIRD));
        assertEquals(7, playerBPits.get(PIT.SECOND));
        assertEquals(7, playerBPits.get(PIT.FIRST));
        assertEquals(0, match.getPlayerModelB().getTotalScore());
    }

    @Test
    @DisplayName("Testing if moving handles large amount of stones")
    void movingStones_playerA_movingFromFirstPit_with13Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(PIT.FIRST, 13);

        assertEquals(13, match.getPlayerModelA().getPits().get(PIT.FIRST), "The first pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        Map<PIT, Integer> playerAPits = match.getPlayerModelA().getPits();

        assertEquals(1, playerAPits.get(PIT.FIRST));
        assertEquals(7, playerAPits.get(PIT.SECOND));
        assertEquals(7, playerAPits.get(PIT.THIRD));
        assertEquals(7, playerAPits.get(PIT.FOURTH));
        assertEquals(7, playerAPits.get(PIT.FIFTH));
        assertEquals(7, playerAPits.get(PIT.SIX));
        assertEquals(1, match.getPlayerModelA().getTotalScore());

        Map<PIT, Integer> playerBPits = match.getPlayerModelB().getPits();

        assertEquals(7, playerBPits.get(PIT.SIX));
        assertEquals(7, playerBPits.get(PIT.FIFTH));
        assertEquals(7, playerBPits.get(PIT.FOURTH));
        assertEquals(7, playerBPits.get(PIT.THIRD));
        assertEquals(7, playerBPits.get(PIT.SECOND));
        assertEquals(7, playerBPits.get(PIT.FIRST));
        assertEquals(0, match.getPlayerModelB().getTotalScore());
    }


    @Test
    @DisplayName("Player A makes move from 2nd pit")
    void movingStones_playerA_movingFromSecondPit_with6Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(PIT.SECOND, 6);

        assertEquals(6, match.getPlayerModelA().getPits().get(PIT.SECOND), "The pit did not have correct stones for setup");

        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.SECOND, match);

        Map<PIT, Integer> playerAPits = match.getPlayerModelA().getPits();

        assertEquals(6, playerAPits.get(PIT.FIRST));
        assertEquals(0, playerAPits.get(PIT.SECOND));
        assertEquals(7, playerAPits.get(PIT.THIRD));
        assertEquals(7, playerAPits.get(PIT.FOURTH));
        assertEquals(7, playerAPits.get(PIT.FIFTH));
        assertEquals(7, playerAPits.get(PIT.SIX));
        assertEquals(1, match.getPlayerModelA().getTotalScore());

        Map<PIT, Integer> playerBPits = match.getPlayerModelB().getPits();

        assertEquals(7, playerBPits.get(PIT.SIX));
        assertEquals(6, playerBPits.get(PIT.FIFTH));
        assertEquals(6, playerBPits.get(PIT.FOURTH));
        assertEquals(6, playerBPits.get(PIT.THIRD));
        assertEquals(6, playerBPits.get(PIT.SECOND));
        assertEquals(6, playerBPits.get(PIT.FIRST));
        assertEquals(0, match.getPlayerModelB().getTotalScore());
    }


    @Test
    @DisplayName("Player B making move from 1st pit")
    void movingStones_playerB_movingFromFirstPit_with6Stones_expectCorrectStonePlacement() {

        Match match = createDefaultMatch(false);
        match = moveLogicService.movingStones(match.isPlayerATurn(), PIT.FIRST, match);

        PlayerModel playerModelB = match.getPlayerModelB();
        Map<PIT, Integer> pits = playerModelB.getPits();

        assertEquals(0, pits.get(PIT.FIRST));
        assertEquals(7, pits.get(PIT.SECOND));
        assertEquals(7, pits.get(PIT.THIRD));
        assertEquals(7, pits.get(PIT.FOURTH));
        assertEquals(7, pits.get(PIT.FIFTH));
        assertEquals(7, pits.get(PIT.SIX));
        assertEquals(1, playerModelB.getTotalScore());
    }

    private Match createDefaultMatch(boolean isPlayerAStarting) {
        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        Match match = new MancalaService(moveLogicService, new SpecialTriggerLogicService()).createMatch(playerA, playerB);
        match.setPlayerATurn(isPlayerAStarting);
        return match;
    }
}