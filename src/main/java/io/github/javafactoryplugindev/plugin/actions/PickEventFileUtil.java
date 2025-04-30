package io.github.javafactoryplugindev.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

public class PickEventFileUtil {

    public static PsiFile pickPsiFile(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (project == null || file == null) {
            return null;
        }
        System.out.println("action - open JavaFactory code gen");

        // 로그 출력


        return PsiManager.getInstance(project).findFile(file);
    }

    public static PsiClass getFirstPublicClass(PsiFile psiFile) {
        if (psiFile instanceof PsiClassOwner owner) {
            for (PsiClass psiClass : owner.getClasses()) {
                if (psiClass.hasModifierProperty(PsiModifier.PUBLIC)) {
                    return psiClass;
                }
            }
        }
        return null;
    }
}
