package nel.marco.mancala.controller.v1.validator;

import nel.marco.mancala.controller.v1.model.Command;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.service.MancalaService;
import nel.marco.mancala.service.Match;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MancalaEndpointValidator {

    private MancalaService mancalaService;

    public MancalaEndpointValidator(MancalaService mancalaService) {
        this.mancalaService = mancalaService;
    }

    public List<String> validateUpdatePlayerRequest(String username, String matchId, String uniquePlayerId) {

        List<String> errors = new ArrayList<>();

        if (matchId.trim().isBlank()) {
            errors.add("'matchId' can not be null or empty");
        }
        if (uniquePlayerId.trim().isBlank()) {
            errors.add("'uniquePlayerId' can not be null or empty");
        }
        if (username.trim().isBlank()) {
            errors.add("'username' can not be null or empty");
        }

        Optional<Match> optionalMatch = mancalaService.getMatch(matchId);

        if (!optionalMatch.isPresent()) {
            errors.add(String.format("Match does not exist [matchId=%s]", matchId));
        }

        Optional<PlayerModel> optionalPlayer = mancalaService.findPlayerInMatch(matchId, uniquePlayerId);

        if (!optionalPlayer.isPresent()) {
            errors.add(String.format("Could not find player [matchId=%s;uniquePlayerId=%s]", matchId, uniquePlayerId));
        }


        return errors;
    }

    public List<String> validateExecuteCommand(Command command, String matchId, String uniquePlayerId) {

        List<String> errors = new ArrayList<>();

        if (matchId.trim().isBlank()) {
            errors.add("'matchId' can not be null or empty");
        }
        if (uniquePlayerId.trim().isBlank()) {
            errors.add("'uniquePlayerId' can not be null or empty");
        }

        if (command == null) {
            errors.add("'command' can not be null or empty");
        } else {
            if (command.getPit() == null) {
                errors.add("'command.pit' can not be null or empty");
            }
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        Optional<Match> optionalMatch = mancalaService.getMatch(matchId);

        if (!optionalMatch.isPresent()) {
            errors.add(String.format("Match does not exist [matchId=%s]", matchId));
        }

        Optional<PlayerModel> optionalPlayer = mancalaService.findPlayerInMatch(matchId, uniquePlayerId);

        if (!optionalPlayer.isPresent()) {
            errors.add(String.format("Could not find player [matchId=%s;uniquePlayerId=%s]", matchId, uniquePlayerId));
        }


        return errors;
    }
}
