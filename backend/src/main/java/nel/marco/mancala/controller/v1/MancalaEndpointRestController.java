package nel.marco.mancala.controller.v1;

import nel.marco.mancala.controller.v1.model.Command;
import nel.marco.mancala.controller.v1.model.ErrorMessage;
import nel.marco.mancala.controller.v1.model.PlayerModel;
import nel.marco.mancala.controller.v1.validator.MancalaEndpointValidator;
import nel.marco.mancala.service.MancalaService;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.service.exceptions.NotThatPlayerTurnException;
import nel.marco.mancala.service.exceptions.UnknownPlayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class MancalaEndpointRestController {


    private final MancalaService mancalaService;
    private final MancalaEndpointValidator mancalaEndpointValidator;

    @Autowired
    public MancalaEndpointRestController(MancalaService mancalaService, MancalaEndpointValidator mancalaEndpointValidator) {
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

        return ResponseEntity.ok(mancalaService.createMatch(playerA, playerB));
    }


    @GetMapping("/game/{matchId}")
    public ResponseEntity<?> getMatchStats(@PathVariable(name = "matchId", required = true, value = "") String matchId) {

        if (matchId.trim().isBlank()) {
            return ResponseEntity.badRequest().body("'matchId' can not be null or empty");
        }
        Optional<Match> match = mancalaService.getMatch(matchId);

        if (!match.isPresent())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(match.get());
    }


    @PutMapping("/game/{matchId}/player/{uniquePlayerId}")
    public ResponseEntity<?> updatePlayerStats(@RequestParam(name = "username", required = true, value = "") String username,
                                               @PathVariable(name = "matchId", required = true, value = "") String matchId,
                                               @PathVariable(name = "uniquePlayerId", required = true, value = "") String uniquePlayerId) {


        List<String> errors = mancalaEndpointValidator.validateUpdatePlayerRequest(username, matchId, uniquePlayerId);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Optional<PlayerModel> player = mancalaService.findPlayerInMatch(matchId, uniquePlayerId);

        player.ifPresent(playerModel -> {
            playerModel.setUsername(username);
        });


        return ResponseEntity.ok(String.format("Player has been updated [uniqueId=%s]", uniquePlayerId));
    }

    @PostMapping("/game/{matchId}/player/{uniquePlayerId}/command")
    public ResponseEntity<?> executeCommand(@Validated @RequestBody Command command,
                                            @PathVariable(name = "matchId", required = true, value = "") String matchId,
                                            @PathVariable(name = "uniquePlayerId", required = true, value = "") String uniquePlayerId) {

        List<String> errors = mancalaEndpointValidator.validateExecuteCommand(command, matchId, uniquePlayerId);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            String updatedMatchId = mancalaService.executeCommand(command, matchId, uniquePlayerId);

            return ResponseEntity.ok(updatedMatchId);
        } catch (NotThatPlayerTurnException e) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.addError("Not your turn yet");
            return ResponseEntity.badRequest().body(errorMessage);
        } catch (UnknownPlayerException e) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.addError("Unknown player");
            return ResponseEntity.badRequest().body(errorMessage);
        }

    }


}
