package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PauseMenu extends JDialog {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;

    public PauseMenu(Frame owner, ActionListener resumeListener, ActionListener menuListener) {
        super(owner, "Tạm Dừng", true);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(owner);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("TẠM DỪNG", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // Resume button
        JButton resumeButton = createStyledButton("TIẾP TỤC");
        resumeButton.addActionListener(e -> {
            resumeListener.actionPerformed(e);
            dispose();
        });
        gbc.gridy = 1;
        add(resumeButton, gbc);

        // Settings button
        JButton settingsButton = createStyledButton("CÀI ĐẶT");
        settingsButton.addActionListener(e -> {
            if (owner instanceof GameFrame) {
                GameFrame gameFrame = (GameFrame) owner;
                // Lưu trạng thái game trước khi mở cài đặt
                gameFrame.getGamePanel().saveGameState(gameFrame.getCurrentPlayerID());
                // Đóng PauseMenu trước khi mở SettingPanel
                dispose();
                // Mở SettingPanel với fromPauseMenu = true
                gameFrame.showSettings(gameFrame.getCurrentPlayerID(), true);
            }
        });
        gbc.gridy = 2;
        add(settingsButton, gbc);

        // Menu button
        JButton menuButton = createStyledButton("MENU");
        menuButton.addActionListener(e -> {
            menuListener.actionPerformed(e);
            dispose();
        });
        gbc.gridy = 3;
        add(menuButton, gbc);

        // Keyboard shortcuts
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    resumeListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                    dispose();
                }
            }
        });

        setFocusable(true);
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
}