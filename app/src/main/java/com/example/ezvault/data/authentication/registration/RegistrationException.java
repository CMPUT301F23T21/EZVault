package com.example.ezvault.data.authentication.registration;

/**
 * An exception in the registration process.
 */
public class RegistrationException extends Exception {

    /**
     * Construct a RegistrationException with a given message
     * @param message The message of the exception
     */
    protected RegistrationException(String message) {
        super(message);
    }

    /**
     * An exception representing the error that a user already exists.
     */
    public static class UserAlreadyExists extends RegistrationException {
        /**
         * The user already exists.
         * @param userName The name of the user that already exists.
         */
        public UserAlreadyExists(String userName) {
            super("User " + "\"" + userName +"\" already exists.");
        }
    }
}
