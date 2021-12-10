package nel.marco.mancala.controller.v1.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerModel {

    private Map<PIT, Integer> pits = new HashMap<>();
    private String uniqueId;
    private String username;
    private long totalScore;

}
