package matej.exceptions;

/**
 * Thrown if a user tries to make a move that is illegal by the rules of the game.
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-20
 */
public class IllegalMoveException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs an <code>IllegalMoveException</code> with the specified message.
	 *
	 * @param msg the detail message.
	 */
	public IllegalMoveException (String msg) {
        super("Illegal move: " + msg);
    }
}