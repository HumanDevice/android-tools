package file.exceptions;

/**
 * Created by Mikołaj Styś on 2015-09-01.
 */
public class NotDirectoryPathException extends PathExistException {

    public NotDirectoryPathException(String path) {
        super("Not a directory: " + path);
    }
}
