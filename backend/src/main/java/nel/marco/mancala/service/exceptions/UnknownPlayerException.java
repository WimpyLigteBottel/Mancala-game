package nel.marco.mancala.service.exceptions;

public class UnknownPlayerException extends RuntimeException {
    public UnknownPlayerException(String s) {
        super(s);
    }
}
