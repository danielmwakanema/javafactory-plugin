package io.github.javafactoryplugindev.plugin.ui;

import com.intellij.ide.BrowserUtil;

import javax.swing.*;
import java.awt.*;


public class CodeGenerationGuidePanel extends BaseToolWindow {


    public CodeGenerationGuidePanel(Runnable backToLobby) {
        super("📘  How to Guide", backToLobby);
    }

    @Override
    public void initContent(JPanel content) {
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        // 🧩 중앙 항목 리스트 (CENTER)
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setOpaque(false);

        itemPanel.add(Box.createVerticalStrut(10));
        itemPanel.add(createItem(
                "📘 Introduction",
                "General overview of JavaFactory",
                "https://github.com/JavaFactoryPluginDev/javafactory-plugin/tree/master"
        ));
        itemPanel.add(Box.createVerticalStrut(10));

        itemPanel.add(createItem(
                "🔍 Collecting Referenced Classes",
                "How @JavaFactoryData/@Api are parsed",
                "https://github.com/JavaFactoryPluginDev/javafactory-plugin/blob/master/docs/crawl_java_files.md"
        ));
        itemPanel.add(Box.createVerticalStrut(10));

        itemPanel.add(createItem(
                "🧩 Managing Patterns",
                "Define system/user prompts to guide LLM",
                "https://github.com/JavaFactoryPluginDev/javafactory-plugin/blob/master/docs/patterns.md"
        ));
        itemPanel.add(Box.createVerticalStrut(10));

        itemPanel.add(createItem(
                "💡 Usage Examples",
                "End-to-end examples for generation",
                "https://github.com/JavaFactoryPluginDev/javafactory-plugin/blob/master/docs/usage_example.md"
        ));

        content.add(itemPanel, BorderLayout.CENTER);
    }

    private JPanel createItem(String title, String description, String url) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setFocusPainted(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN, 13f));


        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 여백 추가
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        button.add(textPanel, BorderLayout.CENTER);
        button.addActionListener(e -> BrowserUtil.browse(url));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(button, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // ✅ 최소 높이 50 보장, 내용에 따라 자동 조정
        Dimension pref = wrapper.getPreferredSize();
        pref.height = Math.max(pref.height, 60);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

        return wrapper;
    }
}