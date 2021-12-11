package nel.marco.mancala.service;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.Command;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.controller.v1.model.PIT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MatchService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public MatchService(@Value("${backend.base.url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    public Match createNewGame() {
        String url = String.format("%s/newgame", baseUrl);

        try {
            ResponseEntity<Match> matchResponseEntity = restTemplate.getForEntity(url, Match.class);

            if (matchResponseEntity.getStatusCode().is2xxSuccessful()) {
                return matchResponseEntity.getBody();
            }
        } catch (Exception e) {
            log.error("createNewGame failed [errorMessage={}]", e.getMessage(), e);
        }

        throw new RuntimeException("Could not create new game");
    }

    public Match getMatchStats(String uniqueMatchId) {

        String url = String.format("%s/game/%s", baseUrl, uniqueMatchId);

        try {
            ResponseEntity<Match> matchResponseEntity = restTemplate.getForEntity(url, Match.class);

            if (matchResponseEntity.getStatusCode().is2xxSuccessful()) {
                return matchResponseEntity.getBody();
            }
        } catch (Exception e) {
            log.error("getMatchStats failed [errorMessage={}]", e.getMessage(), e);
        }

        throw new RuntimeException("Failed to get match stats");
    }

    public String executeCommand(String matchId, String playerUniqueId, String command) {

        String url = String.format("%s/game/%s/player/%s/command", baseUrl, matchId, playerUniqueId);

        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, convertStringToCommandEnum(command), String.class);

        return stringResponseEntity.getBody();
    }

    /**
     * Converts the command from text to enum
     * @param text
     * @return
     */
    private Command convertStringToCommandEnum(String text) {
        Command command = new Command();
        switch (text) {
            case "FIRST":
                command.setPit(PIT.FIRST);
                break;
            case "SECOND":
                command.setPit(PIT.SECOND);
                break;
            case "THIRD":
                command.setPit(PIT.THIRD);
                break;
            case "FOURTH":
                command.setPit(PIT.FOURTH);
                break;
            case "FIFTH":
                command.setPit(PIT.FIFTH);
                break;
            case "SIX":
                command.setPit(PIT.SIX);
                break;
            default:
                throw new RuntimeException("Invalid command [text=" + text + "]");
        }

        return command;
    }
}
