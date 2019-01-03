package com.jukusoft.mmo.gs.region.subsystem;

/**
 * Throw when a required component wasn't found.
 * <p>
 * 
 * @see InjectSubSystem The respective annotation.
 */
public class RequiredSubSystemNotFoundException extends RuntimeException {

    public RequiredSubSystemNotFoundException(String message) {
        super(message);
    }

}
