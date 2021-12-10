package nel.marco.mancala.controller.v1.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Command implements Serializable {
    @Serial
    private static final long serialVersionUID = -8654191188134166304L;
    private PIT pit;
}
