package io.github.javafactoryplugindev.plugin.annotationParser;

import com.intellij.psi.PsiClass;

import java.util.List;

public record DataReferenceInfo(PsiClass selfPsi,
                                List<PsiClass> referencedData,
                                List<PsiClass> referencedClass) {
}
