package io.github.javafactoryplugindev.plugin.annotationParser.dto;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;

import java.util.List;
import java.util.stream.Collectors;

public record JavaFactoryDataParsed(
        PsiClass selfPsi,
        List<PsiClass> referencedData,
        List<PsiClass> referencedClass
) {

    @Override
    public String toString() {
        return "JavaFactoryDataParsed{" +
                "selfPsi=" + describePsiClass(selfPsi) +
                ", referencedData=" + referencedData.stream().map(this::describePsiClass).collect(Collectors.toList()) +
                ", referencedClass=" + referencedClass.stream().map(this::describePsiClass).collect(Collectors.toList()) +
                '}';
    }

    private String describePsiClass(PsiClass psiClass) {
        if (psiClass == null) return "null";
        PsiFile file = psiClass.getContainingFile();
        if (file != null && file.getVirtualFile() != null) {
            return psiClass.getQualifiedName() + " (path=" + file.getVirtualFile().getPath() + ")";
        } else {
            return psiClass.getQualifiedName() + " (no file)";
        }
    }
}
