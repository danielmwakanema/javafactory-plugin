package io.github.javafactoryplugindev.plugin.actions;


import io.github.javafactoryplugindev.plugin.annotationParser.AnnotationParser;
import io.github.javafactoryplugindev.plugin.annotationParser.DefaultAnnotationParser;

import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryApiParsed;
import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryPatternParsed;
import io.github.javafactoryplugindev.plugin.pattern.GenerationType;
import io.github.javafactoryplugindev.plugin.pattern.Pattern;
import io.github.javafactoryplugindev.plugin.pattern.PatternStorageService;
import io.github.javafactoryplugindev.plugin.pattern.ReferenceFlag;
import io.github.javafactoryplugindev.plugin.ui.patternGeneration.PatternExecutionPreviewPanel;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;



public class OpenCodeGenPanelAction extends AnAction {

    private static final Logger log = Logger.getInstance(OpenCodeGenPanelAction.class);

    private static final AnnotationParser annotationParser = DefaultAnnotationParser.getInstance();


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (project == null || file == null) {
            return;
        }
        log.info("action - open JavaFactory code gen");

        // 로그 출력
        var clickedPsiFile = PickEventFileUtil.pickPsiFile(e);
        var publicClass = PickEventFileUtil.getFirstPublicClass(clickedPsiFile);


        JavaFactoryPatternParsed patternParsed = null;
        JavaFactoryApiParsed apiInfo = null;

        if(publicClass == null){
            showMissingPublicClass(project);
        }

        System.out.println("[Parsed] API self = " + publicClass.getQualifiedName());
        try {
            patternParsed = annotationParser.parsePattern(publicClass);
            System.out.println("patternInfo: " + patternParsed.toString());
        } catch (Exception err) {
            err.printStackTrace();
            showMissingAnnotationDialog(project, "@JavaFactoryPattern");
            return;
        }

        try {
            apiInfo = annotationParser.parseApiReferences(publicClass);
            System.out.println("apiInfo : " + apiInfo.toString());
        } catch (Exception err) {
            showMissingAnnotationDialog(project, "@JavaFactoryApi");
            return;
        }

        // OUT CONDITION : Pattern annotation 이 없는 경우
        if (patternParsed == null) {
            showMissingAnnotationDialog(project, "@JavaFactoryPattern");
            return;
        }

        // OUT CONDITION : API annotation 이 없는 경우
        if (apiInfo == null) {
            showMissingAnnotationDialog(project, "@JavaFactoryApi");
            return;
        }

        // OUT CONDITION : Pattern name 이 부정확한 경우.
        List<String> patternNotExist = listNotExist(project, patternParsed);
        if (!patternNotExist.isEmpty()) {
            showPatternNotExistDialog(project, patternNotExist);
            return;
        }


        var requiredPatterns = listActionPatterns(e.getProject(), patternParsed);
        ///
        var sortedPatterns = requiredPatterns.stream()
                .sorted(Comparator.comparingInt(p ->  p != null ? p.getGenerationType().getExecutionOrder() : Integer.MAX_VALUE ))
                .toList();


        var missingFlags = getMissingFlagsByPattern(sortedPatterns, apiInfo);

        // ALARM CONDITION : Pattern 에 정의된 flag 를 모두 포함하지 않는 경우.
        if (!missingFlags.isEmpty()) {
            showMissingFlagsDialog(project, missingFlags);

            int confirm = Messages.showYesNoDialog(
                    project,
                    "Do you want to continue?",
                    "Missing Reference Information",
                    Messages.getQuestionIcon()
            );
            if (confirm != Messages.YES) return;
        }


        // 팝업 진입
        showPatternPreviewPanel(project, sortedPatterns, apiInfo);

    }

    private void showPatternNotExistDialog(Project project, List<String> patternNotExist) {
        StringBuilder sb = new StringBuilder();
        sb.append("The pattern does not exist in the storage.\n\n")
                .append("Please create the pattern using the Pattern Management Panel before proceeding.\n")
                .append("Currently Missing Patterns: ").append(patternNotExist.stream().collect(Collectors.joining(", ")));

        Messages.showMessageDialog(
                project,
                sb.toString(),
                "Missing Pattern Definition Warning",
                Messages.getWarningIcon()
        );
    }

    private List<String> listNotExist(Project project, JavaFactoryPatternParsed patternParsed) {
        var storage = PatternStorageService.getInstance(project);
        List<String> patternsNotFound = new ArrayList<>();

        for (var key : patternParsed.patternNames()) {
            if (storage.findByName(key) == null)
                patternsNotFound.add(key);
        }
        return patternsNotFound;
    }


    private void showPatternPreviewPanel(Project project, List<Pattern> sortedPatterns, JavaFactoryApiParsed apiInfo) {
        JFrame frame = new JFrame("🔍 Show Patterns");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new PatternExecutionPreviewPanel(project, sortedPatterns, apiInfo));
        frame.setVisible(true);
    }

    private List<Pattern> listActionPatterns(Project project, JavaFactoryPatternParsed patternParsed) {
        var patternStorage = PatternStorageService.getInstance(project);
        List<Pattern> result = new ArrayList<>();

        for (var key : patternParsed.patternNames()) {
            result.add(patternStorage.findByName(key));
        }

        return result;
    }

    private Map<String, Set<ReferenceFlag>> getMissingFlagsByPattern(List<Pattern> patterns, JavaFactoryApiParsed info) {
        Set<ReferenceFlag> available = collectAvailableFlagsFrom(info);
        Map<String, Set<ReferenceFlag>> result = new LinkedHashMap<>();

        for (Pattern pattern : patterns) {
            Set<ReferenceFlag> required = pattern.getUserPromptContent().getItems().stream()
                    .flatMap(item -> item.getFlags().stream())
                    .filter(item-> !ReferenceFlag.OTHER_REFERENCED.equals(item))
                    .collect(Collectors.toSet());

            Set<ReferenceFlag> missing = new HashSet<>(required);
            missing.removeAll(available);

            if (!missing.isEmpty()) {
                result.put(pattern.getName(), missing);
            }

            // 패턴 실행 후 획득할 수 있는 플래그 추가
            available.addAll(providedFlagsFrom(pattern.getGenerationType()));
        }

        return result;
    }


    private Set<ReferenceFlag> collectAvailableFlagsFrom(JavaFactoryApiParsed info) {
        Set<ReferenceFlag> flags = new HashSet<>();

        if (info.selfPsi() != null) flags.add(ReferenceFlag.TARGET_API);
        if (info.defaultImpl() != null) flags.add(ReferenceFlag.TARGET_DEFAULT_API_IMPL);
        if (info.defaultFixture() != null) flags.add(ReferenceFlag.TARGET_DEFAULT_API_FIXTURE);

        if (!info.referencedApi().isEmpty()) flags.add(ReferenceFlag.REFERENCED_API);
        if (!info.referencedApiImpl().isEmpty()) flags.add(ReferenceFlag.REFERENCED_API_IMPL);
        if (!info.referencedApiFixture().isEmpty()) flags.add(ReferenceFlag.REFERENCED_API_FIXTURE);

        if (!info.referencedData().isEmpty()) flags.add(ReferenceFlag.DATA);
        if (!info.referencedClass().isEmpty()) flags.add(ReferenceFlag.OTHER_REFERENCED);

        return flags;
    }


    private Set<ReferenceFlag> providedFlagsFrom(GenerationType type) {
        Set<ReferenceFlag> flags = new HashSet<>();
        if (type == null)
            return Set.of();
        switch (type) {
            case IMPLEMENTATION -> flags.add(ReferenceFlag.TARGET_DEFAULT_API_IMPL);
            case TEST -> flags.add(ReferenceFlag.TARGET_DEFAULT_API_IMPL);
            case FIXTURE -> flags.add(ReferenceFlag.TARGET_DEFAULT_API_FIXTURE);
        }
        return flags;
    }


    private void showMissingFlagsDialog(Project project, Map<String, Set<ReferenceFlag>> missingFlagsMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("⚠️ The following reference information is missing for the pattern:\n\n");

        for (Map.Entry<String, Set<ReferenceFlag>> entry : missingFlagsMap.entrySet()) {
            sb.append("📌 ").append(entry.getKey()).append(":\n");
            for (ReferenceFlag flag : entry.getValue()) {
                sb.append("   - ").append(flag.name()).append("\n");
            }
            sb.append("\n");
        }

        Messages.showMessageDialog(
                project,
                sb.toString(),
                "Missing Reference Information Notice",
                Messages.getWarningIcon()
        );
    }

    private void showMissingAnnotationDialog(Project project, String className) {
        StringBuilder sb = new StringBuilder();
        sb.append("@JavaFactoryPattern and @JavaFactoryApi annotations are required to generate code.\n\n")
                .append("Currently Missing Items: ").append(className);

        Messages.showMessageDialog(
                project,
                sb.toString(),
                "Missing Annotation Warning",
                Messages.getWarningIcon()
        );
    }

    private void showMissingPublicClass(Project project) {
        StringBuilder sb = new StringBuilder();
        sb.append(" Can't find 'Java public class'  .\n\n");

        Messages.showMessageDialog(
                project,
                sb.toString(),
                "Java File Warning",
                Messages.getWarningIcon()
        );
    }
}
