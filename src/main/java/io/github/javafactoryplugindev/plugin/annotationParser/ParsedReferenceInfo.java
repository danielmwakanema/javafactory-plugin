package io.github.javafactoryplugindev.plugin.annotationParser;

import com.intellij.psi.PsiClass;

import java.util.List;

public record ParsedReferenceInfo(
        PsiClass selfPsi,
        // AnnotationInfo annotationInfo,
        PsiClass defaultImpl,
        PsiClass defaultFixture,
        List<PsiClass> referencedData,
        List<PsiClass> referencedApi,
        List<PsiClass> referencedApiImpl,
        List<PsiClass> referencedApiFixture,
        List<PsiClass> referencedClass
) {

}
