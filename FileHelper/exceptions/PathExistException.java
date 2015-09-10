package com.mwstys.graphtask.repositories.file.exceptions;

/**
 * Created by Mikołaj Styś on 2015-09-01.
 */
public class PathExistException extends GeneralPathException {

    public PathExistException(String message) {
        super(message);
    }

    public PathExistException(Throwable exception) {
        super(exception);
    }

    public PathExistException(String message, Throwable exception) {
        super(message, exception);
    }
}
