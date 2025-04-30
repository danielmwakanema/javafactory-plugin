package io.github.javafactoryplugindev.plugin.pattern;

import io.github.javafactoryplugindev.plugin.FileUtils;
import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryApiParsed;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PromptRenderUtils {

    public static String showSystemPrompt(SystemPromptContent sys) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Goal\n").append(sys.getGoal()).append("\n\n");
        sb.append("## Rules\n").append(sys.getRules()).append("\n\n");
        sb.append("## Output Format\n").append(sys.getOutputFormat()).append("\n\n");
        sb.append("## Output Example\n").append(sys.getOutputExample()).append("\n");
        return sb.toString();
    }


    public static String showUserPromptPreview(UserPromptContent user) {
        StringBuilder sb = new StringBuilder();
        for (UserPromptContent.UserPromptItem item : user.getItems()) {
            sb.append("<< ").append(item.getKey()).append(" >>\n");
            for (var flag : item.getFlags()) {
                sb.append("... source of ").append(flag).append(" ...\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    public static Map<String, String> renderUserPrompt(Project project, UserPromptContent promptContent, JavaFactoryApiParsed parsedInfo) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();

        for (UserPromptContent.UserPromptItem item : promptContent.getItems()) {
            StringBuilder sb = new StringBuilder();

            for (var flagStr : item.getFlags()) {
                ReferenceFlag flag;
                try {
                    flag = ReferenceFlag.valueOf(flagStr.name().trim());
                } catch (IllegalArgumentException e) {
                    // 잘못된 flag는 무시
                    continue;
                }

                List<PsiClass> classes = extractClassesByFlag(flag, parsedInfo);
                if (classes == null || classes.isEmpty()) continue;

                for (PsiClass psiClass : classes) {
                    String code = FileUtils.readFileAsString(project, psiClass);
                    if (code != null && !code.isBlank()) {
                        sb.append(code).append("\n\n");
                    }

                    sb.append("-------------------------------------\n");
                }
            }

            if (!sb.isEmpty()) {
                result.put(item.getKey(), sb.toString().trim());
            }
        }

        return result;
    }

    public static Map<String, String> renderUserPromptFromGenerated(Project project, UserPromptContent promptContent, JavaFactoryApiParsed parsedInfo, Map<ReferenceFlag, String> generatedSources) throws IOException {
        Map<String, String> result = new LinkedHashMap<>();

        for (UserPromptContent.UserPromptItem item : promptContent.getItems()) {
            StringBuilder sb = new StringBuilder();

            for (var flagStr : item.getFlags()) {
                ReferenceFlag flag;
                try {
                    flag = ReferenceFlag.valueOf(flagStr.name().trim());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                // ⭐ 먼저 generatedSources에 있는지 확인
                if (generatedSources.containsKey(flag)) {
                    String generated = generatedSources.get(flag);
                    if (generated != null && !generated.isBlank()) {
                        sb.append(generated).append("\n\n");
                    }
                    continue; // 이 flag는 끝났으니 다음 flag로
                }

                List<PsiClass> classes = extractClassesByFlag(flag, parsedInfo);
                if (classes == null || classes.isEmpty()) continue;

                for (PsiClass psiClass : classes) {
                    String code = FileUtils.readFileAsString(project, psiClass);
                    if (code != null && !code.isBlank()) {
                        String className = psiClass.getName(); // 단순 이름 (e.g., Foo)
                        sb.append("===== ").append(className).append(" ======\n\n");
                        sb.append(code).append("\n");
                        sb.append("======================\n\n");
                    }
                }
            }

            if (!sb.isEmpty()) {
                result.put(item.getKey(), sb.toString().trim());
            }
        }

        return result;
    }

    public static String formatPromptMap(Map<String, String> promptMap) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : promptMap.entrySet()) {
            sb.append("<< ").append(entry.getKey()).append(" >>\n");
            sb.append(indentMultiline(entry.getValue(), "    "));
            sb.append("\n\n");
        }

        return sb.toString().trim();
    }

    private static String indentMultiline(String text, String indent) {
        return Arrays.stream(text.split("\n"))
                .map(line -> indent + line)
                .collect(Collectors.joining("\n"));
    }

    private static List<PsiClass> extractClassesByFlag(ReferenceFlag flag, JavaFactoryApiParsed parsed) {
        return switch (flag) {
            case TARGET_API -> List.of(parsed.selfPsi());
            case TARGET_DEFAULT_API_IMPL -> parsed.defaultImpl() != null ? List.of(parsed.defaultImpl()) : List.of();
            case TARGET_DEFAULT_API_FIXTURE ->
                    parsed.defaultFixture() != null ? List.of(parsed.defaultFixture()) : List.of();
            case REFERENCED_API -> parsed.referencedApi();
            case REFERENCED_API_IMPL -> parsed.referencedApiImpl();
            case REFERENCED_API_FIXTURE -> parsed.referencedApiFixture();
            case DATA -> parsed.referencedData();
            case OTHER_REFERENCED -> parsed.referencedClass();
        };
    }

}
