package io.github.javafactoryplugindev.plugin.ui.pattern;

import io.github.javafactoryplugindev.plugin.pattern.Pattern;
import io.github.javafactoryplugindev.plugin.pattern.PatternStorageService;
import io.github.javafactoryplugindev.plugin.ui.BaseToolWindow;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class PatternEditorPanel extends BaseToolWindow {

    private final JPanel rootPanel;

    private final transient Project project;

    public PatternEditorPanel(Project project, Runnable backAction, JPanel rootPanel) {
        super("🔧 JavaFactory Pattern Editor", backAction);
        this.rootPanel = rootPanel;
        this.project = project;
    }

    @Override
    public void initContent(JPanel content) {
        content.setLayout(new BorderLayout());
        content.add(createTopPanel(), BorderLayout.NORTH);
        content.add(createPatternListPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createButton = new JButton("+ Create new pattern");
        createButton.addActionListener(e -> {
            CardLayout layout = (CardLayout) rootPanel.getLayout();
            layout.show(rootPanel, "PatternCreatePanel");
        });
        topPanel.add(createButton);
        return topPanel;
    }

    private JScrollPane createPatternListPanel() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        List<Pattern> patterns = PatternStorageService.getInstance(project).getAllPatterns();

        for (Pattern pattern : patterns) {
            JPanel item = new JPanel(new BorderLayout());
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));


            JTextField nameLabel = new JTextField(pattern.getName());
            nameLabel.setEditable(true);
            nameLabel.setBorder(null);
            nameLabel.setBackground(null);
            nameLabel.setFont(new JLabel().getFont());
            nameLabel.setCaretColor(Color.DARK_GRAY);
            nameLabel.setOpaque(false);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            JButton editButton = new JButton("✏️ Edit");
            JButton deleteButton = new JButton("🗑 Delete");

            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this pattern?\nDeleted patterns cannot be recovered.",
                        "⚠️ Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    PatternStorageService.getInstance(project).deletePattern(pattern.getName());
                    refresh();
                }
            });

            editButton.addActionListener(e -> {
                PatternEditPanel editPanel = new PatternEditPanel(project, pattern, () -> {
                    CardLayout layout = (CardLayout) rootPanel.getLayout();
                    layout.show(rootPanel, "PatternEditorPanel");
                });
                rootPanel.add(editPanel, "PatternEditPanel_" + pattern.getName());
                CardLayout layout = (CardLayout) rootPanel.getLayout();
                layout.show(rootPanel, "PatternEditPanel_" + pattern.getName());
            });

            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            item.add(nameLabel, BorderLayout.WEST);
            item.add(buttonPanel, BorderLayout.EAST);
            listPanel.add(item);
        }

        // 🔵 스크롤 세팅 추가
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도 부드럽게

        return scrollPane;
    }
}
