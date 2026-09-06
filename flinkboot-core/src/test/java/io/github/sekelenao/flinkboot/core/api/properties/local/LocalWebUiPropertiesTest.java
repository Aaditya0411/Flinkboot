package io.github.sekelenao.flinkboot.core.api.properties.local;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LocalWebUiProperties Tests")
class LocalWebUiPropertiesTest {

    private static final YAMLMapper mapper = YAMLMapper.builder()
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .findAndAddModules()
        .build();

    private static final Validator validator;

    static {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("Deserialization Tests")
    class DeserializationTests {

        @Test
        @DisplayName("Should deserialize valid YAML with all fields")
        void shouldDeserializeValidYamlWithAllFields() throws Exception {
            var yaml = "enabled: true\n" +
                "port: 8081\n" +
                "bind-address: localhost\n";

            var config = mapper.readValue(yaml, LocalWebUiProperties.class);

            assertAll(
                () -> assertNotNull(config),
                () -> assertTrue(config.enabled().isPresent()),
                () -> assertTrue(config.enabled().get()),
                () -> assertTrue(config.port().isPresent()),
                () -> assertEquals(8081, config.port().getAsInt()),
                () -> assertTrue(config.bindAddress().isPresent()),
                () -> assertEquals("localhost", config.bindAddress().get())
            );
        }

        @Test
        @DisplayName("Should deserialize valid empty YAML")
        void shouldDeserializeValidEmptyYaml() throws Exception {
            var yaml = "{}\n";

            var config = mapper.readValue(yaml, LocalWebUiProperties.class);

            assertAll(
                () -> assertNotNull(config),
                () -> assertTrue(config.enabled().isEmpty()),
                () -> assertTrue(config.port().isEmpty()),
                () -> assertTrue(config.bindAddress().isEmpty())
            );
        }

        @Test
        @DisplayName("Should fail validation when enabled is null")
        void shouldFailValidationWhenEnabledIsNull() {
            var config = new LocalWebUiProperties(null, 8081, "localhost");

            Set<ConstraintViolation<LocalWebUiProperties>> violations = validator.validate(config);

            assertFalse(violations.isEmpty(), "Should have validation violation for null enabled");
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("enabled")),
                "Violation should be on the enabled field");
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @ParameterizedTest
        @ValueSource(ints = {0, 8081, 65535})
        @DisplayName("Should pass validation when port is within valid range")
        void shouldPassValidationWhenPortIsWithinValidRange(int port) {
            var config = new LocalWebUiProperties(true, port, "127.0.0.1");

            Set<ConstraintViolation<LocalWebUiProperties>> violations = validator.validate(config);

            assertTrue(violations.isEmpty(), "Should have no violations");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 65536})
        @DisplayName("Should fail validation when port is outside valid range")
        void shouldFailValidationWhenPortIsOutsideValidRange(int port) {
            var config = new LocalWebUiProperties(true, port, "localhost");

            Set<ConstraintViolation<LocalWebUiProperties>> violations = validator.validate(config);

            assertFalse(violations.isEmpty(), "Should have validation violations for invalid port");
        }
    }

    @Nested
    @DisplayName("Getters Tests")
    class GettersTests {

        @Test
        @DisplayName("Should return expected values from getters")
        void testGetters() {
            var config = new LocalWebUiProperties(false, 9090, "0.0.0.0");

            assertAll(
                () -> assertTrue(config.enabled().isPresent()),
                () -> assertFalse(config.enabled().get()),
                () -> assertTrue(config.port().isPresent()),
                () -> assertEquals(9090, config.port().getAsInt()),
                () -> assertTrue(config.bindAddress().isPresent()),
                () -> assertEquals("0.0.0.0", config.bindAddress().get())
            );
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Should satisfy equals and hashCode contract")
        void testEqualsAndHashCode() {
            var config1 = new LocalWebUiProperties(true, 8081, "localhost");
            var config2 = new LocalWebUiProperties(true, 8081, "localhost");
            var config3 = new LocalWebUiProperties(false, 8082, "0.0.0.0");

            assertAll(
                () -> assertEquals(config1, config2),
                () -> assertEquals(config1.hashCode(), config2.hashCode()),
                () -> assertNotEquals(config1, config3),
                () -> assertNotEquals(null, config1),
                () -> assertNotEquals("string", config1)
            );
        }
    }
}
