package file.exceptions;

/**
 * Created by Mikołaj Styś on 2015-09-01.
 */
public class FileNotExistPathException extends PathExistException {

    public FileNotExistPathException(String path) {
        super("File not exist: " + path);
    }
}
