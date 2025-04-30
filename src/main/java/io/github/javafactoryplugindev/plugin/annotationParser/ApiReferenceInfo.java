package io.github.javafactoryplugindev.plugin.annotationParser;

import com.intellij.psi.PsiClass;

import java.util.List;

public record ApiReferenceInfo(
        PsiClass selfPsi,
        PsiClass defaultImpl,
        PsiClass defaultFixture,
        List<PsiClass> referencedData,
        List<PsiClass> referencedClass,

        List<PsiClass> referencedApi,

        List<PsiClass> referencedApiImpl,

        List<PsiClass> referencedApiFixture

        ) {


}
