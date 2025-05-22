/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake;


import javax.swing.JFrame;

public class GameFrame extends JFrame{
    GameFrame(){
        GamePanel Panel  = new GamePanel();
        this.add(Panel);
        this.setTitle("Ran san moi"); // dat tieu do cho cua so game
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // dam bao dong app khi tat cua so
        this.setResizable(false); //khong cho thay doi kich thuoc cua so
        this.pack();
        this.setLocationRelativeTo(null); //Căn giữa cửa sổ trên màn hình.
        this.setVisible(true); //Hiển thị cửa sổ.
    }
    
}
