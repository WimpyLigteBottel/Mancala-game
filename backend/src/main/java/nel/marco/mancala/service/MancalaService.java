package nel.marco.mancala.service;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.model.Command;
import nel.marco.mancala.controller.model.PIT;
import nel.marco.mancala.controller.model.PlayerModel;
import nel.marco.mancala.service.stones.MoveLogicService;
import nel.marco.mancala.service.trigger.SpecialTriggerLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MancalaService {

    private final MoveLogicService moveLogicService;
    private final SpecialTriggerLogicService specialTriggerLogicService;


    @Autowired // This is technically not needed, but I like to indicate it like this
    public MancalaService(MoveLogicService moveLogicService, SpecialTriggerLogicService specialTriggerLogicService) {
        this.moveLogicService = moveLogicService;
        this.specialTriggerLogicService = specialTriggerLogicService;
    }

    Map<String, Match> internalMemoryMap = new ConcurrentHashMap<>();


    public Optional<Match> getMatch(String uniqueMatch) {
        return Optional.ofNullable(internalMemoryMap.get(uniqueMatch));
    }



    /**
     * Sets up the boardstate for both players.
     *
     * @param playerA The player A (first player)
     * @param playerB The player A (first player)
     */
    public Match createMatch(PlayerModel playerA, PlayerModel playerB) {

        Map<PIT, Integer> initialBoardState = new HashMap<>();

        for (int i = 1; i < 7; i++) {
            initialBoardState.put(PIT.valueOf(i), 6);
        }

        playerA.setPits(new HashMap<>(initialBoardState));
        playerB.setPits(new HashMap<>(initialBoardState));

        Match match = new Match();
        match.setPlayerModelA(playerA);
        match.setPlayerModelB(playerB);
        match.setUniqueMatchId(UUID.randomUUID().toString());

        internalMemoryMap.put(match.getUniqueMatchId(), match);

        return match;
    }

    /**
     * 1. Finds the match based on the command's match ID
     * 2. Find out who's turn it is and make sure that its the correct players command
     * 3. Execute the logic to move the stones accordingly
     * 4. Update the match stats
     *
     * @param command The command form the player
     * @return
     */
    public String executeCommand(Command command) {

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

        //NOTE: the isPlayerA is like safetyCheck to make sure its not someone else trying to cheat ;)
        if (isPlayerA && match.isPlayerATurn()) {
            Match updatedMatch = moveLogicService.movingStones(true, command.getPit(), match);
            updatedMatch.setPlayerATurn(false);
            updatedMatch = specialTriggerLogicService.hasSpecialLogicTriggered(updatedMatch);
            clearNecessaryDataForNextMatch(updatedMatch);
            internalMemoryMap.put(command.getMatchID(), updatedMatch);
        } else if (isPlayerB && !match.isPlayerATurn()) {
            Match updatedMatch = moveLogicService.movingStones(false, command.getPit(), match);
            updatedMatch.setPlayerATurn(true);
            updatedMatch = specialTriggerLogicService.hasSpecialLogicTriggered(updatedMatch);
            clearNecessaryDataForNextMatch(updatedMatch);
            internalMemoryMap.put(command.getMatchID(), updatedMatch);
        } else {
            log.error("INVALID PLAYER COMMAND!!!"); // TODO: handle this more nicely*
        }

        return match.getUniqueMatchId();

    }

    private void clearNecessaryDataForNextMatch(Match updatedMatch) {
        updatedMatch.setLastStoneLocation(null);
        updatedMatch.setLastStonePlayerBoard(null);
    }


}
