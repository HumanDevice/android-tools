package com.mwstys.graphtask.repositories.file.exceptions;

/**
 * Created by Mikołaj Styś on 2015-09-01.
 */
public class GeneralPathException extends RuntimeException {

    public GeneralPathException(String message) {
        super(message);
    }

    public GeneralPathException(Throwable exception) {
        super(exception);
    }

    public GeneralPathException(String message, Throwable exception) {
        super(message, exception);
    }

}
