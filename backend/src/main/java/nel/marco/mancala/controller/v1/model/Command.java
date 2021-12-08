package nel.marco.mancala.controller.v1.model;

import lombok.Data;

@Data
public class Command {

    private String matchID;
    private String playerUniqueId;
    private PIT pit;
}
