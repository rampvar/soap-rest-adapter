package com.soaprestadapter.utility;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Cobol field.
 */
@Getter
@Setter
public class CobolField {
    /**
     * name of the field
     */
    private String name;

    /**
     * start position in the record
     */
    private int start;

    /**
     * length of the field
     */
    private int length;

    /**
     * field type (X for alphanumeric, 9 for numeric)
     */
    private String type; // "X" for alphanumeric, "9" for numeric

    /**
     * Constructor.
     *
     * @param nameParam of the field
     * @param startParam start position in the record
     * @param lengthParam length of the field
     * @param typeParam field type (X for alphanumeric, 9 for numeric)
     */
    public CobolField(final String nameParam, final int startParam, final int lengthParam, final String typeParam) {
        this.name = nameParam;
        this.start = startParam;
        this.length = lengthParam;
        this.type = typeParam;
    }
}
