package com.example.ezvault.authentication.authentication;

import com.example.ezvault.model.User;
import com.google.android.gms.tasks.Task;

public interface IAuthenticationStrategy {
    Task<User> authenticate();
}
