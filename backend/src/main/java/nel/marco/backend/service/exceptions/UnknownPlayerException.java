package nel.marco.backend.service.exceptions;

public class UnknownPlayerException extends RuntimeException {
    public UnknownPlayerException(String s) {
        super(s);
    }
}
