package nel.marco.mancala.service;

public class MatchDoesNotExistException extends RuntimeException {
    public MatchDoesNotExistException(String message) {
        super(message);
    }
}
