package com.example.ezvault.camera.utils;

import org.checkerframework.checker.regex.qual.Regex;

/**
 * Implements a VERY crude and arbitrary serial number matcher.
 */
public class CrudeMatcher implements SerialNumberMatcher {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keep(String possibility) {
        @Regex String regex = "[A-Z0-9]{6,20}";
        return possibility.matches(regex);
    }
}
