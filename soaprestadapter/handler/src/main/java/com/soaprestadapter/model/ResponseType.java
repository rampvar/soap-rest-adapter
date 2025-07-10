package com.soaprestadapter.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum for different response types.
 */
@Getter
@RequiredArgsConstructor
public enum ResponseType {

    /**
     * amt response type.
     */
    AMT_RESPONSE("AMT-RESPONSE"),
    /**
     * blueage response type.
     */
    BLUEAGE_RESPONSE("BLUEAGE-RESPONSE"),
    /**
     * custom response type.
     */
    CUSTOM_RESPONSE("CUSTOM-RESPONSE");

    /**
     * response type.
     */
    private final String type;

    /**
     * Converts a response type string to a ResponseType enum.
     * @param type response type string
     * @return ResponseType enum
     **/
    public static ResponseType fromString(final String type) {
        for (ResponseType rt : values()) {
            if (rt.getType().equalsIgnoreCase(type)) {
                return rt;
            }
        }
        return null;
    }
}
