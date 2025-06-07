package snake;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleSystem {
    private List<Particle> particles;
    private List<Particle> particlesToAdd; // Buffer để tránh ConcurrentModificationException
    
    public ParticleSystem() {
        particles = new ArrayList<>();
        particlesToAdd = new ArrayList<>();
    }
    
    public void update() {
        // Thêm particles mới
        particles.addAll(particlesToAdd);
        particlesToAdd.clear();
        
        // Cập nhật tất cả particles
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update();
            
            // Xóa particles đã chết
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }
    
    public void draw(Graphics2D g2d) {
        for (Particle particle : particles) {
            particle.draw(g2d);
        }
    }
    
    // Tạo explosion effect khi ăn táo
    public void createExplosion(int x, int y) {
        for (int i = 0; i < 8; i++) { // 8 particles
            particlesToAdd.add(new Particle(x + 12.5f, y + 12.5f, Particle.ParticleType.EXPLOSION));
        }
    }
    
    // Tạo death effect khi chết
    public void createDeathEffect(int x, int y) {
        for (int i = 0; i < 15; i++) { // 15 particles
            particlesToAdd.add(new Particle(x + 12.5f, y + 12.5f, Particle.ParticleType.DEATH));
        }
    }
    
    // Tạo trail effect cho đuôi rắn
    public void createTrail(int x, int y) {
        if (Math.random() < 0.3) { // 30% cơ hội tạo trail
            particlesToAdd.add(new Particle(x + 12.5f, y + 12.5f, Particle.ParticleType.TRAIL));
        }
    }
    
    // Tạo sparkle effect cho táo
    public void createSparkle(int x, int y) {
        if (Math.random() < 0.1) { // 10% cơ hội tạo sparkle
            particlesToAdd.add(new Particle(x + 12.5f, y + 12.5f, Particle.ParticleType.SPARKLE));
        }
    }
    
    // Xóa tất cả particles
    public void clear() {
        particles.clear();
        particlesToAdd.clear();
    }
    
    // Lấy số lượng particles hiện tại
    public int getParticleCount() {
        return particles.size();
    }
}