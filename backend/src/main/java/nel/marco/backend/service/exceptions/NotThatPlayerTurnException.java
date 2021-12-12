package nel.marco.backend.service.exceptions;

public class NotThatPlayerTurnException extends RuntimeException {
    public NotThatPlayerTurnException(String invalid_player_command) {
        super(invalid_player_command);
    }
}
