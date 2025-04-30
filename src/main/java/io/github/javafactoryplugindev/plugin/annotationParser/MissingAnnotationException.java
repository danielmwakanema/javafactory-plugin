package io.github.javafactoryplugindev.plugin.annotationParser;

import com.intellij.psi.PsiClass;

public class MissingAnnotationException extends RuntimeException {
    public MissingAnnotationException(PsiClass target, String expectedAnnotation) {
        super("Class " + target.getQualifiedName() + " is missing expected annotation @" + expectedAnnotation);
    }
}
