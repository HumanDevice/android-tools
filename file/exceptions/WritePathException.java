package file.exceptions;

/**
 * Created by Mikołaj Styś on 2015-09-01.
 */
public class WritePathException extends GeneralPathException {

    public WritePathException(String message) {
        super(message);
    }

    public WritePathException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
