package io.github.javafactoryplugindev.plugin.ui;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.net.URI;


public class LobbyPanel extends JPanel {


    public LobbyPanel(JPanel rootPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setOpaque(false);

        add(Box.createVerticalStrut(30)); // Top spacing

        // ✅ contentBox: 중앙에 고정폭 컨테이너
        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setOpaque(false);
        contentBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentBox.setMaximumSize(new Dimension(300, Integer.MAX_VALUE)); // 고정 폭 설정
        contentBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // title 2
        contentBox.add(createPluginTitlePanel(
                "/META-INF/pluginIcon.svg",
                "JavaFactory"
        ));

        // Title
        contentBox.add(Box.createVerticalStrut(20));

        contentBox.add(createMenuButton("🗂 Pattern Management", rootPanel, "PatternEditorPanel"));
        contentBox.add(Box.createVerticalStrut(15));
        contentBox.add(createMenuButton("🔧 Configure OpenAI Key", rootPanel, "Settings"));
        contentBox.add(Box.createVerticalStrut(15));
        contentBox.add(createMenuButton("📖 How to Use", rootPanel, "Guide"));
        contentBox.add(Box.createVerticalStrut(15));
        contentBox.add(createDiscussionButton());

        // 전체 패널에 중앙 배치
        add(contentBox);
        add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text, JPanel rootPanel, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(280, 40));
        button.setPreferredSize(new Dimension(280, 40));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT); // ✅ 텍스트 좌측 정렬
        button.addActionListener(e -> show(rootPanel, panelName));
        return button;
    }

    private JButton createDiscussionButton() {
        JButton button = new JButton("💬 Join the Discussion");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(280, 40));
        button.setPreferredSize(new Dimension(280, 40));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT); // ✅ 텍스트 좌측 정렬
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/JavaFactoryPluginDev/javafactory-plugin/discussions"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return button;
    }

    private JPanel createPluginTitlePanel(String iconPath, String titleText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(300, 60));

        // 아이콘
        Icon icon = IconLoader.getIcon(iconPath, getClass());
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0)); // 오른쪽에만 padding
        iconLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // 텍스트
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(titleLabel);
        return panel;
    }

    private void show(JPanel root, String name) {
        ((CardLayout) root.getLayout()).show(root, name);
    }
}