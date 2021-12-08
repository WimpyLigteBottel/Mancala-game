package nel.marco.mancala.controller.v1.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerModel {

    private Map<PIT, Integer> pits = new HashMap<>();
    private long id;
    private String uniqueId;
    private String username;
    private long totalScore;

    public PlayerModel() {
    }

    public PlayerModel(Map<PIT, Integer> pits, long id, String username) {
        this.pits = pits;
        this.id = id;
        this.username = username;
    }

}
