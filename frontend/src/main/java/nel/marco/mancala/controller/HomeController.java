package nel.marco.mancala.controller;

import lombok.extern.slf4j.Slf4j;
import nel.marco.mancala.controller.v1.model.Match;
import nel.marco.mancala.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class HomeController {

    private final MatchService matchService;

    @Autowired
    public HomeController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/")
    public String landingPage(
            @RequestParam(value = "activePlayer", required = false, defaultValue = "") String activePlayer,
            @RequestParam(value = "uniqueMatchId", required = false) String uniqueMatchId,
            Model model) {

        if (uniqueMatchId == null) {
            return createNewGame(model);
        }

        try {
            Match matchStats = matchService.getMatchStats(uniqueMatchId);
            model.addAttribute("match", matchStats);
            model.addAttribute("activePlayer", activePlayer);

            if (matchStats.isGameOver()) {
                log.info("Displaying gameover screen [matchId={}]", matchStats.getUniqueMatchId());
                return "gameover";
            }

        } catch (Exception e) {
            log.error("getMatchStats failed [activePlayer={};uniqueMatchId={}]", activePlayer, uniqueMatchId);
            model.addAttribute("error", "Backend failed, try again when server is back up [matchId=" + uniqueMatchId + "]");
            return "error";
        }


        return "index";
    }

    @GetMapping("/createnewgame")
    public String createNewGame(Model model) {

        try {
            Match newGame = matchService.createNewGame();
            model.addAttribute("match", newGame);
            model.addAttribute("activePlayer", "");
            log.info("Creating a new game [matchId={}]", newGame.getUniqueMatchId());
            return "index";
        } catch (Exception e) {
            log.error("Creating a new game failed");
            model.addAttribute("error", "Could not create new game");
            return "error";
        }

    }

    @GetMapping("/executeCommand")
    public String executeCommand(@RequestParam(value = "activePlayer", required = false, defaultValue = "") String activePlayer,
                                 @RequestParam(value = "uniqueMatchId", required = false) String uniqueMatchId,
                                 @RequestParam(value = "command", required = false) String command,
                                 Model model) {

        try {
            matchService.executeCommand(uniqueMatchId, activePlayer, command);
        } catch (Exception e) {
            log.error("executeCommand failed[activePlayer={};uniqueMatchId={};command={}]", activePlayer, uniqueMatchId, command);
            String errorMessage = String.format("executeCommand failed [command=%s]", command);
            model.addAttribute("error", errorMessage);
        }

        return landingPage(activePlayer, uniqueMatchId, model);

    }
}
