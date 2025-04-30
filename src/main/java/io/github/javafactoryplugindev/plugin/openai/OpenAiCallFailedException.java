package io.github.javafactoryplugindev.plugin.openai;

public class OpenAiCallFailedException extends RuntimeException {
    private String message;

    public OpenAiCallFailedException(String message) {
        super();
        this.message = message;
    }
}
