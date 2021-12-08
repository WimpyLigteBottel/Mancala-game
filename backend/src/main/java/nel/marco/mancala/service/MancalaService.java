package nel.marco.mancala.service;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.Command;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.service.exceptions.MatchDoesNotExistException;
import nel.marco.mancala.service.exceptions.NotThatPlayerTurnException;
import nel.marco.mancala.service.exceptions.UnknownPlayerException;
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

    private Map<String, Match> internalMemoryMap = new ConcurrentHashMap<>();


    /**
     * Find current active matches
     *
     * @param uniqueMatch
     * @return
     */
    public Optional<Match> getMatch(String uniqueMatch) {
        return Optional.ofNullable(internalMemoryMap.get(uniqueMatch));
    }

    /**
     * Tries to find the player in the match
     *
     * @param matchId
     * @param uniquePlayerId
     * @return
     */
    public Optional<PlayerModel> findPlayerInMatch(String matchId, String uniquePlayerId) {


        Optional<Match> match = getMatch(matchId);

        if (match.isEmpty()) {
            return Optional.empty();
        }

        PlayerModel playerA = match.get().getPlayerModelA();
        PlayerModel playerB = match.get().getPlayerModelB();

        if (playerA.getUniqueId().equals(uniquePlayerId)) {
            return Optional.of(playerA);
        } else if (playerB.getUniqueId().equals(uniquePlayerId)) {
            return Optional.of(playerB);
        }

        return Optional.empty();
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
            initialBoardState.put(PIT.valueOf(i), 4);
        }

        playerA.setPits(new HashMap<>(initialBoardState));
        playerB.setPits(new HashMap<>(initialBoardState));

        Match match = new Match();
        match.setPlayerModelA(playerA);
        match.setPlayerModelB(playerB);
        match.setUniqueMatchId(UUID.randomUUID().toString());
        match.setPlayerATurn(true);

        internalMemoryMap.put(match.getUniqueMatchId(), match);

        return match;
    }

    /**
     * 1. Finds the match based on the command's match ID
     * 2. Find out who's turn it is and make sure that its the correct players command
     * 3. Execute the logic to move the stones accordingly
     * 4. Update the match stats
     *
     * @param command        The command form the player
     * @param matchId
     * @param uniquePlayerId
     * @return
     */
    public String executeCommand(Command command, String matchId, String uniquePlayerId) {

        Match match = internalMemoryMap.get(matchId);

        if (match == null) {
            throw new MatchDoesNotExistException("This match does not exist");//TODO:Replace with proper Exception to indicate what the issue was
        }

        PlayerModel playerModelA = match.getPlayerModelA();
        PlayerModel playerModelB = match.getPlayerModelB();

        boolean isPlayerA = uniquePlayerId.equals(playerModelA.getUniqueId());
        boolean isPlayerB = uniquePlayerId.equals(playerModelB.getUniqueId());

        if (!isPlayerA && !isPlayerB) {
            throw new UnknownPlayerException("Unknown player ->" + uniquePlayerId); //TODO: make this more clear and add the player
        }

        //NOTE: the isPlayerA is like safetyCheck to make sure its not someone else trying to cheat ;)
        boolean isPlayerATurn = isPlayerA && match.isPlayerATurn();
        boolean isPlayerBTurn = isPlayerB && !match.isPlayerATurn();

        if ((isPlayerA && !isPlayerATurn || isPlayerB && !isPlayerBTurn)) {
            log.error("INVALID PLAYER COMMAND!!! [playerId={};command={}]", uniquePlayerId, command.getPit()); // TODO: handle this more nicely*
            throw new NotThatPlayerTurnException("Not that player turn yet");
        }

        Match updatedMatch;
        if (isPlayerATurn) {
            updatedMatch = moveLogicService.movingStones(true, command.getPit(), match);
            updatedMatch.setPlayerATurn(false);
            updatedMatch = specialTriggerLogicService.hasSpecialLogicTriggered(updatedMatch);
            updateMatch(updatedMatch);
        } else {
            updatedMatch = moveLogicService.movingStones(false, command.getPit(), match);
            updatedMatch.setPlayerATurn(true);
            updatedMatch = specialTriggerLogicService.hasSpecialLogicTriggered(updatedMatch);
            updateMatch(updatedMatch);
        }

        return match.getUniqueMatchId();
    }


    private void updateMatch(Match updatedMatch) {
        updatedMatch.setLastStoneLocation(null);
        updatedMatch.setLastStonePlayerBoard(null);
        internalMemoryMap.put(updatedMatch.getUniqueMatchId(), updatedMatch);
    }


}
