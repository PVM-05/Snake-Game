package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PauseMenu extends JDialog {
    public PauseMenu(JFrame parent, ActionListener onResume, ActionListener onMenu) {
        super(parent, "Tạm dừng", true);
        setLayout(new GridLayout(3, 1, 10, 10));
        setSize(300, 200);
        setLocationRelativeTo(parent);
        // lam menu khong the dong duoc
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); 
        JButton resumeButton = new JButton("Tiếp tục");
        JButton menuButton = new JButton("Về menu chính");
        JButton exitConfirmButton = new JButton("Thoát game"); // nút mới

        resumeButton.addActionListener(e -> {
            onResume.actionPerformed(e);
            dispose();
        });

        menuButton.addActionListener(e -> {
            onMenu.actionPerformed(e);
            dispose();
        });

        // Nút mới mở dialog xác nhận thoát
        exitConfirmButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    PauseMenu.this,
                    "Bạn có chắc muốn thoát game?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);  // hoặc gọi hàm thoát game của bạn
            }
        });

        add(resumeButton);
        add(menuButton);
        add(exitConfirmButton); // thêm nút vào dialog
    }
}
