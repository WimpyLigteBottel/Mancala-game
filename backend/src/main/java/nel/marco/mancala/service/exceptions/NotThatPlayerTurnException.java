package nel.marco.mancala.service.exceptions;

public class NotThatPlayerTurnException extends RuntimeException {
    public NotThatPlayerTurnException(String invalid_player_command) {
        super(invalid_player_command);
    }
}
