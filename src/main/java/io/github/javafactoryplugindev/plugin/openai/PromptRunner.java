package io.github.javafactoryplugindev.plugin.openai;

import com.intellij.openapi.project.Project;

public interface PromptRunner {
    String call(Project project, String systemPrompt, String userPrompt);

}
