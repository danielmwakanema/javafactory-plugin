package io.github.javafactoryplugindev.plugin;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    // 1. 자바 파일을 읽어서 문자열로 반환
    public static String readFileAsString(Project project, String relativePath) throws IOException {
        String basePath = project.getBasePath();
        if (basePath == null) throw new IOException("Project base path is null");

        Path path = Paths.get(basePath, relativePath);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static String readFileAsString(Project project, PsiClass psiClass) throws IOException {
        PsiFile psiFile = psiClass.getContainingFile();
        if (psiFile == null) throw new IOException("PsiFile is null");

        VirtualFile vFile = psiFile.getVirtualFile();
        if (vFile == null) throw new IOException("VirtualFile is null");

        // 🔍 최신 내용: Document에서 읽기
        Document doc = FileDocumentManager.getInstance().getDocument(vFile);
        if (doc != null) {
            return doc.getText(); // 현재 열린 편집기 상의 내용까지 포함됨
        }

        // fallback: 저장된 파일에서 읽기
        return VfsUtilCore.loadText(vFile);
    }

    // 2. 새 파일을 생성하고 내용 쓰기 (경로까지 포함해서 생성)
    public static void writeNewFile(Project project, String relativePath, String content) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String basePath = project.getBasePath();
                if (basePath == null) return;

                File ioFile = new File(basePath, relativePath);
                if (!ioFile.exists()) {
                    ioFile.getParentFile().mkdirs();
                    ioFile.createNewFile();
                }

                VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ioFile);
                if (vFile != null) {
                    VfsUtil.saveText(vFile, content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // 3. 기존 파일에 덮어쓰기
    public static void overwriteFile(Project project, String relativePath, String content) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            String basePath = project.getBasePath();
            if (basePath == null) return;

            File ioFile = new File(basePath, relativePath);
            if (!ioFile.exists()) return; // 파일 없으면 안씀

            VirtualFile vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ioFile);
            if (vFile != null) {
                try {
                    VfsUtil.saveText(vFile, content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 4. 파일 존재 여부 확인
    public static boolean fileExists(Project project, String relativePath) {
        String basePath = project.getBasePath();
        if (basePath == null) return false;

        File ioFile = new File(basePath, relativePath);
        return ioFile.exists();
    }

    // 존재하면 .dup 을 붙임
    // 반복하면 A.dup, A.dup.dup A.dup.dup.dup 이 나오게. ..
    public static String generateSafeName(Project project, String relativePath) {
        String basePath = project.getBasePath();
        if (basePath == null) return relativePath;

        File originalFile = new File(basePath, relativePath);
        String currentPath = relativePath;
        while (new File(basePath, currentPath).exists()) {
            currentPath += ".dup";
        }
        return currentPath;
    }
}
