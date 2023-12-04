package com.example.ezvault.data.authentication.registration;

import com.example.ezvault.model.User;
import com.google.android.gms.tasks.Task;

public interface IRegistrationStrategy {
    Task<User> register(String userName);
}
