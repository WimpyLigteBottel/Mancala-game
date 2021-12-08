package nel.marco.mancala.service;

import lombok.Data;
import nel.marco.mancala.controller.v1.model.PIT;
import nel.marco.mancala.controller.v1.model.Player;
import nel.marco.mancala.controller.v1.model.PlayerModel;

@Data
public class Match {

    private String uniqueMatchId;
    private PlayerModel playerModelA;
    private PlayerModel playerModelB;

    private boolean isPlayerATurn;

    private PIT lastStoneLocation;
    private Player lastStonePlayerBoard;


}
