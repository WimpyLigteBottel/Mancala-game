package nel.marco.mancala.controller.v1.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorMessage {

    List<String> errors;

    public void addError(String message) {

        if (errors == null)
            errors = new ArrayList<>();

        errors.add(message);
    }
}
