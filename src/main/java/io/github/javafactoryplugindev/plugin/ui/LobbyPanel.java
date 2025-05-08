package io.github.javafactoryplugindev.plugin.ui;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.net.URI;


public class LobbyPanel extends JPanel {

    public LobbyPanel(JPanel rootPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(30)); // Top spacing
        JLabel title = new JLabel("JavaFactory Plugin");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(Box.createHorizontalStrut(8)); // 아이콘과 제목 사이 여백
        titlePanel.add(title);

        titlePanel.setMaximumSize(new Dimension(250, 60));

        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(20)); // 위쪽 여백
        add(titlePanel);
        add(Box.createVerticalStrut(20)); // 아래쪽 여백

        add(createMenuButton("🗂 Pattern Management", rootPanel, "PatternEditorPanel"));
        add(Box.createVerticalStrut(20));
        add(createMenuButton("🔧 Configure OpenAI Key", rootPanel, "Settings"));
        add(Box.createVerticalStrut(20));
        add(createMenuButton("📖 How to Use", rootPanel, "Guide"));
        add(Box.createVerticalStrut(20));

        add(createDiscussionButton()); // 👈 Discussion 버튼 추가

        add(Box.createVerticalGlue());
    }

    private JButton createMenuButton(String text, JPanel rootPanel, String panelName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.setPreferredSize(new Dimension(250, 40));
        button.setRolloverEnabled(true); // hover 상태 감지 활성화
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false); // 눌렀을 때 테두리 없음
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); // 살짝 패딩
        button.addActionListener(e -> show(rootPanel, panelName));
        return button;
    }

    private JButton createDiscussionButton() {
        JButton button = new JButton("💬 Join the Discussion");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.setPreferredSize(new Dimension(250, 40));
        button.setRolloverEnabled(true); // hover 상태 감지 활성화
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/JavaFactoryPluginDev/javafactory-plugin/discussions"));
            } catch (Exception ex) {
                ex.printStackTrace(); // 혹은 사용자에게 에러 메시지 표시
            }
        });

        return button;
    }

    private void show(JPanel root, String name) {
        ((CardLayout) root.getLayout()).show(root, name);
    }
}