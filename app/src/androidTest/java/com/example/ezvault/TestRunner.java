package com.example.ezvault;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import dagger.hilt.android.testing.HiltTestApplication;

public class TestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return super.newApplication(cl, HiltTestApplication.class.getName(), context);
    }
}
