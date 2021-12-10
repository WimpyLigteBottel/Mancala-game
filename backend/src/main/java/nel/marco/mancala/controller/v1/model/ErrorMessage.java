package nel.marco.mancala.controller.v1.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorMessage {

    private List<String> errors;

    public void addError(String message) {

        if (errors == null)
            errors = new ArrayList<>();

        errors.add(message);
    }
}
