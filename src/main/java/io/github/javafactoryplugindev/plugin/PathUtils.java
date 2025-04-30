package io.github.javafactoryplugindev.plugin;

import com.intellij.psi.PsiClass;

public class PathUtils {

    public static String guessDomainImplWithApi(PsiClass apiClass) {
        String fqn = apiClass.getQualifiedName();
        if (fqn == null) return null;
        // MyApi -> DefaultMyApi
        int lastDotIndex = fqn.lastIndexOf(".");
        String prefix = fqn.substring(0, lastDotIndex + 1);
        String className = fqn.substring(lastDotIndex + 1);
        String implFqn = prefix + "Default" + className;
        return "src/main/java/" + implFqn.replace('.', '/') + ".java";
    }

    public static String guessDomainImplTestWithApi(PsiClass apiClass) {
        String fqn = apiClass.getQualifiedName();
        if (fqn == null) return null;
        // MyApi -> DefaultMyApiTest
        int lastDotIndex = fqn.lastIndexOf(".");
        String prefix = fqn.substring(0, lastDotIndex + 1);
        String className = fqn.substring(lastDotIndex + 1);
        String testFqn = prefix + "Default" + className + "Test";
        return "src/test/java/" + testFqn.replace('.', '/') + ".java";
    }

    public static String guessDomainImplFixtureWithApi(PsiClass apiClass, String basePackage) {
        String fqn = apiClass.getQualifiedName();
        if (fqn == null || !fqn.startsWith(basePackage)) return null;

        String suffix = fqn.substring(basePackage.length());
        String fixtureFqn = basePackage + ".fixture" + suffix + "Fixture";
        return "src/test/java/" + fixtureFqn.replace('.', '/') + ".java";
    }

    public static String guessInfraImplWithApi(PsiClass apiClass) {
        String fqn = apiClass.getQualifiedName();
        if (fqn == null) return null;

        int lastDotIndex = fqn.lastIndexOf(".");
        String prefix = fqn.substring(0, lastDotIndex + 1);
        String className = fqn.substring(lastDotIndex + 1);

        String targetName = className.startsWith("I") && className.length() > 1
                ? className.substring(1)
                : "Default" + className;

        String implFqn = prefix + targetName;
        return "src/main/java/" + implFqn.replace('.', '/') + ".java";
    }

    public static String guessInfraImplTestWithApi(PsiClass apiClass) {
        String fqn = apiClass.getQualifiedName();
        if (fqn == null) return null;

        int lastDotIndex = fqn.lastIndexOf(".");
        String prefix = fqn.substring(0, lastDotIndex + 1);
        String className = fqn.substring(lastDotIndex + 1);

        String targetName = className.startsWith("I") && className.length() > 1
                ? className.substring(1)
                : "Default" + className;

        String testFqn = prefix + targetName + "Test";
        return "src/test/java/" + testFqn.replace('.', '/') + ".java";
    }

    public static String guessInfraImplFixtureWithApi(PsiClass apiClass, String basePackage) {
        String fqn = apiClass.getQualifiedName();
        if (fqn == null || !fqn.startsWith(basePackage)) return null;

        String suffix = fqn.substring(basePackage.length());
        String className = apiClass.getName();

        String targetName = className.startsWith("I") && className.length() > 1
                ? "Fake" + className.substring(1)
                : "Fake" + className;

        String fixtureFqn = basePackage + ".fixture" + suffix.replace(className, targetName);
        return "src/test/java/" + fixtureFqn.replace('.', '/') + ".java";
    }
}
