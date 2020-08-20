package matej.exceptions;

/**
 * Thrown if a user tries to make a access a form that does not belong to him.
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-20
 */
public class InvalidOwnershipException extends Exception {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>InvalidOwnershipException</code> with the specified message.
	 *
	 * @param msg the detail message.
	 */
	public InvalidOwnershipException (String errorMessage) {
        super("Invalid ownership: " + errorMessage);
    }
}