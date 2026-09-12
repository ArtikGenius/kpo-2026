package studying.withsolid.exception;

import lombok.Getter;

/** Application level error carrying a machine readable code and an optional original cause. */
@Getter
public class ApplicationException extends RuntimeException {
    /** Code describing the kind of the failure. */
    private final ApplicationErrorCode code;

    /**
     * Creates an exception without an original cause.
     *
     * @param code code describing the kind of the failure
     * @param message human readable description of the failure
     */
    public ApplicationException(ApplicationErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Creates an exception preserving the original cause.
     *
     * @param code code describing the kind of the failure
     * @param message human readable description of the failure
     * @param cause original error that led to this failure
     */
    public ApplicationException(ApplicationErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
