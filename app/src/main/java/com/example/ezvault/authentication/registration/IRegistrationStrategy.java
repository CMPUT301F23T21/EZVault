package com.example.ezvault.authentication.registration;

import com.example.ezvault.model.User;
import com.google.android.gms.tasks.Task;

public interface IRegistrationStrategy {
    Task<User> register(String userName) throws RegistrationException;
}
