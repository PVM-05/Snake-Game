package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class SettingPanel extends JPanel {
    private String playerID;
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private Theme.Type selectedTheme = Theme.Type.CLASSIC;
    private int soundVolumePercent = 50; // Phần trăm âm lượng (0-100)
    private SettingsListener settingsListener;
    private Rectangle saveButtonBounds;
    private Rectangle themeButtonBounds;
    private Rectangle backButtonBounds;
    private boolean saveButtonHovered;
    private boolean themeButtonHovered;
    private boolean backButtonHovered;

    public interface SettingsListener {
        void onSaveSettings(String playerID, Theme.Type theme, int soundVolumePercent);
        void onBack();
    }

    public SettingPanel(String playerID) {
        this.playerID = playerID;
        setPreferredSize(new java.awt.Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        addMouseListener(new SettingsMouseAdapter());
    }

    public void setSettingsListener(SettingsListener listener) {
        this.settingsListener = listener;
    }

    public void setTheme(Theme.Type theme) {
        this.selectedTheme = theme;
        repaint();
    }

    public void setSoundVolumePercent(int volumePercent) {
        this.soundVolumePercent = Math.max(0, Math.min(100, volumePercent));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics titleMetrics = g.getFontMetrics();
        String title = "Cài đặt";
        g.drawString(title, (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics buttonMetrics = g.getFontMetrics();
        String themeText = "Theme: " + selectedTheme;
        int themeX = (SCREEN_WIDTH - buttonMetrics.stringWidth(themeText)) / 2;
        int themeY = 200;
        g.setColor(themeButtonHovered ? new Color(70, 200, 70) : Color.GREEN);
        g.fillRoundRect(themeX - 20, themeY - 25, buttonMetrics.stringWidth(themeText) + 40, 35, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRoundRect(themeX - 20, themeY - 25, buttonMetrics.stringWidth(themeText) + 40, 35, 15, 15);
        g.drawString(themeText, themeX, themeY);
        themeButtonBounds = new Rectangle(themeX - 20, themeY - 25, buttonMetrics.stringWidth(themeText) + 40, 35);

        g.setColor(Color.WHITE);
        g.drawString("Âm lượng: " + soundVolumePercent + "%", 200, 300);
        g.setColor(Color.GRAY);
        g.fillRect(200, 310, 200, 10);
        int volumeX = 200 + (soundVolumePercent * 200 / 100);
        g.setColor(Color.CYAN);
        g.fillOval(Math.max(200, Math.min(400, volumeX)) - 5, 305, 10, 20);

        String saveText = "Lưu";
        int saveX = (SCREEN_WIDTH - buttonMetrics.stringWidth(saveText)) / 2;
        int saveY = 400;
        g.setColor(saveButtonHovered ? new Color(70, 200, 70) : Color.GREEN);
        g.fillRoundRect(saveX - 20, saveY - 25, buttonMetrics.stringWidth(saveText) + 40, 35, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRoundRect(saveX - 20, saveY - 25, buttonMetrics.stringWidth(saveText) + 40, 35, 15, 15);
        g.drawString(saveText, saveX, saveY);
        saveButtonBounds = new Rectangle(saveX - 20, saveY - 25, buttonMetrics.stringWidth(saveText) + 40, 35);

        String backText = "Quay lại";
        int backX = (SCREEN_WIDTH - buttonMetrics.stringWidth(backText)) / 2;
        int backY = saveY + 60;
        g.setColor(backButtonHovered ? new Color(150, 150, 150) : Color.GRAY);
        g.fillRoundRect(backX - 20, backY - 25, buttonMetrics.stringWidth(backText) + 40, 35, 15, 15);
        g.setColor(Color.WHITE);
        g.drawRoundRect(backX - 20, backY - 25, buttonMetrics.stringWidth(backText) + 40, 35, 15, 15);
        g.drawString(backText, backX, backY);
        backButtonBounds = new Rectangle(backX - 20, backY - 25, buttonMetrics.stringWidth(backText) + 40, 35);
    }

    private class SettingsMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            if (themeButtonBounds.contains(mouseX, mouseY)) {
                Theme.Type[] themes = Theme.Type.values();
                selectedTheme = themes[(selectedTheme.ordinal() + 1) % themes.length];
                repaint();
            } else if (saveButtonBounds.contains(mouseX, mouseY)) {
                if (settingsListener != null) {
                    settingsListener.onSaveSettings(playerID, selectedTheme, soundVolumePercent);
                }
            } else if (backButtonBounds.contains(mouseX, mouseY)) {
                if (settingsListener != null) {
                    settingsListener.onBack();
                }
            } else if (mouseY >= 305 && mouseY <= 325 && mouseX >= 200 && mouseX <= 400) {
                soundVolumePercent = (int) ((mouseX - 200) / 200.0f * 100);
                setSoundVolumePercent(soundVolumePercent);
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            boolean wasSaveHovered = saveButtonHovered;
            boolean wasThemeHovered = themeButtonHovered;
            boolean wasBackHovered = backButtonHovered;

            saveButtonHovered = saveButtonBounds != null && saveButtonBounds.contains(mouseX, mouseY);
            themeButtonHovered = themeButtonBounds != null && themeButtonBounds.contains(mouseX, mouseY);
            backButtonHovered = backButtonBounds != null && backButtonBounds.contains(mouseX, mouseY);

            if (wasSaveHovered != saveButtonHovered || 
                wasThemeHovered != themeButtonHovered || 
                wasBackHovered != backButtonHovered) {
                repaint();
            }
        }
    }
}