package nel.marco.mancala.controller.model;

import lombok.Data;

@Data
public class PlayerModel {

    private int[] pits = new int[6];
    private long id;
    private String uniqueId;
    private String username;
    private long totalScore;

    public PlayerModel() {
    }

    public PlayerModel(int[] pits, long id, String username) {
        this.pits = pits;
        this.id = id;
        this.username = username;
    }

}
