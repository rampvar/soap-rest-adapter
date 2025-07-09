package com.soaprestadapter.utility;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Cobol field with header.
 */
@Getter
@Setter
public class CobolHeaderField {
    /**
     * name of the field
     */
    private String name;
    /**
     * type of the field
     */
    private String type;
    /**
     * length of the field
     */
    private int length;

    /**
     * Constructor for CobolHeaderField.
     *
     * @param nameParam name of the field
     * @param typeParam type of the field
     * @param lengthParam length of the field
     **/
    public CobolHeaderField(final String nameParam, final String typeParam, final int lengthParam) {
        this.name = nameParam;
        this.type = typeParam;
        this.length = lengthParam;
    }
}
