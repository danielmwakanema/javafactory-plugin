package io.github.javafactoryplugindev.plugin.ui.patternGeneration;

import com.intellij.psi.PsiClass;

public class PathGuesser {

    public static String guessDefaultImplPathFromInterface(PsiClass interfaceClass) {
        String qualifiedName = interfaceClass.getQualifiedName();
        if (qualifiedName == null || qualifiedName.isBlank()) return null;

        String basePath = getBaseMainPath(interfaceClass);
        String implName = "Default" + interfaceClass.getName(); // remove "I"

        return basePath + "/" + qualifiedName.replace('.', '/')
                .replace(interfaceClass.getName(), implName) + ".java";
    }

    public static String guessTestPathFromInterface(PsiClass interfaceClass) {
        String implName = "Default" + interfaceClass.getName();
        String basePath = getBaseTestPath(interfaceClass);

        return basePath + "/" + interfaceClass.getQualifiedName().replace('.', '/')
                .replace(interfaceClass.getName(), implName + "Test") + ".java";
    }

    public static String guessFixturePathFromInterface(PsiClass interfaceClass) {
        String implName = "FakeDefault" + interfaceClass.getName();
        String basePath = getBaseTestPath(interfaceClass);

        return basePath + "/" + interfaceClass.getQualifiedName().replace('.', '/')
                .replace(interfaceClass.getName(), implName ) + ".java";
    }



    public static String guessDefaultImplPath(PsiClass interfaceClass) {
        String qualifiedName = interfaceClass.getQualifiedName();
        if (qualifiedName == null) return null;

        String basePath = getBaseMainPath(interfaceClass);
        String implName = "Default" + interfaceClass.getName();

        return basePath + "/" + qualifiedName.replace('.', '/').replace(interfaceClass.getName(), implName) + ".java";
    }

    public static String guessTestPath(PsiClass implClass) {
        String qualifiedName = implClass.getQualifiedName();
        if (qualifiedName == null) return null;

        String basePath = getBaseTestPath(implClass);
        String testName = implClass.getName() + "Test";

        return basePath + "/" + qualifiedName.replace('.', '/').replace(implClass.getName(), testName) + ".java";
    }

    public static String guessFixturePath(PsiClass implClass) {
        String qualifiedName = implClass.getQualifiedName();
        if (qualifiedName == null) return null;

        String basePath = getBaseTestPath(implClass);
        String fixtureName = implClass.getName() + "Fixture";

        return basePath + "/" + qualifiedName.replace('.', '/').replace(implClass.getName(), fixtureName) + ".java";
    }

    private static String getBaseMainPath(PsiClass psiClass) {
        // 이 경로는 상황에 따라 project base path 등으로 바꿀 수 있음
        return "src/main/java";
    }

    private static String getBaseTestPath(PsiClass psiClass) {
        return "src/test/java";
    }
}