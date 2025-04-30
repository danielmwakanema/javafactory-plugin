package io.github.javafactoryplugindev.plugin;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class SaveFileOnPanel {
    public static void saveContentToFile(Project project, JPanel parentComponent, String relativePath, String content) {
        if (content == null || content.isBlank()) {
            JOptionPane.showMessageDialog(parentComponent, " No content to save", "⚠️ Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (FileUtils.fileExists(project, relativePath)) {
            int result = JOptionPane.showConfirmDialog(
                    parentComponent,
                    "A file already exists at the target path. \n Do you want to overwrite it? \n \n Path: " + relativePath,
                    "⚠️ Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION
            );
            if (result != JOptionPane.YES_OPTION) return;
        }

        FileUtils.writeNewFile(project, relativePath, content);
        JOptionPane.showMessageDialog(parentComponent, "File saved successfully: " + relativePath, "✅ Saved", JOptionPane.INFORMATION_MESSAGE);
    }
}
