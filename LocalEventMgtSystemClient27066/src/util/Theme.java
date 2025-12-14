package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/**
 * Theme utility for consistent UI styling
 * 
 * @author 27066
 */
public class Theme {
    // Background colors
    public static final Color BACKGROUND_LIGHT = new Color(245, 245, 250);
    public static final Color BACKGROUND_WHITE = Color.WHITE;
    
    // Primary colors
    public static final Color PRIMARY = new Color(70, 130, 180); // Steel Blue
    public static final Color PRIMARY_DARK = new Color(50, 100, 150);
    public static final Color PRIMARY_LIGHT = new Color(135, 206, 250); // Light Sky Blue
    public static final Color PRIMARY_COLOR = PRIMARY; // Alias
    
    // Accent colors
    public static final Color ACCENT = new Color(255, 140, 0); // Dark Orange
    public static final Color SUCCESS = new Color(34, 139, 34); // Forest Green
    public static final Color SUCCESS_COLOR = SUCCESS; // Alias
    public static final Color WARNING = new Color(255, 165, 0); // Orange
    public static final Color WARNING_COLOR = WARNING; // Alias
    public static final Color ERROR = new Color(220, 20, 60); // Crimson
    public static final Color DANGER = ERROR; // Alias
    public static final Color SECONDARY_COLOR = PRIMARY_LIGHT; // Alias
    
    // Text colors
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    public static final Color TEXT_LIGHT = new Color(150, 150, 150);
    
    // Border colors
    public static final Color BORDER_LIGHT = new Color(200, 200, 200);
    public static final Color BORDER_MEDIUM = new Color(150, 150, 150);
    
    // Fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBHEADING_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
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
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK);
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
                button.setBackground(PRIMARY_LIGHT);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND_WHITE);
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
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(BORDER_LIGHT);
        table.getTableHeader().setFont(SUBHEADING_FONT);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
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
        valueLabel.setForeground(accentColor);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
}
