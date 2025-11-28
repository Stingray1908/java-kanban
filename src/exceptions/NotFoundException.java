package exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("NotFoundByIdException");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
