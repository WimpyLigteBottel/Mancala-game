package nel.marco.mancala.service.trigger;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.Player;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import org.springframework.stereotype.Service;

/**
 * This class handles the special "rules" related to the game.
 * <p>
 * Example: captures stones,game over, getting extra turns ...
 */
@Slf4j
@Service
public class SpecialTriggerLogicService {

    public Match hasSpecialLogicTriggered(Match updatedMatch) {

        captureStones(updatedMatch);

        updatedMatch = isGameOver(updatedMatch);

        if (updatedMatch.isGameOver()) {
            return updatedMatch;
        }

        updatedMatch = seeIfSomeoneGetsExtraTurn(updatedMatch);

        return updatedMatch;
    }

    private Match isGameOver(Match updatedMatch) {

        updatedMatch = isGameOver(updatedMatch, updatedMatch.getPlayerModelA(), updatedMatch.getPlayerModelB());
        updatedMatch = isGameOver(updatedMatch, updatedMatch.getPlayerModelB(), updatedMatch.getPlayerModelA());

        return updatedMatch;
    }

    private Match isGameOver(Match updatedMatch, PlayerModel activePlayer, PlayerModel opponent) {
        boolean isFieldClear = true;
        for (int i = 1; i < 7; i++) {
            if (activePlayer.getPits().get(PIT.valueOf(i)) != 0) {
                isFieldClear = false;
            }
        }

        if (isFieldClear) {
            int addUpRemainingStones = opponent.getPits().values().stream().mapToInt(Integer::intValue).sum();
            for (int i = 1; i < 7; i++) {
                opponent.getPits().put(PIT.valueOf(i), 0);
            }
            long totalScore = opponent.getTotalScore();
            opponent.setTotalScore(totalScore + addUpRemainingStones);
            updatedMatch.setGameOver(true);
        }

        return updatedMatch;
    }

    private void captureStones(Match updatedMatch) {
        PIT lastStoneLocation = updatedMatch.getLastStoneLocation();
        Player lastStonePlayerBoard = updatedMatch.getLastStonePlayerBoard();

        //You can't capture if it was in score pit
        if (!updatedMatch.isStealable())
            return;

        PlayerModel activePlayer = null;
        PlayerModel opponent = null;

        if (updatedMatch.isPlayerATurn() && lastStonePlayerBoard == Player.PLAYER1) {
            activePlayer = updatedMatch.getPlayerModelA();
            opponent = updatedMatch.getPlayerModelB();
        } else if (!updatedMatch.isPlayerATurn() && lastStonePlayerBoard == Player.PLAYER2) {
            activePlayer = updatedMatch.getPlayerModelB();
            opponent = updatedMatch.getPlayerModelA();
        }

        if (activePlayer == null || opponent == null)
            return;

        Integer stonesInPit = activePlayer.getPits().get(lastStoneLocation);
        if (stonesInPit == 1) {
            stealStones(lastStoneLocation, activePlayer, opponent);
        }
    }

    private void stealStones(PIT lastStoneLocation, PlayerModel activePlayer, PlayerModel opponent) {
        Integer stonesBeingStolen = opponent.getPits().get(lastStoneLocation);

        if (stonesBeingStolen > 0) {
            opponent.getPits().put(lastStoneLocation, 0);

            activePlayer.setTotalScore(activePlayer.getTotalScore() + activePlayer.getPits().get(lastStoneLocation) + stonesBeingStolen);
            activePlayer.getPits().put(lastStoneLocation, 0);
            log.info("Stealing stones [stoneLocation={};activePlayer={};opponent={}]", lastStoneLocation, activePlayer.getUniqueId(), opponent.getUniqueId());
        }

    }

    private Match seeIfSomeoneGetsExtraTurn(Match updatedMatch) {
        PIT lastStoneLocation = updatedMatch.getLastStoneLocation();
        Player lastStonePlayerBoard = updatedMatch.getLastStonePlayerBoard();

        if (lastStoneLocation == PIT.PLAYER_1_BOARD && lastStonePlayerBoard == Player.PLAYER1) {
            updatedMatch.setPlayerATurn(true);
            log.info("gets extra turn  [matchId={};player={}]", updatedMatch.getUniqueMatchId(), "A");
        } else if (lastStoneLocation == PIT.PLAYER_2_BOARD && lastStonePlayerBoard == Player.PLAYER2) {
            log.info("gets extra turn  [matchId={};player={}]", updatedMatch.getUniqueMatchId(), "B");
            updatedMatch.setPlayerATurn(false);
        } else {
            updatedMatch.setPlayerATurn(!updatedMatch.isPlayerATurn());
        }

        return updatedMatch;
    }

}
