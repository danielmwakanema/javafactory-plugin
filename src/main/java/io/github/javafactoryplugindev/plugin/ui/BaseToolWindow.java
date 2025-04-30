package io.github.javafactoryplugindev.plugin.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


public abstract class BaseToolWindow extends JPanel {

    private final JPanel rootPanel = new JPanel(new CardLayout());
    private JPanel contentPanel; // 🔹 본문 영역을 위한 필드

    public BaseToolWindow(String title, Runnable backAction) {
        setLayout(new BorderLayout());

        // 상단 타이틀 + 뒤로가기 버튼
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 약간 여백 추가

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> backAction.run());
        backButton.setFocusPainted(false);

        topPanel.add(titleLabel, BorderLayout.WEST);  // 타이틀은 왼쪽
        topPanel.add(backButton, BorderLayout.EAST);  // 버튼은 오른쪽

        this.add(topPanel, BorderLayout.NORTH);

        // 패널 진입 시 refresh 실행
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
    }

    public void refresh() {
        if (contentPanel != null) {
            this.remove(contentPanel); // 🔸 기존 본문 제거
        }

        contentPanel = new JPanel(new BorderLayout());
        initContent(contentPanel); // 🔸 본문 새로 구성
        this.add(contentPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    // 하위 클래스에서 실제 본문을 구성
    public abstract void initContent(JPanel content);
}