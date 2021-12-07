package nel.marco.mancala.service.stones;

import nel.marco.mancala.controller.model.PIT;
import nel.marco.mancala.service.Match;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * This class contains the logic on how to move the stones accordingly for active player or opponent.
 */
@Service
public class MoveLogicService {

    public Match movingStones(boolean isPlayerA, PIT pit, Match match) {

        Map<Integer, Integer> activePlayer;
        Map<Integer, Integer> opponent;

        if (isPlayerA) {
            activePlayer = match.getPlayerModelA().getPits();
            opponent = match.getPlayerModelB().getPits();
        } else {
            activePlayer = match.getPlayerModelB().getPits();
            opponent = match.getPlayerModelA().getPits();
        }

        return moveStonesToPits(pit.getPitIndex(), match, activePlayer, opponent);
    }

    private Match moveStonesToPits(int startingPoint, Match
            match, Map<Integer, Integer> activePlayer, Map<Integer, Integer> opponent) {
        int totalStones = activePlayer.get(startingPoint);
        activePlayer.put(startingPoint, 0);

        while (totalStones > 0) {
            for (int i = startingPoint + 1; i < 7; i++) {
                if (totalStones == 0)
                    break;
                activePlayer.put(i, activePlayer.get(i) + 1);
                totalStones--;
            }

            if (totalStones > 0) {
                if (match.isPlayerATurn()) {
                    match.getPlayerModelA().setTotalScore(match.getPlayerModelA().getTotalScore() + 1);
                } else {
                    match.getPlayerModelB().setTotalScore(match.getPlayerModelB().getTotalScore() + 1);
                }
                totalStones--;
            }

            if (totalStones > 0) {
                for (int i = 6; i > 0; i--) {
                    if (totalStones == 0)
                        break;
                    opponent.put(i, opponent.get(i) + 1);
                    totalStones--;
                }
            }

            if (totalStones > 0) {
                startingPoint = 0; //TODO this is hack consider change formula
            }
        }

        return match;
    }
}
