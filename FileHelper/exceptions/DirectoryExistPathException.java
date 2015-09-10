package com.mwstys.graphtask.repositories.file.exceptions;

/**
 * Created by Mikołaj Styś on 2015-09-01.
 */
public class DirectoryExistPathException extends PathExistException {

    public DirectoryExistPathException(String path) {
        super("Directory already exist: " + path);
    }
}
