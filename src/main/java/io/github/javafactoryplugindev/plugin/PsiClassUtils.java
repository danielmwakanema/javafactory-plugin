package io.github.javafactoryplugindev.plugin;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PsiClassUtils {
    public static PsiAnnotation findAnnotation(PsiClass clazz, String nameOrFqName) {
        PsiModifierList modifierList = clazz.getModifierList();
        if (modifierList == null) return null;

        for (PsiAnnotation annotation : modifierList.getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            String simpleName = qualifiedName != null ? qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1) : null;

            if (nameOrFqName.equals(qualifiedName) || nameOrFqName.equals(simpleName)) {
                return annotation;
            }
        }
        return null;
    }


    public static List<PsiClass> getClassArrayAttribute(PsiAnnotation annotation, String name) {
        List<PsiClass> results = new ArrayList<>();
        PsiAnnotationMemberValue value = annotation.findAttributeValue(name);

        if (value instanceof PsiArrayInitializerMemberValue array) {
            for (PsiAnnotationMemberValue item : array.getInitializers()) {
                addIfResolvable(item, results);
            }
        } else {
            addIfResolvable(value, results);
        }
        return results;
    }

    private static void addIfResolvable(PsiAnnotationMemberValue item, List<PsiClass> results) {
        if (item instanceof PsiClassObjectAccessExpression expr) {
            PsiType type = expr.getOperand().getType();
            PsiClass resolved = PsiUtil.resolveClassInClassTypeOnly(type);
            if (resolved != null) results.add(resolved);
        }
    }

    public static List<String> getStringArrayAttribute(PsiAnnotation annotation, String attrName) {
        if (annotation == null) return List.of();

        PsiAnnotationMemberValue value = annotation.findAttributeValue(attrName);
        if (value == null) return List.of();

        // 배열인 경우
        if (value instanceof PsiArrayInitializerMemberValue arrayValue) {
            return Arrays.stream(arrayValue.getInitializers())
                    .filter(PsiLiteralExpression.class::isInstance)
                    .map(expr -> ((PsiLiteralExpression) expr).getValue())
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        // 단일 문자열 값일 경우
        if (value instanceof PsiLiteralExpression literal) {
            Object raw = literal.getValue();
            if (raw instanceof String str) {
                return List.of(str);
            }
        }

        return List.of();
    }

}
