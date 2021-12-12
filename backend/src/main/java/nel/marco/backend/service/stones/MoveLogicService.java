package nel.marco.backend.service.stones;

import nel.marco.backend.controller.v1.model.Match;
import nel.marco.backend.controller.v1.model.PIT;
import nel.marco.backend.controller.v1.model.Player;
import nel.marco.backend.service.exceptions.InvalidMoveException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * This class contains the logic on how to move the stones accordingly for active player or opponent.
 */
@Service
public class MoveLogicService {

    /**
     * Handles the core logic of moving the stones around the board.
     * <p>
     * Notes: The board operates slightly differently for active player and opponent.
     * <p>
     * <p>
     * See below on how the board should flow
     * x <- x <- x
     * !         ^
     * x -> x -> x
     *
     * @param pit   The pit of stones that the player wishes to move
     * @param match The match details
     * @return
     */
    public Match movingStones(PIT pit, Match match) {

        if (match.isPlayerATurn()) {
            Map<PIT, Integer> activePlayer = match.getPlayerModelA().getPits();
            Map<PIT, Integer> opponent = match.getPlayerModelB().getPits();
            return moveStonesToPitsForPlayerA(pit, match, activePlayer, opponent);
        }

        Map<PIT, Integer> activePlayer = match.getPlayerModelB().getPits();
        Map<PIT, Integer> opponent = match.getPlayerModelA().getPits();
        return moveStonesToForPlayerB(pit, match, activePlayer, opponent);

    }

    private Match moveStonesToPitsForPlayerA(PIT startingPoint, Match
            match, Map<PIT, Integer> activePlayer, Map<PIT, Integer> opponent) {
        int totalStones = activePlayer.get(startingPoint);
        activePlayer.put(startingPoint, 0);

        if (totalStones == 0) {
            throw new InvalidMoveException("This match is done");
        }

        while (totalStones > 0) {
            for (int i = startingPoint.getPitIndex() + 1; i <= 6; i++) {
                if (totalStones == 0)
                    break;
                PIT pit = PIT.valueOf(i);
                activePlayer.put(pit, activePlayer.get(pit) + 1);
                totalStones--;
                match.setLastStoneLocation(pit);
                match.setLastStonePlayerBoard(Player.PLAYER1);
                match.setStealable(true);
            }

            if (totalStones > 0) {
                match.getPlayerModelA().setTotalScore(match.getPlayerModelA().getTotalScore() + 1);
                match.setLastStoneLocation(PIT.PLAYER_1_BOARD);
                match.setLastStonePlayerBoard(Player.PLAYER1);
                totalStones--;
                match.setStealable(false);
            }

            if (totalStones > 0) {
                for (int i = 6; i >= 1; i--) {
                    if (totalStones == 0)
                        break;
                    PIT pit = PIT.valueOf(i);
                    opponent.put(pit, opponent.get(pit) + 1);
                    totalStones--;
                    match.setLastStoneLocation(pit);
                    match.setLastStonePlayerBoard(Player.PLAYER2);
                    match.setStealable(false);
                }
            }

            if (totalStones > 0) {
                startingPoint = PIT.valueOf(1);
                activePlayer.put(startingPoint, activePlayer.get(startingPoint) + 1);
                totalStones--;
                match.setLastStoneLocation(startingPoint);
                match.setLastStonePlayerBoard(Player.PLAYER1);
                match.setStealable(true);
            }
        }

        return match;
    }


    private Match moveStonesToForPlayerB(PIT startingPoint, Match match, Map<PIT, Integer> activePlayer, Map<PIT, Integer> opponent) {
        int totalStones = activePlayer.get(startingPoint);
        activePlayer.put(startingPoint, 0);

        if (totalStones == 0) {
            throw new InvalidMoveException("This match is done");
        }

        while (totalStones > 0) {
            for (int i = startingPoint.getPitIndex() - 1; i >= 1; i--) {
                if (totalStones == 0)
                    break;
                PIT pit = PIT.valueOf(i);
                activePlayer.put(pit, activePlayer.get(pit) + 1);
                totalStones--;
                match.setLastStoneLocation(pit);
                match.setLastStonePlayerBoard(Player.PLAYER2);
                match.setStealable(true);
            }

            if (totalStones > 0) {
                match.getPlayerModelB().setTotalScore(match.getPlayerModelB().getTotalScore() + 1);
                match.setLastStoneLocation(PIT.PLAYER_2_BOARD);
                match.setLastStonePlayerBoard(Player.PLAYER2);
                totalStones--;
                match.setStealable(false);
            }

            if (totalStones > 0) {
                for (int i = 1; i <= 6; i++) {
                    if (totalStones == 0)
                        break;
                    PIT pit = PIT.valueOf(i);
                    opponent.put(pit, opponent.get(pit) + 1);
                    totalStones--;
                    match.setLastStoneLocation(pit);
                    match.setLastStonePlayerBoard(Player.PLAYER1);
                    match.setStealable(false);
                }
            }

            if (totalStones > 0) {
                startingPoint = PIT.valueOf(6);
                activePlayer.put(startingPoint, activePlayer.get(startingPoint) + 1);
                totalStones--;
                match.setLastStoneLocation(startingPoint);
                match.setLastStonePlayerBoard(Player.PLAYER2);
                match.setStealable(true);
            }
        }

        return match;
    }
}
