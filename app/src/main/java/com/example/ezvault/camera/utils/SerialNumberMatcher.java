package com.example.ezvault.camera.utils;

/**
 * Interface that represents serial number matchers
 */
public interface SerialNumberMatcher {
    /**
     * Check whether a string conforms to a serial number in some format.
     */
    boolean keep(String possibility);
}
