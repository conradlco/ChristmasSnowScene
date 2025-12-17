package org.conradlco.christmas.snowscene;

import org.conradlco.christmas.snowscene.ui.SnowSceneWindow;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Application::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        SnowSceneWindow snowSceneWindow = new SnowSceneWindow();
        snowSceneWindow.setVisible(true);
        snowSceneWindow.startAnimation();
    }
}
