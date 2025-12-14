package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;

/**
 * Theme utility for consistent UI styling
 * 
 * @author 27066
 */
public class Theme {
    // Background colors - High contrast for readability
    public static final Color BACKGROUND_LIGHT = new Color(248, 249, 250); // Very light gray
    public static final Color BACKGROUND_WHITE = Color.WHITE;
    public static final Color SIDEBAR_BG = new Color(30, 41, 59); // Dark slate for sidebar
    
    // Primary colors - Using sidebar dark color as main system color
    public static final Color PRIMARY = new Color(30, 41, 59); // Dark slate - same as sidebar (#1E293B)
    public static final Color PRIMARY_DARK = new Color(15, 23, 42); // Darker slate (#0F172A)
    public static final Color PRIMARY_LIGHT = new Color(51, 65, 85); // Lighter slate (#334155)
    public static final Color PRIMARY_COLOR = PRIMARY; // Alias
    
    // Accent colors - High contrast
    public static final Color ACCENT = new Color(249, 115, 22); // Bright orange (#F97316)
    public static final Color SUCCESS = new Color(34, 197, 94); // Bright green (#22C55E)
    public static final Color SUCCESS_COLOR = SUCCESS; // Alias
    public static final Color WARNING = new Color(251, 191, 36); // Bright yellow (#FBBF24)
    public static final Color WARNING_COLOR = WARNING; // Alias
    public static final Color ERROR = new Color(239, 68, 68); // Bright red (#EF4444)
    public static final Color DANGER = ERROR; // Alias
    public static final Color SECONDARY_COLOR = PRIMARY_LIGHT; // Alias
    
    // Text colors - High contrast for readability
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42); // Almost black (#0F172A)
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105); // Dark gray (#475569)
    public static final Color TEXT_LIGHT = new Color(148, 163, 184); // Medium gray (#94A3B8)
    public static final Color TEXT_WHITE = Color.WHITE; // White text for dark backgrounds
    public static final Color TEXT_ON_DARK = new Color(241, 245, 249); // Light text for dark backgrounds
    
    // Border colors
    public static final Color BORDER_LIGHT = new Color(200, 200, 200);
    public static final Color BORDER_MEDIUM = new Color(150, 150, 150);
    
    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    // Emoji-capable font (for icons)
    private static Font getEmojiFont(int size) {
        // Try to use fonts that support emojis, in order of preference
        String[] emojiFonts = {
            "Segoe UI Emoji",      // Windows 10+ default emoji font (best for Windows 11)
            "Segoe UI Symbol",     // Windows symbol font  
            "Apple Color Emoji",   // macOS
            "Noto Color Emoji",    // Linux/Android
            "Segoe UI"             // Fallback
        };
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        // Try each font in order
        for (String fontName : emojiFonts) {
            // Check if font is available
            for (String available : availableFonts) {
                if (available.equals(fontName)) {
                    try {
                        Font font = new Font(fontName, Font.PLAIN, size);
                        // Verify font can actually be used
                        if (font != null && font.getFamily() != null) {
                            return font;
                        }
                    } catch (Exception e) {
                        // Continue to next font
                        break;
                    }
                }
            }
        }
        
        // Final fallback: Try Segoe UI Emoji directly (Windows 11 should have this)
        // Java's font system may allow this even if not in the available fonts list
        try {
            Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, size);
            // Test if the font actually works by checking its name
            if (emojiFont != null) {
                return emojiFont;
            }
        } catch (Exception e) {
            // Continue to next fallback
        }
        
        // Last resort: Use Segoe UI Symbol (usually available on Windows)
        try {
            Font symbolFont = new Font("Segoe UI Symbol", Font.PLAIN, size);
            if (symbolFont != null) {
                return symbolFont;
            }
        } catch (Exception e) {
            // Final fallback
        }
        
        // Ultimate fallback: system default
        return new Font(Font.SANS_SERIF, Font.PLAIN, size);
    }
    
    // Font getters
    public static Font getTitleFont() {
        return TITLE_FONT;
    }
    
    public static Font getHeadingFont() {
        return HEADING_FONT;
    }
    
    public static Font getSubheadingFont() {
        return SUBHEADING_FONT;
    }
    
    public static Font getBodyFont() {
        return BODY_FONT;
    }
    
    /**
     * Get a font that supports emoji characters for icons
     */
    public static Font getIconFont(int size) {
        return getEmojiFont(size);
    }
    
    // Panel creation methods
    public static JPanel createGradientPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY,
                    0, getHeight(), PRIMARY_DARK
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        return panel;
    }
    
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }
    
    // Text field creation
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(BODY_FONT);
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(BACKGROUND_WHITE);
        return field;
    }
    
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(BODY_FONT);
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(BACKGROUND_WHITE);
        return field;
    }
    
    public static JTextArea createStyledTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setFont(BODY_FONT);
        area.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        area.setBackground(BACKGROUND_WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        combo.setFont(BODY_FONT);
        combo.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setBackground(BACKGROUND_WHITE);
        return combo;
    }
    
    // Button creation
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SUBHEADING_FONT);
        button.setBackground(PRIMARY);
        button.setForeground(Theme.TEXT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_LIGHT);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY);
            }
        });
        return button;
    }
    
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SUBHEADING_FONT);
        button.setBackground(BACKGROUND_WHITE);
        button.setForeground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(PRIMARY, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(241, 245, 249)); // Light gray hover
                button.setForeground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND_WHITE);
                button.setForeground(PRIMARY);
            }
        });
        return button;
    }
    
    public static JButton createSuccessButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SUBHEADING_FONT);
        button.setBackground(SUCCESS);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }
    
    public static JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SUBHEADING_FONT);
        button.setBackground(ERROR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }
    
    public static JButton createInfoButton(String text) {
        JButton button = new JButton(text);
        button.setFont(SUBHEADING_FONT);
        button.setBackground(new Color(52, 152, 219)); // Info blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }
    
    // Menu creation
    public static JMenuBar createStyledMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY);
        menuBar.setBorder(new LineBorder(PRIMARY_DARK, 1));
        return menuBar;
    }
    
    public static JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(SUBHEADING_FONT);
        menu.setForeground(Color.WHITE);
        menu.setOpaque(false);
        return menu;
    }
    
    public static JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(BODY_FONT);
        return item;
    }
    
    // Label creation
    public static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    // Table styling
    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setRowHeight(25);
        table.setSelectionBackground(new Color(241, 245, 249)); // Light gray selection
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_LIGHT);
        
        // Style table header - ensure it's always visible with dark blue background
        JTableHeader header = table.getTableHeader();
        header.setFont(SUBHEADING_FONT);
        header.setBackground(SIDEBAR_BG); // Same dark blue as sidebar
        header.setForeground(TEXT_WHITE);
        header.setReorderingAllowed(false);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Make header taller
        header.setVisible(true);
        
        // Ensure header cells are also styled
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(SIDEBAR_BG);
                c.setForeground(TEXT_WHITE);
                c.setFont(SUBHEADING_FONT);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
    }
    
    // Stat card creation for dashboard
    public static JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(BACKGROUND_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(accentColor, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(200, 120));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBHEADING_FONT);
        titleLabel.setForeground(TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        // Use accent color, but if it's the dark PRIMARY, use a lighter version for better contrast
        if (accentColor.equals(PRIMARY) || accentColor.equals(PRIMARY_COLOR)) {
            valueLabel.setForeground(PRIMARY); // Dark slate for primary
        } else {
            valueLabel.setForeground(accentColor);
        }
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
}
