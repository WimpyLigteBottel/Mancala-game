package nel.marco.mancala.service.exceptions;

public class MatchDoesNotExistException extends RuntimeException {
    public MatchDoesNotExistException(String message) {
        super(message);
    }
}
