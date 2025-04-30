package io.github.javafactoryplugindev.plugin.openai.prompt;

public interface PromptGenerator<F extends GenerateRequest> {
    String system();

    String user(F request);
}