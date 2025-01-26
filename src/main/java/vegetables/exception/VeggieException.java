package vegetables.exception;

/**
 * Custom exception class for input validation.
 * This exception is used to handle errors specific to the application's logic.
 */
public class VeggieException extends Exception {
    /**
     * Constructs a new {@code VeggieException} with the specified detail message.
     * The message provides additional information about the exception cause.
     *
     * @param message the detail message describing the exception.
     */
    public VeggieException(String message) {
        super(message);
    }
}
