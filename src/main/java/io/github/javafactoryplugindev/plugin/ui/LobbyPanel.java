package io.github.javafactoryplugindev.plugin.ui;

import javax.swing.*;
import java.awt.*;



public class LobbyPanel extends JPanel {

    public LobbyPanel(JPanel rootPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(30)); // Top spacing

        JLabel title = new JLabel("🎯 JavaFactory Plugin");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);

        add(Box.createVerticalStrut(30));

        add(createMenuButton("🗂 Pattern Management", rootPanel, "PatternEditorPanel"));
        add(Box.createVerticalStrut(20));
        add(createMenuButton("🔧 Configure OpenAI Key", rootPanel, "Settings"));
        add(Box.createVerticalStrut(20));
        add(createMenuButton("📖 How to Use", rootPanel, "Guide"));

        add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text, JPanel rootPanel, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.setPreferredSize(new Dimension(250, 40));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false); // 눌렀을 때 테두리 없음
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); // 살짝 패딩
        button.addActionListener(e -> show(rootPanel, panelName));
        return button;
    }

    private void show(JPanel root, String name) {
        ((CardLayout) root.getLayout()).show(root, name);
    }
}