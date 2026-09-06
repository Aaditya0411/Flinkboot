package io.github.sekelenao.flinkboot.core.internal.parser.bool;

import io.github.sekelenao.flinkboot.core.api.exception.parsing.BooleanParsingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("StrictBooleanParser")
class StrictBooleanParserTest {

    @Nested
    @DisplayName("Parse")
    class Parse {

        @ParameterizedTest
        @ValueSource(strings = {"true", "TRUE", "TrUe", "tRuE"})
        @DisplayName("Should return true for valid true representations case-insensitively")
        void shouldReturnTrueForValidTrueRepresentations(String input) {
            assertTrue(StrictBooleanParser.parse(input));
        }

        @ParameterizedTest
        @ValueSource(strings = {"false", "FALSE", "FaLsE", "fAlSe"})
        @DisplayName("Should return false for valid false representations case-insensitively")
        void shouldReturnFalseForValidFalseRepresentations(String input) {
            assertFalse(StrictBooleanParser.parse(input));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "yes", "no", "1", "0", " true", "false ", "  true  ", "true1", "untrue"})
        @DisplayName("Should throw BooleanParsingException for invalid boolean values")
        void shouldThrowExceptionForInvalidBooleans(String input) {
            var exception = assertThrows(BooleanParsingException.class, () -> StrictBooleanParser.parse(input));
            assertEquals("Invalid boolean value: " + input, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("Should throw AssertionError when trying to instantiate the class via reflection")
        void shouldPreventInstantiation() throws Exception {
            var constructor = StrictBooleanParser.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            var exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
            assertInstanceOf(AssertionError.class, exception.getCause());
        }
    }
}
