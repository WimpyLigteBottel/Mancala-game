package nel.marco.mancala.service.trigger;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.Player;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class SpecialTriggerLogicService {

    public Match hasSpecialLogicTriggered(Match updatedMatch) {
        //Capture stones if last stones is placed in open spot
        updatedMatch = captureStones(updatedMatch);

        updatedMatch = isGameOver(updatedMatch);

        if (updatedMatch.isGameOver()) {
            return updatedMatch;
        }

        //If last stone is in totalScore gets extra turn
        updatedMatch = getExtraTurn(updatedMatch);

        return updatedMatch;
    }

    private Match isGameOver(Match updatedMatch) {


        Map<PIT, Integer> playerAPits = updatedMatch.getPlayerModelA().getPits();
        Map<PIT, Integer> playerBPits = updatedMatch.getPlayerModelB().getPits();


        updatedMatch = isGameOver(updatedMatch, playerAPits, playerBPits);
        updatedMatch = isGameOver(updatedMatch, playerBPits, playerAPits);

        return updatedMatch;
    }

    private Match isGameOver(Match updatedMatch, Map<PIT, Integer> activePlayer, Map<PIT, Integer> opponent) {
        boolean isFieldClear = true;
        for (int i = 1; i < 7; i++) {
            if (activePlayer.get(PIT.valueOf(i)) != 0) {
                isFieldClear = false;
            }
        }

        if (isFieldClear) {
            int addUpRemainingStones = opponent.values().stream().mapToInt(Integer::intValue).sum();
            for (int i = 1; i < 7; i++) {
                opponent.put(PIT.valueOf(i), 0);
            }
            long totalScore = updatedMatch.getPlayerModelB().getTotalScore();
            updatedMatch.getPlayerModelB().setTotalScore(totalScore + addUpRemainingStones);
            updatedMatch.setGameOver(true);
        }

        return updatedMatch;
    }

    private Match captureStones(Match updatedMatch) {
        PIT lastStoneLocation = updatedMatch.getLastStoneLocation();
        Player lastStonePlayerBoard = updatedMatch.getLastStonePlayerBoard();

        //You can't capture if it was in score pit
        if (!updatedMatch.isStealable())
            return updatedMatch;

        if (updatedMatch.isPlayerATurn() && lastStonePlayerBoard == Player.PLAYER1 && updatedMatch.isStealable()) {
            PlayerModel activePlayer = updatedMatch.getPlayerModelA();
            PlayerModel opponent = updatedMatch.getPlayerModelB();
            Integer stonesInPit = activePlayer.getPits().get(lastStoneLocation);
            if (stonesInPit == 1) {
                stealStones(lastStoneLocation, activePlayer, opponent);
            }
        } else if (!updatedMatch.isPlayerATurn() && lastStonePlayerBoard == Player.PLAYER2 && updatedMatch.isStealable()) {
            PlayerModel activePlayer = updatedMatch.getPlayerModelB();
            PlayerModel opponent = updatedMatch.getPlayerModelA();

            Integer stonesInPit = activePlayer.getPits().get(lastStoneLocation);
            if (stonesInPit == 1) {
                stealStones(lastStoneLocation, activePlayer, opponent);
            }
        }

        return updatedMatch;
    }

    private void stealStones(PIT lastStoneLocation, PlayerModel activePlayer, PlayerModel opponent) {
        log.info("Stealing stones [stoneLocation={};]", lastStoneLocation);
        Integer stonesBeingStolen = opponent.getPits().get(lastStoneLocation);

        if(stonesBeingStolen> 0){
            opponent.getPits().put(lastStoneLocation, 0);

            activePlayer.setTotalScore(activePlayer.getTotalScore() + activePlayer.getPits().get(lastStoneLocation) + stonesBeingStolen);
            activePlayer.getPits().put(lastStoneLocation, 0);
        }

    }

    private Match getExtraTurn(Match updatedMatch) {
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
