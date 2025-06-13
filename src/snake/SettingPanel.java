package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingPanel extends JPanel {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private String playerID;
    private boolean fromPauseMenu;
    private Theme.Type selectedTheme = Theme.Type.CLASSIC;
    private int soundVolumePercent = 50;
    private SettingsListener settingsListener;
    private JSlider volumeSlider;
    private JLabel volumeLabel;
    private JButton themeButton; // Thêm biến để truy cập themeButton

    public interface SettingsListener {
        void onSaveSettings(String playerID, Theme.Type theme, int soundVolumePercent);
        void onBack();
        void onResumeGame(String playerID, Theme.Type theme, int soundVolumePercent);
    }

    public SettingPanel(String playerID, boolean fromPauseMenu) {
        this.playerID = playerID;
        this.fromPauseMenu = fromPauseMenu;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.CENTER;

        // tiêu đề
        JLabel titleLabel = new JLabel("CÀI ĐẶT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 30));
        titleLabel.setForeground(Color.GRAY);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // chọn theme
        JLabel themeLabel = new JLabel("Chủ đề:", SwingConstants.RIGHT);
        themeLabel.setFont(new Font("Montserrat", Font.PLAIN, 20));
        themeLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(themeLabel, gbc);

        themeButton = createStyledButton(selectedTheme.toString());
        themeButton.addActionListener(e -> {
            Theme.Type[] themes = Theme.Type.values();
            selectedTheme = themes[(selectedTheme.ordinal() + 1) % themes.length];
            themeButton.setText(selectedTheme.toString());
        });
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(themeButton, gbc);

        // thanh âm lượng
        JLabel volumeTitle = new JLabel("Âm lượng:", SwingConstants.RIGHT);
        volumeTitle.setFont(new Font("Montserrat", Font.PLAIN, 20));
        volumeTitle.setForeground(Color.WHITE);
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(volumeTitle, gbc);

        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, soundVolumePercent);
        volumeSlider.setBackground(Color.BLACK);
        volumeSlider.setForeground(Color.GRAY);
        volumeSlider.setPreferredSize(new Dimension(200, 50));
        volumeSlider.addChangeListener(e -> {
            soundVolumePercent = volumeSlider.getValue();
            volumeLabel.setText(soundVolumePercent + "%");
        });
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        add(volumeSlider, gbc);

        volumeLabel = new JLabel(soundVolumePercent + "%", SwingConstants.CENTER);
        volumeLabel.setFont(new Font("Montserrat", Font.PLAIN, 14));
        volumeLabel.setForeground(Color.WHITE);
        gbc.gridy = 5;
        add(volumeLabel, gbc);

        // nút lưu
        JButton saveButton = createStyledButton("LƯU");
        saveButton.addActionListener(e -> {
            if (settingsListener != null) {
                settingsListener.onSaveSettings(playerID, selectedTheme, soundVolumePercent);
            }
        });
        gbc.gridy = 6;
        add(saveButton, gbc);

        // Continue button (only if from PauseMenu)
        if (fromPauseMenu) {
            JButton continueButton = createStyledButton("TIẾP TỤC");
            continueButton.addActionListener(e -> {
                if (settingsListener != null) {
                    settingsListener.onResumeGame(playerID, selectedTheme, soundVolumePercent);
                }
            });
            gbc.gridy = 7;
            add(continueButton, gbc);
        }

        // Back button
        JButton backButton = createStyledButton("QUAY LẠI");
        backButton.setBackground(Color.GRAY.darker());
        backButton.addActionListener(e -> {
            if (settingsListener != null) {
                settingsListener.onBack();
            }
        });
        gbc.gridy = fromPauseMenu ? 8 : 7;
        add(backButton, gbc);
    }

    public void setSettingsListener(SettingsListener listener) {
        this.settingsListener = listener;
    }

    public void setTheme(Theme.Type type) {
        this.selectedTheme = type;
        if (themeButton != null) {
            themeButton.setText(type.toString()); // Cập nhật văn bản button
        }
        repaint();
    }

    public void setSoundVolumePercent(int volumePercent) {
        this.soundVolumePercent = Math.max(0, Math.min(100, volumePercent));
        if (volumeSlider != null) {
            volumeSlider.setValue(soundVolumePercent);
        }
        if (volumeLabel != null) {
            volumeLabel.setText(soundVolumePercent + "%");
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Montserrat", Font.BOLD, 20));
        button.setBackground(Color.GREEN.darker());
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().equals(Color.GREEN.darker()) ? Color.GREEN : Color.GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(button.getBackground().equals(Color.GREEN) ? Color.GREEN.darker() : Color.GRAY.darker());
            }
        });

        return button;
    }
}