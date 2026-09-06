package io.github.sekelenao.flinkboot.core.internal.parser.bool;

import io.github.sekelenao.flinkboot.core.api.exception.parsing.BooleanParsingException;

public final class StrictBooleanParser {

    private StrictBooleanParser() {
        throw new AssertionError("You cannot instantiate this class");
    }

    public static boolean parse(String value) {
        if("true".equalsIgnoreCase(value)) {
            return true;
        }
        if("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new BooleanParsingException("Invalid boolean value: " + value);
    }

}
