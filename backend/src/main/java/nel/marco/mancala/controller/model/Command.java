package nel.marco.mancala.controller.model;

import lombok.Data;

@Data
public class Command {

    private String matchID;
    private String playerUniqueId;
    private PIT pit;
}
