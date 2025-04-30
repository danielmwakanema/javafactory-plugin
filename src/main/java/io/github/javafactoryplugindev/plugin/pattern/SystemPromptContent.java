package io.github.javafactoryplugindev.plugin.pattern;

public class SystemPromptContent {
    private String goal;
    private String rules;
    private String outputFormat;
    private String outputExample;

    public SystemPromptContent() {}

    public SystemPromptContent(String goal, String rules, String outputFormat, String outputExample) {
        this.goal = goal;
        this.rules = rules;
        this.outputFormat = outputFormat;
        this.outputExample = outputExample;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getOutputExample() {
        return outputExample;
    }

    public void setOutputExample(String outputExample) {
        this.outputExample = outputExample;
    }
}
