package io.github.javafactoryplugindev.plugin.toolWindow;


import com.intellij.openapi.diagnostic.Logger;
import io.github.javafactoryplugindev.plugin.ui.BaseToolWindow;
import io.github.javafactoryplugindev.plugin.ui.CodeGenerationGuidePanel;
import io.github.javafactoryplugindev.plugin.ui.LobbyPanel;
import io.github.javafactoryplugindev.plugin.ui.SettingsPanel;
import io.github.javafactoryplugindev.plugin.ui.pattern.PatternCreatePanel;
import io.github.javafactoryplugindev.plugin.ui.pattern.PatternEditorPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;


import javax.swing.JPanel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class MyToolWindowFactory implements ToolWindowFactory {

    private Logger log = Logger.getInstance(MyToolWindowFactory.class);

    private final Map<String, JPanel> panels = new HashMap<>();


    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        JPanel rootPanel = new JPanel(new CardLayout());

        // 진입 함수
        Runnable showLobby = () -> showPanel(rootPanel, "Lobby");

        // 패널 인스턴스 생성 및 등록
        panels.put("Lobby", new LobbyPanel(rootPanel));
        panels.put("Guide", new CodeGenerationGuidePanel(showLobby));
        panels.put("Settings", new SettingsPanel(project, showLobby));


        //
        panels.put("PatternEditorPanel", new PatternEditorPanel(project, showLobby, rootPanel));
        panels.put("PatternCreatePanel", new PatternCreatePanel(project, showLobby));
        // 각 패널을 rootPanel에 등록
        for (Map.Entry<String, JPanel> entry : panels.entrySet()) {
            rootPanel.add(entry.getValue(), entry.getKey());
        }

        // 최초 진입
        showPanel(rootPanel, "Lobby");

        Content content = ContentFactory.getInstance().createContent(rootPanel, null, false);
        toolWindow.getContentManager().addContent(content);
    }


    private void showPanel(JPanel rootPanel, String name) {
        JPanel panel = panels.get(name);
        log.info("try show pannel : " + name);
        if (panel instanceof BaseToolWindow basePanel) {
            basePanel.refresh(); // 패널이 BaseToolWindow라면 진입 시 refresh 실행
        }

        CardLayout layout = (CardLayout) rootPanel.getLayout();
        layout.show(rootPanel, name);
    }
}