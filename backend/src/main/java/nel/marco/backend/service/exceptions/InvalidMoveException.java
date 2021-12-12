package nel.marco.backend.service.exceptions;

public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String msg) {
        super(msg);
    }
}
