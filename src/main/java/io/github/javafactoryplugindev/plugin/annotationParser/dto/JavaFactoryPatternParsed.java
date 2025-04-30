package io.github.javafactoryplugindev.plugin.annotationParser.dto;

import com.intellij.psi.PsiClass;

import java.util.List;

public record JavaFactoryPatternParsed(
        PsiClass selfPsi,
        List<String> patternNames
) {
}
