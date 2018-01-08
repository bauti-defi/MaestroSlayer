package org.osbot.maestro.script.util.directory.exceptions;

public class InvalidFileNameException extends InvalidNameException {

    public InvalidFileNameException(String name) {
        super(name + " is not a valid file name.");
    }

}