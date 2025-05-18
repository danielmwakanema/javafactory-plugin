package io.github.javafactoryplugindev.plugin.ui.pattern;


import io.github.javafactoryplugindev.plugin.ui.BaseToolWindow;
import com.intellij.openapi.project.Project;
import io.github.javafactoryplugindev.plugin.pattern.*;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PatternEditPanel extends BaseToolWindow {

    private final transient Project project;
    private final transient Pattern pattern;

    private final List<JPanel> flagRows = new ArrayList<>();
    private final List<JTextField> flagFieldList = new ArrayList<>();
    private final List<List<JCheckBox>> checkBoxList = new ArrayList<>();
    private JPanel userPromptPanel;

    private JTextField nameField;
    private JTextArea goalArea;
    private JTextArea rulesArea;
    private JTextArea formatArea;
    private JTextArea exampleArea;

    private ButtonGroup generationTypeGroup;
    private JRadioButton noneButton, implButton, testButton, fixtureButton;

    public PatternEditPanel(Project project, Pattern pattern, Runnable backAction) {
        super("✏️ Edit  Pattern - " + pattern.getName(), backAction);
        this.project = project;
        this.pattern = pattern;
    }

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
        JPanel footer = createFooterForEdit();
        outerPanel.add(panel, BorderLayout.NORTH);
        outerPanel.add(footer, BorderLayout.SOUTH);
        return outerPanel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);


        panel.add(createNameInputSection());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createSystemPromptSection());
        panel.add(Box.createVerticalStrut(30));
        panel.add(createUserPromptSection());
        panel.add(Box.createVerticalStrut(30));
        panel.add(createGenerationTypeSection());

        return panel;
    }

    private JPanel createNameInputSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Pattern");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField = new JTextField();
        nameField.setText(pattern.getName());
        nameField.setEnabled(false);
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(nameLabel);
        section.add(nameField);

        return section;
    }

    private JPanel createSystemPromptSection() {
        SystemPromptContent sys = pattern.getSystemPromptPattern();

        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sysLabel = new JLabel("🧐 System Prompt Configuration");
        sysLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        goalArea = createTextAreaOnly("Goal", sys.getGoal());
        rulesArea = createTextAreaOnly("Rules", sys.getRules());
        formatArea = createTextAreaOnly("Format", sys.getOutputFormat());
        exampleArea = createTextAreaOnly("Example", sys.getOutputExample());

        section.add(sysLabel);
        section.add(wrapWithScrollPane(goalArea));
        section.add(wrapWithScrollPane(rulesArea));
        section.add(wrapWithScrollPane(formatArea));
        section.add(wrapWithScrollPane(exampleArea));


        return section;
    }

    private JPanel createUserPromptSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("📆 User Prompt Configuration");
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userPromptPanel = new JPanel();
        userPromptPanel.setLayout(new BoxLayout(userPromptPanel, BoxLayout.Y_AXIS));
        userPromptPanel.setBorder(BorderFactory.createTitledBorder("Bind Items and Contents"));
        userPromptPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (UserPromptContent.UserPromptItem item : pattern.getUserPromptContent().getItems()) {
            List<String> flags = new ArrayList<>();
            if (item.getFlags() != null)
                flags = item.getFlags().stream().map(e -> e.name()).toList();
            addFlagRow(item.getKey(), flags);
        }

        JButton addFlagButton = new JButton("+ Add Item");
        addFlagButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addFlagButton.addActionListener(e -> addFlagRow());

        section.add(userLabel);
        section.add(userPromptPanel);
        section.add(addFlagButton);

        return section;
    }

    private void addFlagRow() {
        addFlagRow("NEW_KEY", new ArrayList<>());
    }


    private JTextArea createTextAreaOnly(String title, String value) {
        JTextArea area = new JTextArea(5, 40);
        area.setBorder(BorderFactory.createTitledBorder(title));
        area.setText(value);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setLineWrap(true); // 자동 줄바꿈
        area.setWrapStyleWord(true); // 단어 단위 줄바꿈
        area.setCaretPosition(0); // 커서를 맨 앞으로
        return area;
    }

    private JScrollPane wrapWithScrollPane(JTextArea area) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private void addFlagRow(String key, List<String> flags) {
        JPanel flagRow = new JPanel();
        flagRow.setLayout(new BoxLayout(flagRow, BoxLayout.Y_AXIS));
        flagRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel flagInputRow = new JPanel(new BorderLayout());

        JLabel leftLabel = new JLabel("Item:");
        JTextField flagField = new JTextField(key, 10);
        flagField.setMaximumSize(new Dimension(150, 30));
        flagField.setEditable(true);
        JLabel rightLabel = new JLabel("Value:");

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.add(leftLabel);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(flagField);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(rightLabel);

        flagInputRow.add(leftPanel, BorderLayout.WEST);

        JPanel checkPanel = new JPanel(new GridLayout(0, 3, 10, 5));
        List<JCheckBox> boxGroup = new ArrayList<>();
        for (ReferenceFlag rf : ReferenceFlag.values()) {
            JCheckBox cb = new JCheckBox(rf.name());
            if (flags.contains(rf.name())) cb.setSelected(true);
            cb.setEnabled(true);
            checkPanel.add(cb);
            boxGroup.add(cb);
        }


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


        flagRow.add(flagInputRow);
        flagRow.add(checkPanel);

        flagRows.add(flagRow);
        flagFieldList.add(flagField);
        checkBoxList.add(boxGroup);
        userPromptPanel.add(flagRow);


        flagInputRow.add(removeButton, BorderLayout.EAST);


        userPromptPanel.revalidate();
        userPromptPanel.repaint();
    }


    private JPanel createFooterForEdit() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("💾 Edit");

        updateButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            SystemPromptContent sys = toSystemPromptContent();
            UserPromptContent user = toUserPromptContent();

            if (name.isBlank() || sys.getGoal().isBlank() || sys.getRules().isBlank()
                    || sys.getOutputFormat().isBlank() || sys.getOutputExample().isBlank()) {
                JOptionPane.showMessageDialog(this, "Please complete all system prompt fields..", "⚠️ Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (user.getItems().isEmpty()) {
                JOptionPane.showMessageDialog(this, "User prompt items cannot be empty.", "⚠️ Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 같은 이름인데 다른 객체인 경우만 중복 검사


            for (var entry : user.getItems()) {
                if (entry.getFlags().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Some items have no assigned values. \nPlease select the necessary flags or remove the item.",
                            "⚠️ Missing Content", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            Set<String> allFlags = new HashSet<>();
            for (UserPromptContent.UserPromptItem item : user.getItems()) {
                for (var flag : item.getFlags()) {
                    if (!allFlags.add(flag.name())) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "[" + flag + "] Some flags are assigned to multiple items. Do you still want to save?",
                                "⚠️ 중복 확인", JOptionPane.YES_NO_OPTION);
                        if (confirm != JOptionPane.YES_OPTION) {
                            return;
                        }
                        break;
                    }
                }
            }


            if (getSelectedGenerationType() == GenerationType.IMPLEMENTATION && !hasInterfaceFlag(user)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "This pattern targets IMPLEMENTATION generation,\n" +
                                "but no INTERFACE item is specified in the user prompt\n This may cause issues during reference resolution.\n" +
                                "Please explicitly define name: INTERFACE, value: TARGET_API \n" +
                                "Do you still want to save?",
                        "⚠️ NO API INTERFACE ", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            if ((getSelectedGenerationType() == GenerationType.TEST || getSelectedGenerationType() == GenerationType.FIXTURE) && !hasImplementationFlag(user)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "This pattern targets" + getSelectedGenerationType().name() + "generation, but no IMPLEMENTATION item is specified in the user prompt.\n This may cause issues during reference resolution\n" +
                                "Please explicitly define name: IMPLEMENTATION, value: TARGET_API \n" +
                                "Do you still want to save?",
                        "⚠️ NO Implementation value", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }


            var originalPattern = PatternStorageService.getInstance(project).findByName(pattern.getName());
            // 업데이트
            originalPattern.setSystemPromptPattern(sys);
            originalPattern.setUserPromptContent(user);
            originalPattern.setGenerationType(getSelectedGenerationType());

            PatternStorageService.getInstance(project).savePattern(originalPattern);

            JOptionPane.showMessageDialog(this, "Pattern saved successfully.", "✅ Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        footer.add(updateButton);
        return footer;
    }

    private GenerationType getSelectedGenerationType() {
        if (implButton.isSelected()) return GenerationType.IMPLEMENTATION;
        if (testButton.isSelected()) return GenerationType.TEST;
        if (fixtureButton.isSelected()) return GenerationType.FIXTURE;
        return GenerationType.NONE;
    }

    private boolean hasInterfaceFlag(UserPromptContent user) {
        Set<ReferenceFlag> flags = new HashSet<>();
        for (UserPromptContent.UserPromptItem item : user.getItems()) {
            flags.addAll(item.getFlags());
        }

        return flags.contains(ReferenceFlag.TARGET_API);

    }

    private boolean hasImplementationFlag(UserPromptContent user) {
        Set<ReferenceFlag> flags = new HashSet<>();

        for (UserPromptContent.UserPromptItem item : user.getItems()) {
            flags.addAll(item.getFlags());
        }
        return flags.contains(ReferenceFlag.TARGET_DEFAULT_API_IMPL);
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
        // 기존 패턴 정보에서 generation type 자동 선택
        GenerationType previousType = pattern.getGenerationType();
        if (previousType == null) {
            noneButton.setSelected(true);
        } else {
            switch (previousType) {
                case IMPLEMENTATION -> implButton.setSelected(true);
                case TEST -> testButton.setSelected(true);
                case FIXTURE -> fixtureButton.setSelected(true);
                default -> noneButton.setSelected(true);
            }
        }

        section.add(label);
        section.add(guide);
        section.add(noneButton);
        section.add(implButton);
        section.add(testButton);
        section.add(fixtureButton);

        return section;
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

            var flag = flagFieldList.get(i);
            if (!flag.isValid())
                continue;

            String key = flag.getText().trim();
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


}
