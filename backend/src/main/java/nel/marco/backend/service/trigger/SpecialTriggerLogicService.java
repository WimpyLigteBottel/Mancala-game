package nel.marco.backend.service.trigger;

import lombok.extern.slf4j.Slf4j;
import nel.marco.backend.controller.v1.model.Match;
import nel.marco.backend.controller.v1.model.PIT;
import nel.marco.backend.controller.v1.model.Player;
import nel.marco.backend.controller.v1.model.PlayerModel;
import org.springframework.stereotype.Service;

/**
 * This class handles the special "rules" related to the game.
 * <p>
 * Example: captures stones,game over, getting extra turns ...
 */
@Slf4j
@Service
public class SpecialTriggerLogicService {

    /**
     * The base method to check against all special rules for
     * a match and executes rules if any of the special rules should trigger
     *
     * @param match
     * @return
     */
    public Match hasSpecialLogicTriggered(Match match) {

        captureStones(match);

        checkIfTheGameIsOver(match);

        if (match.isGameOver()) {
            return match;
        }

        seeIfSomeoneGetsExtraTurn(match);

        return match;
    }

    /**
     * Checks the if the game is over for the players.
     *
     * @param match
     */
    private void checkIfTheGameIsOver(Match match) {
        handleGameOverLogic(match, match.getPlayerModelA(), match.getPlayerModelB());
        handleGameOverLogic(match, match.getPlayerModelB(), match.getPlayerModelA());
    }

    /**
     * First checks if the activePlayer board is empty (aka 0,0,0,0,0) and if it is. Add up all the remaining
     * stones on the opponent board and add it to his totalscore
     *
     * @param updatedMatch
     * @param activePlayer
     * @param opponent
     */
    private void handleGameOverLogic(Match updatedMatch, PlayerModel activePlayer, PlayerModel opponent) {
        for (int i = 1; i <= 6; i++) {
            if (activePlayer.getPits().get(PIT.valueOf(i)) != 0) {
                return;
            }
        }

        int addUpRemainingStones = opponent.getPits().values().stream().mapToInt(Integer::intValue).sum();
        for (int i = 1; i <= 6; i++) {
            opponent.getPits().put(PIT.valueOf(i), 0);
        }
        long totalScore = opponent.getTotalScore();
        opponent.setTotalScore(totalScore + addUpRemainingStones);
        updatedMatch.setGameOver(true);
    }

    /**
     * Checks the games state and see if stones should be captured or not.
     * <p>
     * If stones MUST be captured then capture the stones accordingly
     *
     * @param match The game state
     */
    private void captureStones(Match match) {
        PIT lastStoneLocation = match.getLastStoneLocation();
        Player lastStonePlayerBoard = match.getLastStonePlayerBoard();

        if (!match.isStealable())
            return;

        PlayerModel activePlayer = null;
        PlayerModel opponent = null;

        if (match.isPlayerATurn() && lastStonePlayerBoard == Player.PLAYER1) {
            activePlayer = match.getPlayerModelA();
            opponent = match.getPlayerModelB();
        } else if (!match.isPlayerATurn() && lastStonePlayerBoard == Player.PLAYER2) {
            activePlayer = match.getPlayerModelB();
            opponent = match.getPlayerModelA();
        }

        if (activePlayer == null || opponent == null)
            return;

        Integer stonesInPit = activePlayer.getPits().get(lastStoneLocation);
        if (stonesInPit == 1) {
            stealStones(lastStoneLocation, activePlayer, opponent);
        }
    }

    /**
     * Moving the stones to scoreboard of active player and clear both pits that was captured
     *
     * @param lastStoneLocation
     * @param activePlayer
     * @param opponent
     */
    private void stealStones(PIT lastStoneLocation, PlayerModel activePlayer, PlayerModel opponent) {
        Integer stonesBeingStolen = opponent.getPits().get(lastStoneLocation);

        if (stonesBeingStolen > 0) {
            opponent.getPits().put(lastStoneLocation, 0);

            activePlayer.setTotalScore(activePlayer.getTotalScore() + activePlayer.getPits().get(lastStoneLocation) + stonesBeingStolen);
            activePlayer.getPits().put(lastStoneLocation, 0);
            log.info("Stealing stones [stoneLocation={};activePlayer={};opponent={}]", lastStoneLocation, activePlayer.getUniqueId(), opponent.getUniqueId());
        }

    }

    /**
     * Handles the logic around turns for players. If player last stones landed in the scoreboard.
     * They will get extra turn.
     * <p>
     * Note: This can happen many times
     *
     * @param match
     */
    private void seeIfSomeoneGetsExtraTurn(Match match) {
        PIT lastStoneLocation = match.getLastStoneLocation();
        Player lastStonePlayerBoard = match.getLastStonePlayerBoard();

        if (lastStoneLocation == PIT.PLAYER_1_BOARD && lastStonePlayerBoard == Player.PLAYER1) {
            match.setPlayerATurn(true);
            log.info("gets extra turn  [matchId={};player={}]", match.getUniqueMatchId(), match.getPlayerModelA().getUniqueId());
        } else if (lastStoneLocation == PIT.PLAYER_2_BOARD && lastStonePlayerBoard == Player.PLAYER2) {
            match.setPlayerATurn(false);
            log.info("gets extra turn  [matchId={};player={}]", match.getUniqueMatchId(), match.getPlayerModelB().getUniqueId());
        } else {
            match.setPlayerATurn(!match.isPlayerATurn());
        }
    }

}
