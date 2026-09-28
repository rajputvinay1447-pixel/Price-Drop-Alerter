package pricedrop.util;

import java.awt.Color;
import java.awt.Font;

public class Theme {
    // Dark theme colors
    public static Color BG       = new Color(0x07080f);
    public static Color BG2      = new Color(0x0d0f1a);
    public static Color BG3      = new Color(0x111422);
    public static Color BG4      = new Color(0x161929);
    public static Color BG5      = new Color(0x1c2035);
    public static Color SIDEBAR  = new Color(0x09, 0x0a, 0x11);
    public static Color TOPBAR   = new Color(0x0a, 0x0c, 0x14);
    public static Color CARD     = new Color(0x0d, 0x0f, 0x1a);
    public static Color BORDER   = new Color(0xff, 0xff, 0xff, 15);
    public static Color BORDER2  = new Color(0xff, 0xff, 0xff, 25);
    public static Color INPUT    = new Color(0x10, 0x13, 0x1e);

    public static Color TEXT     = new Color(0xeef0fa);
    public static Color TEXT2    = new Color(0x8b93bb);
    public static Color TEXT3    = new Color(0x4a5070);

    // Accent colors
    public static final Color CYAN   = new Color(0x00d4ff);
    public static final Color BLUE   = new Color(0x4f9eff);
    public static final Color GREEN  = new Color(0x00e676);
    public static final Color RED    = new Color(0xff4b6a);
    public static final Color ORANGE = new Color(0xff9800);
    public static final Color PURPLE = new Color(0xb06dff);
    public static final Color PINK   = new Color(0xff3f8e);

    // Platform colors
    public static final Color AMAZON   = new Color(0xff9900);
    public static final Color FLIPKART = new Color(0x2874f0);
    public static final Color MYNTRA   = new Color(0xff3f6c);

    // Transparent variants
    public static Color cyan10()   { return new Color(0x00, 0xd4, 0xff, 25); }
    public static Color green10()  { return new Color(0x00, 0xe6, 0x76, 25); }
    public static Color red10()    { return new Color(0xff, 0x4b, 0x6a, 25); }
    public static Color orange10() { return new Color(0xff, 0x98, 0x00, 25); }
    public static Color purple10() { return new Color(0xb0, 0x6d, 0xff, 25); }
    public static Color amazon10() { return new Color(0xff, 0x99, 0x00, 25); }
    public static Color flipkart10(){ return new Color(0x28, 0x74, 0xf0, 25); }
    public static Color myntra10() { return new Color(0xff, 0x3f, 0x6c, 25); }

    public static Font FONT_TITLE(int size) {
        return new Font("SansSerif", Font.BOLD, size);
    }
    public static Font FONT_MONO(int size) {
        return new Font("Monospaced", Font.PLAIN, size);
    }
    public static Font FONT_BODY(int size) {
        return new Font("SansSerif", Font.PLAIN, size);
    }
    public static Font FONT_BOLD(int size) {
        return new Font("SansSerif", Font.BOLD, size);
    }

    public static Color getCategoryColor(String cat) {
        switch (cat) {
            case "Purchase":
                return BLUE;
            case "Food":
                return ORANGE;
            case "Transport":
                return CYAN;
            case "Bill":
                return PURPLE;
            case "Entertainment":
                return GREEN;
            case "Healthcare":
                return RED;
            case "Education":
                return AMAZON;
            case "Savings":
                return GREEN;
            default:
                return TEXT2;
        }
    }
}
