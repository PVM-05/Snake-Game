/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
    
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE);
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    GamePanel() {
        
    }
    public void startGame() {
        
    }
    public void paintComponent(Graphics g) {
        
    }
    public void draw(Graphics g) {
        
    }
    public void move() {
        
    }
    public void checkApple() {
        
    }
    public void checkCollision() {
        
    }
    public void gameOver(Graphics g) {
        
    }
    @Override
    public void actionPerformed(ActionEvent e){
        
    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            
        }
    }
}
