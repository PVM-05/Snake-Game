package snake;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame() {
        this.setTitle("Rắn Săn Mồi By PVM :D");
        this.add(new GamePanel());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
    }
}
