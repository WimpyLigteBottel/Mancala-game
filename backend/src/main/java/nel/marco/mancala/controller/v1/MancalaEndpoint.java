package nel.marco.mancala.controller.v1;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.Command;
import nel.marco.mancala.controller.v1.model.ErrorMessage;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.controller.v1.validator.MancalaEndpointValidator;
import nel.marco.mancala.service.MancalaService;
import nel.marco.mancala.service.exceptions.InvalidMoveException;
import nel.marco.mancala.service.exceptions.MatchIsOverException;
import nel.marco.mancala.service.exceptions.NotThatPlayerTurnException;
import nel.marco.mancala.service.exceptions.UnknownPlayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "http://localhost:9000") // This is here to indicate I allow crossorgin request on methods
public class MancalaEndpoint {

    private final MancalaService mancalaService;
    private final MancalaEndpointValidator mancalaEndpointValidator;

    @Autowired
    public MancalaEndpoint(MancalaService mancalaService, MancalaEndpointValidator mancalaEndpointValidator) {
        this.mancalaService = mancalaService;
        this.mancalaEndpointValidator = mancalaEndpointValidator;
    }

    @GetMapping("/newgame")
    public ResponseEntity<Match> createGame() {
        PlayerModel playerA = new PlayerModel();
        playerA.setUsername("playerA");
        playerA.setUniqueId(UUID.randomUUID().toString());

        PlayerModel playerB = new PlayerModel();
        playerB.setUsername("playerB");
        playerB.setUniqueId(UUID.randomUUID().toString());

        Match match = mancalaService.createMatch(playerA, playerB);

        log.info("newgame created [match={}]", match);
        return ResponseEntity.ok(match);
    }


    @GetMapping("/game/{matchId}")
    public ResponseEntity<?> getMatchStats(@PathVariable(name = "matchId", required = true, value = "") String matchId) {
        log.info("getMatchStats called [matchId={}]", matchId);

        if (matchId.trim().isBlank()) {
            return ResponseEntity.badRequest().body("'matchId' can not be null or empty");
        }
        Optional<Match> match = mancalaService.getMatch(matchId);

        if (match.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(match.get());
    }


    @PutMapping("/game/{matchId}/player/{uniquePlayerId}")
    public ResponseEntity<?> updatePlayerUsername(@RequestParam(name = "username", required = true, value = "") String username,
                                                  @PathVariable(name = "matchId", required = true, value = "") String matchId,
                                                  @PathVariable(name = "uniquePlayerId", required = true, value = "") String uniquePlayerId) {

        log.info("updatePlayerUsername called [matchId={};uniquePlayerId={};username={}]", matchId, uniquePlayerId, username);

        List<String> errors = mancalaEndpointValidator.validateUpdatePlayerRequest(username, matchId, uniquePlayerId);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Optional<PlayerModel> player = mancalaService.findPlayerInMatch(matchId, uniquePlayerId);

        player.ifPresent(playerModel -> {
            playerModel.setUsername(username);
        });

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/game/{matchId}/player/{uniquePlayerId}/command")
    public ResponseEntity<?> executeCommand(@RequestBody Command command,
                                            @PathVariable(name = "matchId", required = true, value = "") String matchId,
                                            @PathVariable(name = "uniquePlayerId", required = true, value = "") String uniquePlayerId) {

        log.info("executeCommand called [matchId={};uniquePlayerId={}]", matchId, uniquePlayerId);

        List<String> errors = mancalaEndpointValidator.validateExecuteCommand(command, matchId, uniquePlayerId);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            return ResponseEntity.ok(mancalaService.executeCommand(command, matchId, uniquePlayerId));
        } catch (Exception e) {
            ErrorMessage errorMessage = new ErrorMessage();

            if (e instanceof UnknownPlayerException) {
                errorMessage.addError("Unknown player");
            } else if (e instanceof NotThatPlayerTurnException) {
                errorMessage.addError("Not your turn yet");
            } else if (e instanceof MatchIsOverException) {
                errorMessage.addError("The match is over");
            } else if (e instanceof InvalidMoveException) {
                errorMessage.addError("The move is invalid, try again");
            }

            return ResponseEntity.internalServerError().body(errorMessage);
        }

    }


}
