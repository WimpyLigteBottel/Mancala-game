package nel.marco.mancala.controller;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.model.Match;
import nel.marco.mancala.model.PIT;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/index")
    public String greeting(Model model) {


        if (model.getAttribute("match") == null) {
            ResponseEntity<Match> forEntity = new RestTemplate().getForEntity("http://localhost:8080/v1/newgame", Match.class);
            Match body = forEntity.getBody();
            Map<PIT, Integer> pits = body.getPlayerModelA().getPits();
            pits.put(PIT.FIRST, 3);
            pits.put(PIT.SIX, 999);
            body.getPlayerModelA().setPits(pits);
            model.addAttribute("match", body);
        }

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
    public String getMatchStats(@RequestParam(value = "matchId", required = false) String matchId, @ModelAttribute Match match, Model model) {

        ResponseEntity<Match> forEntity = new RestTemplate().getForEntity("http://localhost:8080/v1/game/" + match.getUniqueMatchId(), Match.class);

        model.addAttribute("match", forEntity.getBody());


        log.info("createnewgame");
        return "index";
    }
}
