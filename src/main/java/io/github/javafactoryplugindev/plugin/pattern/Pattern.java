package io.github.javafactoryplugindev.plugin.pattern;

public class Pattern {
    private String name;
    private SystemPromptContent systemPromptPattern;
    private UserPromptContent userPromptContent;

    private GenerationType generationType;
    public static void validatePatternName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pattern name must not be null or blank.");
        }
        if (!name.matches("^[a-z_]+$")) {
            throw new IllegalArgumentException("Pattern name must contain only lowercase letters (a-z) and underscores (_).");
        }
    }

    public GenerationType getGenerationType() {
        return generationType;
    }

    public void setGenerationType(GenerationType generationType) {
        this.generationType = generationType;
    }


    public Pattern() {
    } // 🔹 직렬화용 기본 생성자

    public Pattern(String name, SystemPromptContent systemPromptPattern, UserPromptContent userPromptContent, GenerationType generationType) {
        validatePatternName(name);
        if(generationType == null){
            generationType = GenerationType.NONE;
        }

        this.name = name;
        this.systemPromptPattern = systemPromptPattern;
        this.generationType = generationType;
        this.userPromptContent = userPromptContent;
    }


    public Pattern(String name, SystemPromptContent systemPromptPattern, UserPromptContent userPromptContent) {
        validatePatternName(name);
        this.name = name;
        this.systemPromptPattern = systemPromptPattern;
        this.generationType = GenerationType.NONE;
        this.userPromptContent = userPromptContent;
    }

    public String getName() {
        return name;
    }

    public SystemPromptContent getSystemPromptPattern() {
        return systemPromptPattern;
    }

    public UserPromptContent getUserPromptContent() {
        return userPromptContent;
    }

    public void setName(String name) {
        validatePatternName(name);
        this.name = name;
    }

    public void setSystemPromptPattern(SystemPromptContent systemPromptPattern) {
        this.systemPromptPattern = systemPromptPattern;
    }

    public void setUserPromptContent(UserPromptContent userPromptContent) {
        this.userPromptContent = userPromptContent;
    }
}
