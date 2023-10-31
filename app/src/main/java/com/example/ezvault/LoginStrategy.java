package com.example.ezvault;

import com.google.android.gms.tasks.Task;

/**
 * Interface that different login strategies must implement
 */
public interface LoginStrategy {
    /**
     * Authenticate a user
     * @return Return a Task with a User object, that represents the user that logged in.
     */
    Task<User> authenticateUser();
}
