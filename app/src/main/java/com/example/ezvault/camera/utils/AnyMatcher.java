package com.example.ezvault.camera.utils;

/**
 * A composite serial number matcher that checks if any matcher matches.
 */
public class AnyMatcher implements SerialNumberMatcher {
    private SerialNumberMatcher[] matchers;

    /**
     * Construct an AnyMatcher
     * @param matchers The matchers to use.
     */
    public AnyMatcher(SerialNumberMatcher... matchers) {
        this.matchers = matchers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keep(String possibility) {
        for (SerialNumberMatcher matcher : matchers) {
            if (matcher.keep(possibility)) {
                return true;
            }
        }
        return false;
    }
}
