package io.github.javafactoryplugindev.plugin.ui.pattern;



import io.github.javafactoryplugindev.plugin.pattern.*;
import io.github.javafactoryplugindev.plugin.ui.BaseToolWindow;
import com.intellij.openapi.project.Project;


import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PatternCreatePanel extends BaseToolWindow {

    private final List<JPanel> flagRows = new ArrayList<>();
    private final List<JTextField> flagFieldList = new ArrayList<>();
    private final List<List<JCheckBox>> checkBoxList = new ArrayList<>();

    private final transient Project project;
    private JPanel userPromptPanel;


    private ButtonGroup generationTypeGroup;
    private JRadioButton noneButton, implButton, testButton, fixtureButton;

    public PatternCreatePanel(Project project, Runnable backAction) {
        super("🆕 Create New Pattern", backAction);
        this.project = project;
    }

    private JTextField nameField;
    private JTextArea goalArea;
    private JTextArea rulesArea;
    private JTextArea formatArea;
    private JTextArea exampleArea;

    @Override
    public void initContent(JPanel content) {
        content.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        content.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        JPanel panel = createMainPanel();
        outerPanel.add(panel, BorderLayout.NORTH);
        return outerPanel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JLabel title = new JLabel("🧩 Pattern Creation Panel");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title); //
        panel.add(Box.createVerticalStrut(10));

        JLabel description = new JLabel("Please modify the default values as needed.");
        description.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(description); // 🔥
        panel.add(Box.createVerticalStrut(10));

        panel.add(createNameInputSection());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createSystemPromptSection());
        panel.add(Box.createVerticalStrut(30));
        panel.add(createUserPromptSection());


        panel.add(Box.createHorizontalStrut(80));
        panel.add(createGenerationTypeSection());

        panel.add(Box.createHorizontalStrut(80));
        panel.add(createFooter());
        panel.add(Box.createHorizontalStrut(40));


        return panel;
    }


    private JLabel createFieldGuide(String fieldName, String guideText) {
        JLabel guide = new JLabel("📌 " + fieldName + ": " + guideText);
        guide.setFont(new Font("SansSerif", Font.ITALIC, 12));
        guide.setForeground(Color.GRAY);
        guide.setAlignmentX(Component.LEFT_ALIGNMENT);
        guide.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return guide;
    }


    private JPanel createNameInputSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Pattern name");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(nameLabel);
        section.add(nameField);

        return section;
    }

    private JPanel createSystemPromptSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sysLabel = new JLabel("📝 System Prompt Configuration");
        sysLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        goalArea = createTextArea("Goal");
        rulesArea = createTextArea("Rules");
        formatArea = createTextArea("Output Format");
        exampleArea = createTextArea("Output Example");

        goalArea.setText(PatternCreationPreview.goal());
        rulesArea.setText(PatternCreationPreview.rules());
        formatArea.setText(PatternCreationPreview.output());
        exampleArea.setText(PatternCreationPreview.example());

        JButton previewButton = createSystemPromptButton(project);
        previewButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(sysLabel);
        section.add(Box.createVerticalStrut(10));
        section.add(createFieldGuide("Goal", "Define the core objective briefly."));
        section.add(goalArea);

        section.add(Box.createVerticalStrut(10));
        section.add(createFieldGuide("Rules", "Define the rules and conventions for achieving the objective."));
        section.add(rulesArea);

        section.add(Box.createVerticalStrut(10));
        section.add(createFieldGuide("Output Format", "Specify output rules for code usage. Default values are recommended."));
        section.add(formatArea);

        section.add(Box.createVerticalStrut(10));
        section.add(createFieldGuide("Example", "Provide an example of the expected output. Strongly recommended"));
        section.add(exampleArea);

        section.add(previewButton);

        return section;
    }

    private JPanel createUserPromptSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("📦 User Prompt Configuration");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel("📝 Specify the contents for the user prompt. Source code from the classes will be inserted during generation.");
        description.setFont(new Font("SansSerif", Font.ITALIC, 12));
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        description.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));

        userPromptPanel = new JPanel();
        userPromptPanel.setLayout(new BoxLayout(userPromptPanel, BoxLayout.Y_AXIS));
        userPromptPanel.setBorder(BorderFactory.createTitledBorder("Bind Items and Contents"));
        userPromptPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addFlagButton = new JButton("+ Add Item");
        addFlagButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addFlagButton.addActionListener(e -> addFlagRow());

        addFlagRow("API_INTERFACE", List.of(ReferenceFlag.TARGET_API));
        addFlagRow("REFERENCED", List.of(ReferenceFlag.DATA, ReferenceFlag.OTHER_REFERENCED));

        JButton userPreviewButton = createUserPromptPreviewButton(project);
        userPreviewButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(userLabel);
        section.add(description);
        section.add(userPromptPanel);
        section.add(addFlagButton);
        section.add(userPreviewButton);

        return section;
    }

    private JTextArea createTextArea(String title) {
        JTextArea area = new JTextArea(5, 40);
        area.setBorder(BorderFactory.createTitledBorder(title));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setLineWrap(true); // 자동 줄바꿈
        area.setWrapStyleWord(true); // 단어 단위 줄바꿈
        area.setPreferredSize(new Dimension(0, 300));

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(0, 300)); // 스크롤 영역 크기
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 가로 스크롤은 비활성화

        return area;
    }

    private void addFlagRow(String defaultKey, List<ReferenceFlag> selectedFlags) {
        JPanel flagRow = new JPanel();
        flagRow.setLayout(new BoxLayout(flagRow, BoxLayout.Y_AXIS));
        flagRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel flagInputRow = new JPanel(new BorderLayout());

        JLabel leftLabel = new JLabel("Item:");
        JTextField flagField = new JTextField(defaultKey, 10); // 기본 key 세팅
        flagField.setMaximumSize(new Dimension(150, 30));
        JLabel rightLabel = new JLabel("Value:");
        JButton removeButton = new JButton("Delete");
        removeButton.addActionListener(e -> {
            int index = flagRows.indexOf(flagRow);
            flagRows.remove(index);
            flagFieldList.remove(index);
            checkBoxList.remove(index);
            userPromptPanel.remove(flagRow);
            userPromptPanel.revalidate();
            userPromptPanel.repaint();
        });

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.add(leftLabel);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(flagField);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(rightLabel);

        flagInputRow.add(leftPanel, BorderLayout.WEST);
        flagInputRow.add(removeButton, BorderLayout.EAST);

        JPanel checkPanel = new JPanel(new GridLayout(0, 3, 10, 5));
        List<JCheckBox> boxGroup = new ArrayList<>();
        for (ReferenceFlag rf : ReferenceFlag.values()) {
            JCheckBox cb = new JCheckBox(rf.name());
            if (selectedFlags.contains(rf)) { // 선택된 플래그 체크
                cb.setSelected(true);
            }
            checkPanel.add(cb);
            boxGroup.add(cb);
        }

        flagRow.add(flagInputRow);
        flagRow.add(checkPanel);

        flagRows.add(flagRow);
        flagFieldList.add(flagField);
        checkBoxList.add(boxGroup);
        userPromptPanel.add(flagRow);

        userPromptPanel.revalidate();
        userPromptPanel.repaint();
    }

    private void addFlagRow() {
        JPanel flagRow = new JPanel();
        flagRow.setLayout(new BoxLayout(flagRow, BoxLayout.Y_AXIS));
        flagRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel flagInputRow = new JPanel(new BorderLayout());

        JLabel leftLabel = new JLabel("Item:");
        JTextField flagField = new JTextField("NEW_ITEM", 10);
        flagField.setMaximumSize(new Dimension(150, 30));
        JLabel rightLabel = new JLabel("Value:");
        JButton removeButton = new JButton("Delete");
        removeButton.addActionListener(e -> {
            int index = flagRows.indexOf(flagRow);
            flagRows.remove(index);
            flagFieldList.remove(index);
            checkBoxList.remove(index);
            userPromptPanel.remove(flagRow);
            userPromptPanel.revalidate();
            userPromptPanel.repaint();
        });

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.add(leftLabel);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(flagField);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(rightLabel);

        flagInputRow.add(leftPanel, BorderLayout.WEST);
        flagInputRow.add(removeButton, BorderLayout.EAST);

        JPanel checkPanel = new JPanel(new GridLayout(0, 3, 10, 5));
        List<JCheckBox> boxGroup = new ArrayList<>();
        for (ReferenceFlag rf : ReferenceFlag.values()) {
            JCheckBox cb = new JCheckBox(rf.name());
            checkPanel.add(cb);
            boxGroup.add(cb);
        }

        flagRow.add(flagInputRow);
        flagRow.add(checkPanel);

        flagRows.add(flagRow);
        flagFieldList.add(flagField);
        checkBoxList.add(boxGroup);
        userPromptPanel.add(flagRow);

        userPromptPanel.revalidate();
        userPromptPanel.repaint();
    }

    private SystemPromptContent toSystemPromptContent() {
        return new SystemPromptContent(
                goalArea.getText().trim(),
                rulesArea.getText().trim(),
                formatArea.getText().trim(),
                exampleArea.getText().trim()
        );
    }

    private UserPromptContent toUserPromptContent() {
        List<UserPromptContent.UserPromptItem> items = new ArrayList<>();
        for (int i = 0; i < flagFieldList.size(); i++) {
            String key = flagFieldList.get(i).getText().trim();
            List<ReferenceFlag> flags = new ArrayList<>();
            for (JCheckBox cb : checkBoxList.get(i)) {
                if (cb.isSelected()) {
                    var refFlag = ReferenceFlag.valueOf(cb.getText().trim());
                    flags.add(refFlag);
                }
            }
            if (!key.isEmpty()) {
                UserPromptContent.UserPromptItem item = new UserPromptContent.UserPromptItem(key, flags);
                items.add(item);
            }
        }
        return new UserPromptContent(items);
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("💾 Save");
        saveButton.addActionListener(e -> {
            // 1. 필수 값 검증
            String name = nameField.getText().trim();
            SystemPromptContent sys = toSystemPromptContent();
            UserPromptContent user = toUserPromptContent();

            if (name.isBlank() || sys.getGoal().isBlank() || sys.getRules().isBlank()
                    || sys.getOutputFormat().isBlank() || sys.getOutputExample().isBlank()) {
                JOptionPane.showMessageDialog(this, "Please complete all system prompt fields.", "⚠️ Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (user.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "User prompt items cannot be empty.", "⚠️ Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            var alreadyExist = PatternStorageService.getInstance(project).findByName(name);
            if (alreadyExist != null) {
                JOptionPane.showMessageDialog(this, "A pattern with the same name already exists. Please use a different name.", "⚠️ Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. 각 항목에 내용이 없는 경우
            for (var entry : user.getItems()) {
                if (entry.getFlags().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Some items have no assigned values\nPlease select the necessary flags or remove the item.",
                            "⚠️ Missing Content", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // 3. 중복 항목 확인
            Set<String> allFlags = new HashSet<>();
            for (UserPromptContent.UserPromptItem item : user.getItems()) {
                for (var flag : item.getFlags()) {
                    if (!allFlags.add(flag.name())) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "[" + flag + "] Some source values are assigned to multiple items. Do you still want to save?",
                                "⚠️ Duplicate Check", JOptionPane.YES_NO_OPTION);
                        if (confirm != JOptionPane.YES_OPTION) {
                            return;
                        }
                        break;
                    }
                }
            }

            // 4. generationType
            if (getSelectedGenerationType() == GenerationType.IMPLEMENTATION && !hasInterfaceFlag()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "This pattern targets IMPLEMENTATION generation,\n" +
                                "but no INTERFACE item is specified in the user prompt\n This may cause issues during reference resolution.\n" +
                                "Please explicitly define name: INTERFACE, value: TARGET_API \n" +
                                "Do you still want to save?",
                        "⚠️ No API Value", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            if ((getSelectedGenerationType() == GenerationType.TEST || getSelectedGenerationType() == GenerationType.FIXTURE) && !hasImplementationFlag()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "This pattern targets" + getSelectedGenerationType().name() + "generation, but no IMPLEMENTATION item is specified in the user prompt.\n This may cause issues during reference resolution\n" +
                                "Please explicitly define name: IMPLEMENTATION, value: TARGET_API \n" +
                                "Do you still want to save?",
                        "⚠️ NO Implementation value", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            // 저장
            savePattern(project, name, sys, user, getSelectedGenerationType());
        });

        footer.add(saveButton);
        return footer;
    }

    private void savePattern(Project project, String name, SystemPromptContent sys, UserPromptContent user, GenerationType generationType) {
        name = name.trim();
        try {
            Pattern.validatePatternName(name);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Pattern name must contain only lowercase letters (a-z) and underscores (_)", "⚠️ Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Pattern pattern = new Pattern(name, sys, user, generationType);

        PatternStorageService.getInstance(project).savePattern(pattern);
        JOptionPane.showMessageDialog(this, "Pattern saved successfully.", "✅ Saved", JOptionPane.INFORMATION_MESSAGE);
    }


    private GenerationType getSelectedGenerationType() {
        if (implButton.isSelected()) return GenerationType.IMPLEMENTATION;
        if (testButton.isSelected()) return GenerationType.TEST;
        if (fixtureButton.isSelected()) return GenerationType.FIXTURE;
        return GenerationType.NONE;
    }

    private boolean hasInterfaceFlag() {
        return flagFieldList.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase("INTERFACE"));
    }

    private boolean hasImplementationFlag() {
        return flagFieldList.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase("IMPLEMENTATION"));
    }

    // ✅ 버튼 테스트용 샘플 메서드들
    private JButton createSystemPromptButton(Project project) {
        JButton btn = new javax.swing.JButton("🔍 System Prompt");

        btn.addActionListener(e -> {
            String preview = PromptRenderUtils.showSystemPrompt(toSystemPromptContent());
            JTextArea textArea = new javax.swing.JTextArea(preview);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(600, 500));
            JOptionPane.showMessageDialog(null, scrollPane, "🔍 Show System Prompt", javax.swing.JOptionPane.INFORMATION_MESSAGE);

        });
        return btn;
    }

    private JButton createUserPromptPreviewButton(Project project) {
        JButton btn = new JButton("🔍 User Prompt");
        btn.addActionListener(e -> {
            String preview = PromptRenderUtils.showUserPromptPreview(toUserPromptContent());
            JTextArea textArea = new JTextArea(preview);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(600, 500));
            JOptionPane.showMessageDialog(null, scrollPane, "🔍 Show User Prompt", javax.swing.JOptionPane.INFORMATION_MESSAGE);

        });
        return btn;
    }

    private JPanel createGenerationTypeSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("🌟 Generation Type");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel guide = new JLabel("<html> What type of code will this pattern generate? (<br>"
                + " (e.g., interface implementation, test class, fixture class) <br>"
                + "Select the intended purpose below<br><br>"
                + "Reference information will be automatically collected during sequential execution.</html>");
        guide.setFont(new Font("SansSerif", Font.PLAIN, 12));
        guide.setAlignmentX(Component.LEFT_ALIGNMENT);
        guide.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        generationTypeGroup = new ButtonGroup();
        noneButton = new JRadioButton("NONE");
        implButton = new JRadioButton("IMPLEMENTATION");
        testButton = new JRadioButton("TEST");
        fixtureButton = new JRadioButton("FIXTURE");

        generationTypeGroup.add(noneButton);
        generationTypeGroup.add(implButton);
        generationTypeGroup.add(testButton);
        generationTypeGroup.add(fixtureButton);
        noneButton.setSelected(true);

        section.add(label);
        section.add(guide);
        section.add(noneButton);
        section.add(implButton);
        section.add(testButton);
        section.add(fixtureButton);

        return section;
    }


}



