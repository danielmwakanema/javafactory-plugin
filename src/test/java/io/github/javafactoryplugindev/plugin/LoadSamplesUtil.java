package io.github.javafactoryplugindev.plugin;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LoadSamplesUtil {
    public static Map<String, PsiClass> loadTestSources(CodeInsightTestFixture fixture) throws IOException {
        Map<String, PsiClass> result = new HashMap<>();

        Path sampleRoot = Paths.get("src/test/java/com/github/javafactorydev/plugin/testData");

        Files.walk(sampleRoot)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(file -> {
                    try {
                        String relativePath = sampleRoot.relativize(file).toString();
                        String packagePath = relativePath.replace("\\", "/").replace(".java", "");
                        String packageName = "com.github.javafactorydev.plugin.testData." + packagePath.replace('/', '.');
                        String className = packagePath.substring(packagePath.lastIndexOf('/') + 1);

                        String content = Files.readString(file);

                        // ✅ 이때 src/test/java 생략하고 등록
                        PsiFile psiFile = fixture.addFileToProject(
                                "io/github/javafactorydev/plugin/testData/" + relativePath.replace("\\", "/"),
                                content
                        );
                        PsiClass psiClass = ((PsiClassOwner) psiFile).getClasses()[0];
                        result.put(packageName + "." + className, psiClass);

                    } catch (Exception e) {
                        throw new RuntimeException("테스트 소스 로딩 실패: " + file, e);
                    }
                });

        return result;
    }
}
