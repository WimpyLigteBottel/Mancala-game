package nel.marco.mancala.service;

import nel.marco.mancala.controller.v1.model.Command;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.service.exceptions.InvalidMoveException;
import nel.marco.mancala.service.stones.MoveLogicService;
import nel.marco.mancala.service.trigger.SpecialTriggerLogicService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
    @DisplayName("Trying to move from scoreboard pit")
    void executeCommand_movingFromScoreboardPit_expectException() {

        Assertions.assertThrows(InvalidMoveException.class, () -> {
            Match match = createDefaultMatch(true);
            Command command = new Command();
            command.setPit(PIT.PLAYER_1_BOARD);

            mancalaService.executeCommand(command, match.getUniqueMatchId(), match.getPlayerModelA().getUniqueId());
        });

        Assertions.assertThrows(InvalidMoveException.class, () -> {
            Match match = createDefaultMatch(false);

            Command command = new Command();
            command.setPit(PIT.PLAYER_2_BOARD);
            mancalaService.executeCommand(command, match.getUniqueMatchId(), match.getPlayerModelB().getUniqueId());

        });
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
    @DisplayName("Gets the match id back after executing command")
    void executeCommand_expectMatchIdToBeReturned() {

        Match match = createDefaultMatch(false);
        match.getPlayerModelB().getPits().put(PIT.SIX, 1);
        assertEquals(1, match.getPlayerModelB().getPits().get(PIT.SIX), "The first pit did not have correct stones for setup");

        Command command = new Command();
        command.setPit(PIT.SIX);

        when(mockMoveLogicService.movingStones(any(), any())).thenReturn(match);
        when(specialTriggerLogicService.hasSpecialLogicTriggered(any())).thenReturn(match);
        String actual = mancalaService.executeCommand(command, match.getUniqueMatchId(), match.getPlayerModelB().getUniqueId());

        assertEquals(match.getUniqueMatchId(), actual);
    }

    @Test
    @DisplayName("Checks that last move has been cleared")
    void executeCommand_expectLastMoveDataToBeCleared() {

        Match match = createDefaultMatch(false);

        Command command = new Command();
        command.setPit(PIT.SIX);

        when(mockMoveLogicService.movingStones(any(), any())).thenReturn(match);
        when(specialTriggerLogicService.hasSpecialLogicTriggered(any())).thenReturn(match);
        String actual = mancalaService.executeCommand(command, match.getUniqueMatchId(), match.getPlayerModelB().getUniqueId());

        Optional<Match> optionalMatch = mancalaService.getMatch(actual);
        assertTrue(optionalMatch.isPresent());

        assertNull(optionalMatch.get().getLastStoneLocation());
        assertNull(optionalMatch.get().getLastStonePlayerBoard());
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