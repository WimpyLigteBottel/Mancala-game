package nel.marco.mancala.service;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.Command;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.service.exceptions.*;
import nel.marco.mancala.service.stones.MoveLogicService;
import nel.marco.mancala.service.trigger.SpecialTriggerLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles the core business logic to handle command requests from the user (aka player)
 */
@Slf4j
@Service
public class MancalaService {

    private final MoveLogicService moveLogicService;
    private final SpecialTriggerLogicService specialTriggerLogicService;


    @Autowired // This is technically not needed, but I like to indicate it like this
    public MancalaService(MoveLogicService moveLogicService, SpecialTriggerLogicService specialTriggerLogicService) {
        this.moveLogicService = moveLogicService;
        this.specialTriggerLogicService = specialTriggerLogicService;
    }

    //Note: I decided with internal memory map for now but going to change it in the future to database requests
    private final Map<String, Match> internalMemoryMap = new ConcurrentHashMap<>();


    /**
     * Find current active match
     *
     * @param uniqueMatch The match unique id
     * @return {@link Optional<Match>} Optional of searched for Match
     */
    public Optional<Match> getMatch(String uniqueMatch) {
        return Optional.ofNullable(internalMemoryMap.get(uniqueMatch));
    }

    /**
     * Tries to find the player in the match
     *
     * @param matchId        The match unique id
     * @param uniquePlayerId The player id that you are trying to find in specific match
     * @return {@link Optional<PlayerModel>} Optional of the playerModel
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
     * @param playerB The player B (second player)
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
        match.setPlayerATurn(true);

        internalMemoryMap.put(match.getUniqueMatchId(), match);

        return match;
    }

    /**
     * 1. Finds the match based on the command's match ID
     * 2. Do basic validation on input (does match exist + command valid + correct player turn)
     * 3. Execute the logic to move the stones accordingly
     * 4. Update the match stats
     *
     * @param command        The command form the player
     * @param matchId        The match unique id
     * @param uniquePlayerId The uniquePlayer id that's playing
     * @return {@link String} returns the match unique id
     */
    public String executeCommand(Command command, String matchId, String uniquePlayerId) {

        Match match = internalMemoryMap.get(matchId);

        validate(command, uniquePlayerId, match);

        Match updatedMatch;
        updatedMatch = moveLogicService.movingStones(command.getPit(), match);
        updatedMatch = specialTriggerLogicService.hasSpecialLogicTriggered(updatedMatch);
        updateMatch(updatedMatch);

        return match.getUniqueMatchId();
    }

    /**
     * Does basic checks to see if the command is valid and if it can be executed against current match.
     * <p>
     * If invalid command is found then throw invalid exception
     *
     * @param command        The player command to move stones
     * @param uniquePlayerId The unique player id that making the move
     * @param match          The match details
     */
    private void validate(Command command, String uniquePlayerId, Match match) {
        if (match == null) {
            throw new MatchDoesNotExistException("This match does not exist");
        }

        if (match.isGameOver()) {
            throw new MatchIsOverException("This match is done");
        }

        PlayerModel playerModelA = match.getPlayerModelA();
        PlayerModel playerModelB = match.getPlayerModelB();

        boolean isPlayerA = uniquePlayerId.equals(playerModelA.getUniqueId());
        boolean isPlayerB = uniquePlayerId.equals(playerModelB.getUniqueId());

        if (!isPlayerA && !isPlayerB) {
            throw new UnknownPlayerException("Unknown player ->" + uniquePlayerId);
        }

        //NOTE: the isPlayerA is like safetyCheck to make sure its not someone else trying to cheat ;)
        boolean isPlayerATurn = isPlayerA && match.isPlayerATurn();
        boolean isPlayerBTurn = isPlayerB && !match.isPlayerATurn();

        if ((isPlayerA && !isPlayerATurn || isPlayerB && !isPlayerBTurn)) {
            log.error("INVALID PLAYER COMMAND!!! [playerId={};command={}]", uniquePlayerId, command.getPit());
            throw new NotThatPlayerTurnException("Not that player turn yet");
        }

        if (command.getPit() == PIT.PLAYER_1_BOARD || command.getPit() == PIT.PLAYER_2_BOARD) {
            throw new InvalidMoveException("Can't move stones from your scoreboard");
        }
    }

    /**
     * Wipes clean certain state from the match object and updating internal map state
     *
     * @param match
     */
    private void updateMatch(Match match) {
        match.setLastStoneLocation(null);
        match.setLastStonePlayerBoard(null);
        match.setStealable(false);
        internalMemoryMap.put(match.getUniqueMatchId(), match);
    }


}
