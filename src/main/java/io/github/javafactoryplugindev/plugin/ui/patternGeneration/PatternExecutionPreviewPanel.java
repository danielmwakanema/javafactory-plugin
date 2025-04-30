package io.github.javafactoryplugindev.plugin.ui.patternGeneration;


import io.github.javafactoryplugindev.plugin.annotationParser.dto.JavaFactoryApiParsed;
import io.github.javafactoryplugindev.plugin.openai.DefaultPromptRunner;
import io.github.javafactoryplugindev.plugin.openai.MdCleaner;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import io.github.javafactoryplugindev.plugin.pattern.GenerationType;
import io.github.javafactoryplugindev.plugin.pattern.Pattern;
import io.github.javafactoryplugindev.plugin.pattern.PromptRenderUtils;
import io.github.javafactoryplugindev.plugin.pattern.ReferenceFlag;
import io.github.javafactoryplugindev.plugin.SaveFileOnPanel;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * TODO
 *
 * 1. prompt 조회 버튼 추가. ( 0 )
 * 2. 예상 경로 조회 조건 추가 ( 0 )
 *  - target api 존재
 *  - 생성 타입 존재.
 *
 * 3. 예상 경로 조회 조건에 맞다면, 저장 버튼 활성화 ( 0)
 * 4. 프롬프트 작성 조건에 필요한 이력들 수집하는 함수 제작 . (0)
 * 5. 실제 llm 호출 기능 제작.
 * 6. 예상 저장 경로 suggest 하기. ( 0 )
 * */

public class PatternExecutionPreviewPanel extends JPanel {

    private final transient Project project;

    private final transient List<Pattern> patterns;
    private final transient PsiClass targetInterface;

    private final transient JavaFactoryApiParsed apiInfo;
    private final transient Map<ReferenceFlag, String> generatedSources = new HashMap<>();


    private final transient List<JTextArea> resultAreas = new ArrayList<>();
    private final transient List<JButton> saveButtons = new ArrayList<>();
    private final transient List<JButton> showPromptButtons = new ArrayList<>();
    private final transient List<JButton> copyButtons = new ArrayList<>();
    private final transient List<JTextField> pathFields = new ArrayList<>();

    private JButton rollAgainButton; // 🔵 추가

    private boolean isRunning = false; // 🔵 실행중 여부 추적

    public PatternExecutionPreviewPanel(Project project, List<Pattern> patterns, JavaFactoryApiParsed apiInfo) {
        this.project = project;
        this.patterns = patterns;
        this.targetInterface = apiInfo.selfPsi();
        this.apiInfo = apiInfo;


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        rollAgainButton = new JButton("🔄 Roll Again");
        rollAgainButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rollAgainButton.addActionListener(e -> {
            if (isRunning) return; // 이미 실행중이면 무시
            rollAgainButton.setEnabled(false);
            resetPreviewState();
            generatedSources.clear(); // 🔵 롤할 때 반드시 초기화
            startSequentialPreview();
        });

        add(rollAgainButton);
        add(Box.createVerticalStrut(20));


        for (Pattern pattern : patterns) {
            add(createPatternPanel(pattern));
            add(Box.createVerticalStrut(15));
        }

        SwingUtilities.invokeLater(this::startSequentialPreview);
    }

    private void resetPreviewState() {
        for (int i = 0; i < patterns.size(); i++) {
            JTextArea area = resultAreas.get(i);
            JButton saveButton = saveButtons.get(i);
            JButton copyButton = copyButtons.get(i);
            JButton showPromptButton = showPromptButtons.get(i);

            area.setText("Waiting ...");
            saveButton.setEnabled(false);
            copyButton.setEnabled(false);
            showPromptButton.setEnabled(false);
        }
    }

    private JPanel createPatternPanel(Pattern pattern) {
        JPanel panel = createBasePanel();

        var showPromptButton = createShowPromptButton(pattern);
        panel.add(createTitleLabel(pattern));
        panel.add(createTypeLabel(pattern));

        panel.add(Box.createVerticalStrut(8));
        panel.add(showPromptButton);
        panel.add(Box.createVerticalStrut(8));

        JTextArea resultArea = createResultArea();
        resultArea.setText("Waiting ... ");
        resultAreas.add(resultArea);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton copyButton = createCopyButton(resultArea);
        JTextField pathField = createPathField(pattern);
        JButton saveButton = createSaveButton();

        saveButton.addActionListener(e -> {
            String pathText = pathField.getText().replaceFirst("^path:\\s*", "").trim(); // "path: ..." 제거
            if (pathText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❗ No Path to save", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String content = resultArea.getText();
            SaveFileOnPanel.saveContentToFile(project, this, pathText, content);
        });


        pathFields.add(pathField);

        showPromptButton.setEnabled(false);
        copyButton.setEnabled(false);
        saveButton.setEnabled(false);

        showPromptButtons.add(showPromptButton);
        copyButtons.add(copyButton);
        saveButtons.add(saveButton);

        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(5));
        panel.add(copyButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(pathField);
        panel.add(Box.createVerticalStrut(5));
        panel.add(saveButton);


        return panel;
    }

    private JButton createShowPromptButton(Pattern pattern) {
        JButton button = new JButton("📋 Show prompt");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> {
            String system;
            String user;
            try {
                system = PromptRenderUtils.showSystemPrompt(pattern.getSystemPromptPattern());
                user =
                        PromptRenderUtils.formatPromptMap(
                                PromptRenderUtils.renderUserPromptFromGenerated(project, pattern.getUserPromptContent(), apiInfo, generatedSources)
                        );
            } catch (IOException ioe) {
                throw new RuntimeException("Failed to build llm prompt .... ");
            }

            JTextArea systemArea = new JTextArea(system, 8, 50);
            systemArea.setLineWrap(true);
            systemArea.setWrapStyleWord(true);
            systemArea.setEditable(false);
            systemArea.setBorder(BorderFactory.createTitledBorder("System Prompt"));

            JTextArea userArea = new JTextArea(user, 8, 50);
            userArea.setLineWrap(true);
            userArea.setWrapStyleWord(true);
            userArea.setEditable(false);
            userArea.setBorder(BorderFactory.createTitledBorder("User Prompt"));

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(systemArea);
            panel.add(Box.createVerticalStrut(10));
            panel.add(userArea);

            JScrollPane scrollPane = new JScrollPane(panel);


            // 🔧 JDialog 사용
            JDialog dialog = new JDialog((Frame) null, "Show prompt", true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(scrollPane);
            dialog.setPreferredSize(new Dimension(800, 600)); // 초기 크기
            dialog.setResizable(true); // ✅ 크기 조절 가능하게 설정
            dialog.pack();
            dialog.setLocationRelativeTo(null); // 화면 중앙
            dialog.setVisible(true);
        });
        return button;
    }

    private void startSequentialPreview() {
        isRunning = true; // 🔵 시작할 때 true로
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i < patterns.size(); i++) {
                    Pattern pattern = patterns.get(i);
                    JTextArea area = resultAreas.get(i);

                    // UI 업데이트는 publish로
                    publish(i);

                    var sys = PromptRenderUtils.showSystemPrompt(pattern.getSystemPromptPattern());
                    String user =
                            null;
                    try {
                        user = PromptRenderUtils.formatPromptMap(
                                PromptRenderUtils.renderUserPromptFromGenerated(project, pattern.getUserPromptContent(), apiInfo, generatedSources)
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    // 실제 LLM 호출 (오래 걸리는 작업)
                    String generatedCode = MdCleaner.cleanJavaCode(callCodeGeneration(sys, user));
                    updateGeneratedSources(pattern, generatedCode);

                    int finalI = i;
                    SwingUtilities.invokeLater(() -> {
                        var saveButton = saveButtons.get(finalI);
                        var copyButton = copyButtons.get(finalI);
                        var showPromptButton = showPromptButtons.get(finalI);
                        var pathField = pathFields.get(finalI);

                        area.setText(generatedCode);

                        if (Boolean.TRUE.equals(canGuessPath(targetInterface, pattern))) {
                            String guessedPath = guessPath(targetInterface, pattern);
                            pathField.setText("path: " + guessedPath);
                            if(guessedPath != null && !guessedPath.isBlank())
                                saveButton.setEnabled(true);
                        } else {
                            pathField.setText("If the pattern has a GenerationType, a generation path can be suggested.");
                            saveButton.setEnabled(false);
                        }

                        copyButton.setEnabled(true);
                        showPromptButton.setEnabled(true);
                    });

                    try {
                        Thread.sleep(1000); // 약간 쉬어가기 (optional)
                    } catch (InterruptedException ignored) {
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // 중간 상태 업데이트 (ex: "Calling GPT..." 메시지 표시)
                for (Integer index : chunks) {
                    JTextArea area = resultAreas.get(index);
                    area.setText("Calling LLM GPT-4o ...");
                }
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    isRunning = false; // 🔵 종료할 때 false로
                    rollAgainButton.setEnabled(true);
                });
            }
        };

        worker.execute(); // 실제 비동기 시작
    }

    private void updateGeneratedSources(Pattern pattern, String generatedCode) {
        if (GenerationType.IMPLEMENTATION.equals(pattern.getGenerationType())) {
            generatedSources.put(ReferenceFlag.TARGET_DEFAULT_API_IMPL, generatedCode);
        } else if (GenerationType.FIXTURE.equals(pattern.getGenerationType())) {
            generatedSources.put(ReferenceFlag.REFERENCED_API_FIXTURE, generatedCode);
        }
    }

    private String callCodeGeneration(String sys, String user) {
        // 엄청나게 오래걸리는 작업 . 약 5~20초
        var promptRunner = DefaultPromptRunner.getInstance();
        var result = promptRunner.call(project, sys, user);
        return result;
    }

    private JPanel createBasePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JLabel createTitleLabel(Pattern pattern) {
        JLabel label = new JLabel("🔹 Pattern : " + pattern.getName());
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private JLabel createTypeLabel(Pattern pattern) {
        JLabel label = new JLabel("📘 GenerationType: " + pattern.getGenerationType());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return label;
    }

    private JTextArea createResultArea() {
        JTextArea resultArea = new JTextArea(8, 50);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        return resultArea;
    }

    private JButton createCopyButton(JTextArea resultArea) {
        JButton button = new JButton("📋 copy clipboard");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> {
            resultArea.selectAll();
            resultArea.copy();
        });
        return button;
    }

    private JTextField createPathField(Pattern pattern) {
        JTextField pathField = new JTextField("path: ");
        pathField.setEditable(false);
        pathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        pathField.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pathField;
    }

    private JButton createSaveButton() {
        JButton button = new JButton("💾 save on path");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private Boolean canGuessPath(PsiClass targetInterface, Pattern pattern) {
        if (targetInterface == null)
            return false;

        // 진짜 인터페이스인지 확인 해야 할까 ?
        var genType = pattern.getGenerationType();

        if (genType == null || genType.equals(GenerationType.NONE))
            return false;

        return true;
    }

    private String guessPath(PsiClass targetInterface, Pattern pattern) {
        if (pattern.getGenerationType().equals(GenerationType.IMPLEMENTATION)) {
            return PathGuesser.guessDefaultImplPathFromInterface(targetInterface);
        }
        if (pattern.getGenerationType().equals(GenerationType.TEST))
            return PathGuesser.guessTestPathFromInterface(targetInterface);
        if (pattern.getGenerationType().equals(GenerationType.FIXTURE))
            return PathGuesser.guessFixturePathFromInterface(targetInterface);

        return null;
    }
}