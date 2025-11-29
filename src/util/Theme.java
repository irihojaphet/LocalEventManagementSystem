package util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Theme {
    // Color Palette - Modern Gradient Theme
    public static final Color PRIMARY_COLOR = new Color(99, 102, 241); // Indigo
    public static final Color PRIMARY_DARK = new Color(67, 56, 202);
    public static final Color PRIMARY_LIGHT = new Color(165, 180, 252);
    
    public static final Color SECONDARY_COLOR = new Color(236, 72, 153); // Pink
    public static final Color SECONDARY_DARK = new Color(219, 39, 119);
    public static final Color SECONDARY_LIGHT = new Color(251, 207, 232);
    
    public static final Color SUCCESS_COLOR = new Color(34, 197, 94); // Green
    public static final Color WARNING_COLOR = new Color(251, 191, 36); // Yellow
    public static final Color ERROR_COLOR = new Color(239, 68, 68); // Red
    public static final Color INFO_COLOR = new Color(59, 130, 246); // Blue
    
    public static final Color BACKGROUND_LIGHT = new Color(249, 250, 251);
    public static final Color BACKGROUND_WHITE = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color BORDER_COLOR = new Color(229, 231, 235);
    
    // Gradient Colors
    public static final Color GRADIENT_START = new Color(139, 92, 246); // Purple
    public static final Color GRADIENT_END = new Color(59, 130, 246); // Blue
    
    // Fonts
    public static Font getTitleFont() {
        return new Font("Segoe UI", Font.BOLD, 28);
    }
    
    public static Font getHeadingFont() {
        return new Font("Segoe UI", Font.BOLD, 20);
    }
    
    public static Font getSubheadingFont() {
        return new Font("Segoe UI", Font.BOLD, 16);
    }
    
    public static Font getBodyFont() {
        return new Font("Segoe UI", Font.PLAIN, 14);
    }
    
    public static Font getButtonFont() {
        return new Font("Segoe UI", Font.BOLD, 14);
    }
    
    // Styled Button
    public static JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, bgColor,
                    0, getHeight(), bgColor.darker()
                );
                g2.setPaint(gradient);
                
                // Rounded rectangle
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 12, 12
                );
                g2.fill(roundedRectangle);
                
                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth(), getHeight(), 12, 12));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(getButtonFont());
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    // Primary Button
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY_COLOR, Color.WHITE);
    }
    
    // Success Button
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS_COLOR, Color.WHITE);
    }
    
    // Danger Button
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, ERROR_COLOR, Color.WHITE);
    }
    
    // Warning Button
    public static JButton createWarningButton(String text) {
        return createStyledButton(text, WARNING_COLOR, TEXT_PRIMARY);
    }
    
    // Info Button
    public static JButton createInfoButton(String text) {
        return createStyledButton(text, INFO_COLOR, Color.WHITE);
    }
    
    // Secondary Button
    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, SECONDARY_COLOR, Color.WHITE);
    }
    
    // Styled Panel with gradient
    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                int w = getWidth();
                int h = getHeight();
                
                GradientPaint gp = new GradientPaint(
                    0, 0, GRADIENT_START,
                    w, h, GRADIENT_END
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
    }
    
    // Styled Text Field
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2.setColor(BACKGROUND_WHITE);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 8, 8
                );
                g2.fill(roundedRectangle);
                
                // Border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(roundedRectangle);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(getBodyFont());
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(new EmptyBorder(10, 15, 10, 15));
        field.setOpaque(false);
        field.setBackground(BACKGROUND_WHITE);
        
        return field;
    }
    
    // Styled Password Field
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(BACKGROUND_WHITE);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 8, 8
                );
                g2.fill(roundedRectangle);
                
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(roundedRectangle);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        field.setFont(getBodyFont());
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(new EmptyBorder(10, 15, 10, 15));
        field.setOpaque(false);
        field.setBackground(BACKGROUND_WHITE);
        
        return field;
    }
    
    // Styled Menu Bar
    public static JMenuBar createStyledMenuBar() {
        JMenuBar menuBar = new JMenuBar() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, PRIMARY_DARK,
                    getWidth(), 0, PRIMARY_COLOR
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        menuBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        return menuBar;
    }
    
    // Styled Menu
    public static JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(getSubheadingFont());
        menu.setForeground(Color.WHITE);
        menu.setOpaque(false);
        return menu;
    }
    
    // Styled Menu Item
    public static JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(getBodyFont());
        item.setForeground(TEXT_PRIMARY);
        item.setBackground(BACKGROUND_WHITE);
        item.setBorder(new EmptyBorder(8, 15, 8, 15));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(PRIMARY_LIGHT);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(BACKGROUND_WHITE);
            }
        });
        
        return item;
    }
    
    // Card Panel
    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(BACKGROUND_WHITE);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1, 15, 15
                );
                g2.fill(roundedRectangle);
                
                // Shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 1, getHeight() - 1, 15, 15));
                
                // Border
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1));
                g2.draw(roundedRectangle);
                
                g2.dispose();
            }
        };
        
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        return card;
    }
    
    // Stat Card
    public static JPanel createStatCard(String title, String value, Color color) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout());
        
        // Gradient background
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, color,
                    getWidth(), getHeight(), color.darker()
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setOpaque(false);
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(getBodyFont());
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        
        gradientPanel.add(titleLabel, BorderLayout.NORTH);
        gradientPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(gradientPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    // Styled Label
    public static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    // Styled Table
    public static void styleTable(JTable table) {
        table.setFont(getBodyFont());
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(BACKGROUND_WHITE);
        table.setForeground(TEXT_PRIMARY);
        
        // Header styling - ensure it's always visible
        JTableHeader header = table.getTableHeader();
        header.setFont(getSubheadingFont());
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        header.setReorderingAllowed(false);
        
        // Use a custom renderer that always maintains the header styling
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            {
                setOpaque(true);
                setBackground(PRIMARY_COLOR);
                setForeground(Color.WHITE);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(getSubheadingFont());
                setBorder(new EmptyBorder(10, 10, 10, 10));
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setOpaque(true);
                setBackground(PRIMARY_COLOR);
                setForeground(Color.WHITE);
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });
    }
    
    // Styled ComboBox - use Basic UI to avoid Windows LAF conflicts
    public static <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> combo = new JComboBox<T>() {
            @Override
            public void updateUI() {
                // Force Basic UI to avoid Windows LAF painting issues
                setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
            }
        };
        
        // Force update to apply Basic UI
        combo.updateUI();
        
        combo.setFont(getBodyFont());
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BACKGROUND_WHITE);
        combo.setOpaque(true);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        
        return combo;
    }
    
    // Styled TextArea
    public static JTextArea createStyledTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(BACKGROUND_WHITE);
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 8, 8
                );
                g2.fill(roundedRectangle);
                
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(roundedRectangle);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        area.setFont(getBodyFont());
        area.setForeground(TEXT_PRIMARY);
        area.setBorder(new EmptyBorder(10, 15, 10, 15));
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        
        return area;
    }
}

