package org.osbot.maestro.script.util.directory.exceptions;

public class InvalidDirectoryNameException extends InvalidNameException {

    public InvalidDirectoryNameException(String name) {
        super(name + " is not a valid directory name.");
    }

}