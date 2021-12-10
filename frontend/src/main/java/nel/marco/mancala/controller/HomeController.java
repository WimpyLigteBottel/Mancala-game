package nel.marco.mancala.controller;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.model.Match;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/index")
    public String greeting(Model model) {

        if (model.getAttribute("match") == null) {
            ResponseEntity<Match> forEntity = new RestTemplate().getForEntity("http://localhost:8080/v1/newgame", Match.class);
            Match match = forEntity.getBody();
            model.addAttribute("match", match);
        }
        model.addAttribute("activePlayer", "");
        log.info("index");
        return "index";
    }

    @GetMapping("/createnewgame")
    public String createNewGame(Model model) {

        ResponseEntity<Match> forEntity = new RestTemplate().getForEntity("http://localhost:8080/v1/newgame", Match.class);
        Match body = forEntity.getBody();
        model.addAttribute("match", body);

        log.info("createnewgame");
        return "index";
    }

    @GetMapping("/getMatch")
    public String getMatchStats(
            @RequestParam(value = "activePlayer", required = false) String activePlayer,
            @RequestParam(value = "uniqueMatchId", required = false) String uniqueMatchId, Model model) {

        ResponseEntity<Match> forEntity = new RestTemplate().getForEntity("http://localhost:8080/v1/game/" + uniqueMatchId, Match.class);
        Match updatedMatch = forEntity.getBody();

        model.addAttribute("match", updatedMatch);
        model.addAttribute("activePlayer", activePlayer);

        if (updatedMatch.isGameOver()) {
            log.info("getMatch");
            return "gameover";
        }



        log.info("getMatch");
        return "index";
    }
}
