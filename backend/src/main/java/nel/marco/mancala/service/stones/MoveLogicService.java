package nel.marco.mancala.service.stones;

import nel.marco.mancala.controller.model.PIT;
import nel.marco.mancala.controller.model.Player;
import nel.marco.mancala.service.Match;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * This class contains the logic on how to move the stones accordingly for active player or opponent.
 */
@Service
public class MoveLogicService {

    public Match movingStones(boolean isPlayerA, PIT pit, Match match) {

        Map<PIT, Integer> activePlayer;
        Map<PIT, Integer> opponent;

        if (isPlayerA) {
            activePlayer = match.getPlayerModelA().getPits();
            opponent = match.getPlayerModelB().getPits();
            return moveStonesToPits(pit, Player.PLAYER1, match, activePlayer, opponent);
        } else {
            activePlayer = match.getPlayerModelB().getPits();
            opponent = match.getPlayerModelA().getPits();
            return moveStonesToPits(pit, Player.PLAYER2, match, activePlayer, opponent);
        }

    }

    private Match moveStonesToPits(PIT startingPoint, Player player, Match
            match, Map<PIT, Integer> activePlayer, Map<PIT, Integer> opponent) {
        int totalStones = activePlayer.get(startingPoint);
        activePlayer.put(startingPoint, 0);

        while (totalStones > 0) {
            for (int i = startingPoint.getPitIndex() + 1; i < 7; i++) {
                if (totalStones == 0)
                    break;
                PIT pit = PIT.valueOf(i);
                activePlayer.put(pit, activePlayer.get(pit) + 1);
                totalStones--;
                match.setLastStoneLocation(pit);
                match.setLastStonePlayerBoard(Player.PLAYER1);
            }

            if (totalStones > 0) {
                if (player == Player.PLAYER1) {
                    match.getPlayerModelA().setTotalScore(match.getPlayerModelA().getTotalScore() + 1);
                    match.setLastStoneLocation(PIT.PLAYER_1_BOARD);
                    match.setLastStonePlayerBoard(Player.PLAYER1);

                } else if (player == Player.PLAYER2) {
                    match.getPlayerModelB().setTotalScore(match.getPlayerModelB().getTotalScore() + 1);
                    match.setLastStoneLocation(PIT.PLAYER_2_BOARD);
                    match.setLastStonePlayerBoard(Player.PLAYER2);
                }
                totalStones--;
            }

            if (totalStones > 0) {
                for (int i = 6; i > 0; i--) {
                    if (totalStones == 0)
                        break;
                    PIT pit = PIT.valueOf(i);
                    opponent.put(pit, opponent.get(pit) + 1);
                    totalStones--;
                    match.setLastStoneLocation(pit);
                    match.setLastStonePlayerBoard(Player.PLAYER2);
                }
            }

            if (totalStones > 0) {
                startingPoint = PIT.valueOf(1);
                activePlayer.put(startingPoint, activePlayer.get(startingPoint) + 1);
                totalStones--;
                match.setLastStoneLocation(startingPoint);
                match.setLastStonePlayerBoard(Player.PLAYER1);
            }
        }

        return match;
    }
}
