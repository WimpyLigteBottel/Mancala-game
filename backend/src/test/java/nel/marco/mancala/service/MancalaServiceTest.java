package nel.marco.mancala.service;

import nel.marco.mancala.controller.model.PIT;
import nel.marco.mancala.controller.model.PlayerModel;
import nel.marco.mancala.service.stones.MoveLogicService;
import nel.marco.mancala.service.trigger.SpecialTriggerLogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class MancalaServiceTest {

    MancalaService mancalaService;

    @Mock
    MoveLogicService mockMoveLogicService;

    @Mock
    SpecialTriggerLogicService specialTriggerLogicService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mancalaService = new MancalaService(mockMoveLogicService, specialTriggerLogicService);
    }

    @Test
    @DisplayName("There should be 7 pits in total per player")
    void createBoard_expectBoardLengthToBeCorrect() {

        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        Match match = mancalaService.createMatch(playerA, playerB);

        assertEquals(6, match.getPlayerModelA().getPits().size());
        assertEquals(6, match.getPlayerModelA().getPits().size());
    }

    @Test
    @DisplayName("Each pit == 6 marbles")
    void createBoard_expectStartingPitsToBeEqual() {

        PlayerModel playerA = new PlayerModel();
        PlayerModel playerB = new PlayerModel();
        mancalaService.createMatch(playerA, playerB);

        playerA.getPits().forEach((index, marbles) -> {
            assertEquals(6, marbles, "The amount of marbles in pit should be 6");
        });

        playerB.getPits().forEach((index, marbles) -> {
            assertEquals(6, marbles, "The amount of marbles in pit should be 6");
        });
    }


    @Test
    void executeCommand() {
    }


    @Test
    @DisplayName("Player A Makes move to get extra turn")
    void movingStones_lastStoneLandsInScoreboard_playerA_expectExtraTurn() {

        Match match = createDefaultMatch(true);
        match.getPlayerModelA().getPits().put(PIT.SIX, 1);
        assertEquals(1, match.getPlayerModelA().getPits().get(PIT.SIX), "The first pit did not have correct stones for setup");

        Command command = new Command();
        command.setMatchID(match.getUniqueMatchId());
        command.setPit(PIT.SIX);
        command.setPlayerUniqueId(match.getPlayerModelA().getUniqueId());


        when(mockMoveLogicService.movingStones(eq(true), eq(command.getPit()), any())).thenReturn(match);
        when(specialTriggerLogicService.hasSpecialLogicTriggered(any())).thenReturn(match);
        mancalaService.executeCommand(command);

        assertEquals(true, match.isPlayerATurn());

        assertTrue(mancalaService.internalMemoryMap.get(match.getUniqueMatchId()).isPlayerATurn());
    }

    @Test
    @DisplayName("Player B Makes move to get extra turn")
    void executeCommand_lastStoneLandsInScoreboard_playerB_expectExtraTurn() {

        Match match = createDefaultMatch(false);
        match.getPlayerModelB().getPits().put(PIT.SIX, 1);
        assertEquals(1, match.getPlayerModelB().getPits().get(PIT.SIX), "The first pit did not have correct stones for setup");

        Command command = new Command();
        command.setMatchID(match.getUniqueMatchId());
        command.setPit(PIT.SIX);
        command.setPlayerUniqueId(match.getPlayerModelB().getUniqueId());

        mancalaService.executeCommand(command);

        assertEquals(false, match.isPlayerATurn());

        assertFalse(mancalaService.internalMemoryMap.get(match.getUniqueMatchId()).isPlayerATurn());
    }

    private Match createDefaultMatch(boolean isPlayerAStarting) {
        PlayerModel playerA = new PlayerModel();
        playerA.setId(0);
        playerA.setUniqueId(UUID.randomUUID().toString());
        PlayerModel playerB = new PlayerModel();
        playerB.setId(1);
        playerB.setUniqueId(UUID.randomUUID().toString());
        Match match = mancalaService.createMatch(playerA, playerB);
        match.setPlayerATurn(isPlayerAStarting);
        return match;
    }

}