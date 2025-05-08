package io.github.javafactoryplugindev.plugin.ui.pattern;

import javax.swing.*;
import java.awt.*;

public class JScrolPaneUtils {
    public static String getAreaTxt(JScrollPane pane) {
        Component view = pane.getViewport().getView();
        if (view instanceof JTextArea area) {
            return area.getText();
        }
        return "";
    }

    public static void setAreaTxt(JScrollPane pane, String txt) {
        Component view = pane.getViewport().getView();
        if (view instanceof JTextArea area) {
            area.setText(txt);
        }
    }
}
