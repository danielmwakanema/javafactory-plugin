package io.github.javafactoryplugindev.plugin.pattern;

public enum GenerationType {
    NONE(Integer.MAX_VALUE), IMPLEMENTATION(1), TEST(2), FIXTURE(3);

    private final int executionOrder;

    GenerationType(int order) {
        this.executionOrder = order;
    }

    public int getExecutionOrder() {
        return executionOrder;
    }
}
