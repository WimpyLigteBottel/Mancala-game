package nel.marco.mancala.service.trigger;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.Player;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.controller.v1.model.Match;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SpecialTriggerLogicService {

    public Match hasSpecialLogicTriggered(Match updatedMatch) {

        //If last stone is in totalScore gets extra turn
        updatedMatch = getExtraTurn(updatedMatch);
        //Capture stones if last stones is placed in open spot
        updatedMatch = captureStones(updatedMatch);

        return updatedMatch;
    }

    private Match captureStones(Match updatedMatch) {
        PIT lastStoneLocation = updatedMatch.getLastStoneLocation();
        Player lastStonePlayerBoard = updatedMatch.getLastStonePlayerBoard();

        //You can't capture if it was in score pit
        if(lastStoneLocation == PIT.PLAYER_1_BOARD || lastStoneLocation == PIT.PLAYER_2_BOARD)
            return updatedMatch;

        if (lastStonePlayerBoard == Player.PLAYER1) {
            PlayerModel activePlayer = updatedMatch.getPlayerModelA();
            PlayerModel opponent = updatedMatch.getPlayerModelB();
            stealStones(lastStoneLocation, activePlayer, opponent);
        } else if (lastStonePlayerBoard == Player.PLAYER2) {
            PlayerModel activePlayer = updatedMatch.getPlayerModelB();
            PlayerModel opponent = updatedMatch.getPlayerModelA();
            stealStones(lastStoneLocation, activePlayer, opponent);
        }

        return updatedMatch;
    }

    private void stealStones(PIT lastStoneLocation, PlayerModel activePlayer, PlayerModel opponent) {
        Integer amountOfStones = activePlayer.getPits().get(lastStoneLocation);
        if (amountOfStones == 1) {
            Integer stonesBeingStolen = opponent.getPits().get(lastStoneLocation);
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
            log.info("gets extra turn  [matchId={};player={}]",updatedMatch.getUniqueMatchId(),"A");
        } else if (lastStoneLocation == PIT.PLAYER_2_BOARD && lastStonePlayerBoard == Player.PLAYER2) {
            log.info("gets extra turn  [matchId={};player={}]",updatedMatch.getUniqueMatchId(),"B");
            updatedMatch.setPlayerATurn(false);
        }

        return updatedMatch;
    }

}
