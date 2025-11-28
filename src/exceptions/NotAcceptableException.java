package exceptions;

public class NotAcceptableException extends RuntimeException {

    public NotAcceptableException() {
        super("NotAcceptableException");
    }

    public NotAcceptableException(String name) {
        super(name);
    }

}
