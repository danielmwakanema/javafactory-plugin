package io.github.javafactoryplugindev.plugin.ui;

import javax.swing.*;


public class CodeGenerationGuidePanel extends BaseToolWindow {

    public CodeGenerationGuidePanel(Runnable backToLobby) {
        super("📘 코드 생성 가이드", backToLobby);
    }

    @Override
    public void initContent(JPanel content) {
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(new JLabel("🧠 생성 규칙 및 사용 방법에 대한 가이드를 제공합니다."));
        content.add(Box.createVerticalStrut(10));
        content.add(new JButton("가이드 문서 열기"));
    }
}
