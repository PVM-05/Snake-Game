package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPanel extends JPanel {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    private PromptTextField nameField;
    private MenuListener menuListener;
    private GameFrame parentFrame;

    public interface MenuListener {
        void onStartGame(String playerName);
        void onExitGame();
        void onShowScoreboard();
        void onShowSettings(String PlayerID);
    }

    public MenuPanel(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.menuListener = parentFrame;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("RẮN SĂN MỒI", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 48));
        titleLabel.setForeground(Color.GREEN);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Name input
        nameField = new PromptTextField("Nhập tên của bạn...");
        nameField.setFont(new Font("Montserrat", Font.PLAIN, 16));
        nameField.setBackground(Color.DARK_GRAY);
        nameField.setForeground(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        nameField.setHorizontalAlignment(JTextField.CENTER);
        nameField.setPreferredSize(new Dimension(300, 35));
        gbc.gridy = 1;
        add(nameField, gbc);

        // Buttons
        JButton startButton = createStyledButton("BẮT ĐẦU");
        JButton settingsButton = createStyledButton("CÀI ĐẶT");
        JButton scoreboardButton = createStyledButton("BẢNG XẾP HẠNG");
        JButton exitButton = createStyledButton("THOÁT");

        gbc.gridy = 2;
        add(startButton, gbc);
        gbc.gridy = 3;
        add(settingsButton, gbc);
        gbc.gridy = 4;
        add(scoreboardButton, gbc);
        gbc.gridy = 5;
        add(exitButton, gbc);

        // Button actions
        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty() || name.equals("Nhập tên của bạn...")) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            } else {
                menuListener.onStartGame(name);
            }
        });

        settingsButton.addActionListener(e -> menuListener.onShowSettings(parentFrame.getCurrentPlayerID()));
        scoreboardButton.addActionListener(e -> menuListener.onShowScoreboard());
        exitButton.addActionListener(e -> menuListener.onExitGame());

        // Keyboard shortcuts
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        if (!nameField.getText().trim().isEmpty() && !nameField.getText().equals("Nhập tên của bạn...")) {
                            menuListener.onStartGame(nameField.getText().trim());
                        }
                        break;
                    case KeyEvent.VK_S:
                        menuListener.onShowScoreboard();
                        break;
                    case KeyEvent.VK_C:
                        menuListener.onShowSettings(parentFrame.getCurrentPlayerID());
                        break;
                    case KeyEvent.VK_ESCAPE:
                        menuListener.onExitGame();
                        break;
                }
            }
        });

        // Focus name field
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                nameField.requestFocusInWindow();
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Montserrat", Font.BOLD, 20));
        button.setBackground(Color.GREEN.darker());
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.GREEN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.GREEN.darker());
            }
        });

        return button;
    }

    // Custom JTextField with placeholder
    private static class PromptTextField extends JTextField {
        private String prompt;

        public PromptTextField(String prompt) {
            this.prompt = prompt;
            setForeground(Color.LIGHT_GRAY);
            setText(prompt);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(prompt)) {
                        setText("");
                        setForeground(Color.WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(prompt);
                        setForeground(Color.LIGHT_GRAY);
                    }
                }
            });
        }
    }
}