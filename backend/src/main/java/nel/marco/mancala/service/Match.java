package nel.marco.mancala.service;

import lombok.Data;
import nel.marco.mancala.controller.model.PlayerModel;

@Data
public class Match {

    private String uniqueMatchId;
    private PlayerModel playerModelA;
    private PlayerModel playerModelB;

    private boolean isPlayerATurn;
}
