package nel.marco.mancala.service;

import lombok.Data;
import nel.marco.mancala.controller.model.PIT;

@Data
public class Command {

    private String matchID;
    private String playerUniqueId;
    private PIT pit;
}
