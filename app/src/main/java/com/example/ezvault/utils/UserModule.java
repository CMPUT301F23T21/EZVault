package com.example.ezvault.utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class UserModule {
    @Provides
    @Singleton
    UserManager provideUserManager() {
        return new UserManager();
    }
}
