package io.github.javafactoryplugindev.plugin;

public class BasePackageUtils {

    // basePackage가 null이거나 비정상적일 경우 false 반환
    public static boolean isValid(String basePackage) {
        return basePackage != null
                && !basePackage.isBlank()
                && !basePackage.endsWith(".")
                && basePackage.matches("[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*");
    }

    // 예외 던지면서 강제 종료하는 방식
    public static void assertValid(String basePackage) {
        if (!isValid(basePackage)) {
            throw new IllegalArgumentException("잘못된 basePackage 값입니다: " + basePackage);
        }
    }

    // 필요 시 자동으로 후처리된 값 리턴 (예: 맨 뒤 '.' 제거 등)
    public static String normalize(String basePackageInput) {
        if (basePackageInput == null || basePackageInput.isBlank()) return "";
        var basePackage = basePackageInput.trim();
        return basePackage.endsWith(".") ? basePackage.substring(0, basePackage.length() - 1) : basePackage;
    }
}