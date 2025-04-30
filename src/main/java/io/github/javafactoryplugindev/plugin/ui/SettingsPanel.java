package io.github.javafactoryplugindev.plugin.ui;


import io.github.javafactoryplugindev.plugin.openai.OpenAiKeyChecker;
import io.github.javafactoryplugindev.plugin.openai.storage.OpenAiKeyStorage;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends BaseToolWindow {

    private final transient Project project;

    private final transient OpenAiKeyStorage keyStorage;

    private JPasswordField keyField;

    public SettingsPanel(Project project, Runnable backToLobby) {
        super("OpenAi key Settings", backToLobby);
        this.project = project;
        this.keyStorage = OpenAiKeyStorage.getInstance(project);
    }

    @Override
    public void initContent(JPanel content) {
        content.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        content.add(scrollPane);

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
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel headerPanel = createHeaderPanel();
        headerPanel.add(Box.createVerticalStrut(50));
        panel.add(headerPanel);

        var keyInputPanel = createKeyInputPanel();
        keyInputPanel.add(Box.createVerticalStrut(50));
        panel.add(keyInputPanel);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Register your OpenAI Key");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel description1 = new JLabel("📝 You need to register your OpenAI key to generate code. This plugin uses the GPT-4o model.");
        description1.setFont(new Font("SansSerif", Font.PLAIN, 13));
        description1.setAlignmentX(LEFT_ALIGNMENT);

        JLabel description2 = new JLabel("🔒 Your key is encoded and saved securely using IntelliJ PersistentStateComponent.");
        description2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        description2.setAlignmentX(LEFT_ALIGNMENT);

        JLabel description3 = new JLabel("🚫 Make sure to git ignore the ./idea folder to protect your key!");
        description3.setFont(new Font("SansSerif", Font.BOLD, 13));
        description3.setForeground(new Color(200, 0, 0)); // 강조 (빨간 계열 색)
        description3.setAlignmentX(LEFT_ALIGNMENT);

        // ➡️ Key 상태 표시
        var keyStorage = OpenAiKeyStorage.getInstance(project);
        String savedKey = keyStorage.getDecodedKey();

        JLabel keyStatusLabel;
        if (savedKey != null && !savedKey.isBlank()) {
            keyStatusLabel = new JLabel("✅ Key is registered.");
            keyStatusLabel.setForeground(new Color(0, 128, 0)); // 초록색
        } else {
            keyStatusLabel = new JLabel("❌ No key registered.");
            keyStatusLabel.setForeground(Color.RED);
        }
        keyStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        keyStatusLabel.setAlignmentX(LEFT_ALIGNMENT);

        section.add(nameLabel);
        section.add(Box.createVerticalStrut(25));   // 🔵 제목과 내용 사이 크게 (25px)

        section.add(description1);
        section.add(Box.createVerticalStrut(8));    // ⚪️ 내용 간의 작은 간격 (8px)

        section.add(description2);
        section.add(Box.createVerticalStrut(8));    // ⚪️ 내용 간의 작은 간격 (8px)

        section.add(description3);
        section.add(Box.createVerticalStrut(15)); // 마지막 문장과 상태표시 간 살짝 긴 간격

        section.add(keyStatusLabel); // ➡️ 추가된 부분

        section.add(Box.createVerticalStrut(10)); // 하단 마무리 여백
        return section;
    }


    private JPanel createKeyInputPanel() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Register key");
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        keyField = new JPasswordField(); // 🔥 JPasswordField로 변경
        keyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        keyField.setAlignmentX(LEFT_ALIGNMENT);

        var keyStorage = OpenAiKeyStorage.getInstance(project);
        String savedKey = keyStorage.getDecodedKey();
        if (savedKey != null && !savedKey.isBlank()) {
            keyField.setText(savedKey); // 저장된 키를 그대로 넣음 (보이진 않음)
        }

        JButton saveButton = new JButton("💾 Save Key");
        saveButton.setAlignmentX(LEFT_ALIGNMENT);
        saveButton.addActionListener(e -> saveKey());


        JButton testButton = new JButton("🔌 Test Connection");
        testButton.setAlignmentX(LEFT_ALIGNMENT);
        testButton.addActionListener(e -> testConnection());

        section.add(nameLabel);
        section.add(Box.createVerticalStrut(5)); // 간격 추가
        section.add(keyField);
        section.add(Box.createVerticalStrut(5));
        section.add(saveButton);

        section.add(Box.createVerticalStrut(5));
        section.add(testButton);
        return section;
    }



    private void saveKey() {
        String rawKey = new String(keyField.getPassword()).trim();
        if (rawKey.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a valid OpenAI Key.", "⚠️ Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        keyStorage.saveKey(rawKey);
        JOptionPane.showMessageDialog(null, "Key saved successfully!", "✅ Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void testConnection() {
        boolean connected = OpenAiKeyChecker.canConnect(project);

        if (connected) {
            JOptionPane.showMessageDialog(null, "✅ Connection successful!", "Connection Test", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "❌ Connection failed.\nPlease check your OpenAI Key.", "Connection Test", JOptionPane.ERROR_MESSAGE);
        }
    }

}

