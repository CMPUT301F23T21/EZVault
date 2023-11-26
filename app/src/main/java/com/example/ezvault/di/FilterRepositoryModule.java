package com.example.ezvault.di;

import com.example.ezvault.data.FilterRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.scopes.ActivityRetainedScoped;

@Module
@InstallIn(ActivityRetainedComponent.class)
public class FilterRepositoryModule {
    @Provides
    @ActivityRetainedScoped
    public FilterRepository provide() {
        return new FilterRepository();
    }
}
