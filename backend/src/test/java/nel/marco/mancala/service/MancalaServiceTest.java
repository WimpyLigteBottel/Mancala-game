package nel.marco.mancala.service;

import nel.marco.mancala.controller.model.PlayerModel;
import nel.marco.mancala.service.stones.MoveLogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MancalaServiceTest {

    MancalaService mancalaService;

    @Mock
    MoveLogicService mockMoveLogicService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mancalaService = new MancalaService(mockMoveLogicService);
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


}