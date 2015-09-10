package file.exceptions;

/**
 * Created by Mikołaj Styś on 2015-09-01.
 */
public class FileExistPathException extends PathExistException {

    public FileExistPathException(String path) {
        super("File already exist: " + path);
    }
}
