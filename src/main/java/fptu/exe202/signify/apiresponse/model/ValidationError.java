package fptu.exe202.signify.apiresponse.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for building field-level validation error maps.
 */
public final class ValidationError {

    private final Map<String, String> fieldErrors;

    private ValidationError(Map<String, String> fieldErrors) {
        this.fieldErrors = Map.copyOf(fieldErrors);
    }

    /**
     * Creates a new builder for assembling validation errors.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a ValidationError from an existing map.
     */
    public static ValidationError of(Map<String, String> fieldErrors) {
        return new ValidationError(fieldErrors);
    }

    /**
     * Returns an unmodifiable map of field name to error message.
     */
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public static final class Builder {

        private final Map<String, String> fieldErrors = new LinkedHashMap<>();

        private Builder() {
        }

        /**
         * Adds a field error.
         *
         * @param field   the field name
         * @param message the error message
         * @return this builder
         */
        public Builder addError(String field, String message) {
            fieldErrors.put(field, message);
            return this;
        }

        public ValidationError build() {
            return new ValidationError(fieldErrors);
        }
    }
}
