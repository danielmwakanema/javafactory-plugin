package io.github.javafactoryplugindev.plugin.openai;

public class OpenAiCallFailedException extends RuntimeException {
    private String message;

    private ErrorType errorType;

    public OpenAiCallFailedException(String message) {
        super();
        this.message = message;
        this.errorType = ErrorType.UNKNOWN_ERROR;
    }

    public OpenAiCallFailedException(String message, ErrorType errorType) {
        super();
        this.message = message;
        this.errorType = errorType;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public static enum ErrorType {
        KEY_FAILED,
        KEY_MISSING,
        ERROR_ON_SERVER,
        UNKNOWN_ERROR

    }
}
