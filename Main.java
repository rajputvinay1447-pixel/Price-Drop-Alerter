package pricedrop;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pricedrop.ui.MainWindow;

public class Main {
    public static void main(String[] args) {
        // Enable high-quality rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                // Global dark defaults
                UIManager.put("OptionPane.background", new java.awt.Color(0x0d, 0x0f, 0x1a));
                UIManager.put("Panel.background",      new java.awt.Color(0x0d, 0x0f, 0x1a));
                UIManager.put("OptionPane.messageForeground", new java.awt.Color(0xee, 0xf0, 0xfa));
                UIManager.put("Button.background",     new java.awt.Color(0x16, 0x19, 0x29));
                UIManager.put("Button.foreground",     new java.awt.Color(0xee, 0xf0, 0xfa));
                UIManager.put("ComboBox.background",   new java.awt.Color(0x10, 0x13, 0x1e));
                UIManager.put("ComboBox.foreground",   new java.awt.Color(0x8b, 0x93, 0xbb));
                UIManager.put("ComboBox.selectionBackground", new java.awt.Color(0x1c, 0x20, 0x35));
                UIManager.put("ComboBox.selectionForeground", new java.awt.Color(0xee, 0xf0, 0xfa));
                UIManager.put("TextField.background",  new java.awt.Color(0x10, 0x13, 0x1e));
                UIManager.put("TextField.foreground",  new java.awt.Color(0xee, 0xf0, 0xfa));
                UIManager.put("TextField.caretForeground", new java.awt.Color(0x00, 0xd4, 0xff));
                UIManager.put("ScrollBar.background",  new java.awt.Color(0x0d, 0x0f, 0x1a));
                UIManager.put("ScrollBar.thumb",       new java.awt.Color(0x1c, 0x20, 0x35));
                UIManager.put("FileChooser.background",new java.awt.Color(0x0d, 0x0f, 0x1a));
            } catch (Exception ignored) {}

            new MainWindow();
        });
    }
}
