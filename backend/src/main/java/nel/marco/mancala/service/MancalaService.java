package nel.marco.mancala.service;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.model.PIT;
import nel.marco.mancala.controller.model.PlayerModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MancalaService {


    Map<String, Match> internalMemoryMap = new ConcurrentHashMap<>();

    /**
     * Sets up the boardstate for both players.
     *
     * @param playerA The player A (first player)
     * @param playerB The player A (first player)
     */
    public void createBoard(PlayerModel playerA, PlayerModel playerB) {

        int[] initialBoardState = new int[6];

        for (int i = 0; i < initialBoardState.length; i++) {
            initialBoardState[i] = 6;
        }

        playerA.setPits(Arrays.copyOf(initialBoardState, 6));
        playerB.setPits(Arrays.copyOf(initialBoardState, 6));
    }

    /**
     * 1. Finds the match based on the command's match ID
     * 2. Find out who's turn it is and make sure that its the correct players command
     * 3. Execute the logic to move the stones accordingly
     *
     * @param command The command form the player
     */
    public void executeCommand(Command command) {

        Match match = internalMemoryMap.get(command.getMatchID());

        if (match == null) {
            throw new RuntimeException("This match does not exist");//TODO:Replace with proper Exception to indicate what the issue was
        }

        PlayerModel playerModelA = match.getPlayerModelA();
        PlayerModel playerModelB = match.getPlayerModelB();

        boolean isPlayerA = command.getPlayerUniqueId().equals(playerModelA.getUniqueId());
        boolean isPlayerB = command.getPlayerUniqueId().equals(playerModelB.getUniqueId());

        if (!isPlayerA && !isPlayerB) {
            throw new RuntimeException("Unknown player ->" + command.getPlayerUniqueId()); //TODO: make this more clear and add the player
        }

        if (isPlayerA && match.isPlayerATurn()) {

            int pit = playerModelA.getPits()[command.getPit().getPitIndex()];


        }

    }


    public void movingStones(boolean isPlayerA, PIT pit, Match match) {

        if (isPlayerA) {

            int[] boardA = match.getPlayerModelA().getPits();
            int[] boardB = match.getPlayerModelB().getPits();

            switch (pit.getPitIndex()) {
                case 0:
                    int totalStones = boardB[0];

                    if (totalStones > 0) {
                        for (int i = 1; i < 5; i++) {
                            if (totalStones == 0)
                                break;
                            boardB[i] = boardB[i] + 1;
                            totalStones--;
                        }
                    }

                    if (totalStones > 0)
                        match.getPlayerModelA().setTotalScore(match.getPlayerModelA().getTotalScore() + 1);

                    if (totalStones > 0) {
                        for (int i = 5; i >= 0; i--) {
                            if (totalStones == 0)
                                break;
                            boardB[i] = boardB[i] + 1;
                            totalStones--;
                        }
                    }
            }

            System.out.println(boardA);
            System.out.println(boardB);


        }

    }
}
