# README - Trò Chơi Rắn Săn Mồi

## Giới thiệu

Rắn Săn Mồi là một phiên bản hiện đại của trò chơi Snake cổ điển, được phát triển bằng Java trên môi trường NetBeans, tích hợp cơ sở dữ liệu MySQL thông qua XAMPP để lưu trữ thông tin người chơi, điểm số, cài đặt cá nhân và trạng thái trò chơi. Trò chơi nổi bật với giao diện đồ họa sinh động, hiệu ứng hạt (particle effects), các vật phẩm tăng sức mạnh (power-ups), và khả năng lưu/tải trạng thái game.

## Tính năng chính

### Giao diện người dùng
- Menu chính với tùy chọn bắt đầu, cài đặt, bảng xếp hạng và thoát
- Màn hình tạm dừng với các tùy chọn tiếp tục, cài đặt hoặc trở về menu
- Bảng xếp hạng hiển thị top 10 người chơi với thống kê tổng số người chơi, lượt chơi và điểm trung bình
- Cài đặt cho phép chọn chủ đề và điều chỉnh âm lượng:
  - **Classic**: Rắn xanh lá cây, táo màu đỏ
  - **Neon**: Rắn xanh dương, táo màu tím
  - **Retro**: Rắn màu nâu, táo màu vàng

### Gameplay
- Điều khiển rắn ăn táo để tăng điểm và chiều dài
- Vật phẩm tăng sức mạnh: Tăng tốc độ (Speed Boost), gấp đôi điểm (Double Score), và khiên bảo vệ (Shield)
- Tốc độ tăng dần khi ăn táo, với thanh tiến trình hiển thị cấp độ tốc độ

### Hiệu ứng
- Hiệu ứng hạt cho các sự kiện như ăn táo, nhặt vật phẩm, va chạm hoặc di chuyển
- Nhạc nền và hiệu ứng âm thanh khi ăn táo, nhặt vật phẩm hoặc game over

### Cơ sở dữ liệu
- Lưu trữ thông tin người chơi (tên, ID), điểm số, trạng thái game và cài đặt cá nhân
- Hỗ trợ tải trạng thái game đã lưu để tiếp tục chơi

## Yêu cầu hệ thống

- **Hệ điều hành**: Windows, macOS, hoặc Linux
- **Java**: JDK 8 trở lên
- **NetBeans**: Phiên bản 8.2 hoặc cao hơn
- **XAMPP**: Phiên bản 7.4.x hoặc cao hơn (bao gồm MySQL)
- **Thư viện**: MySQL Connector/J (mysql-connector-java)
- **File âm thanh**: Các file WAV (background.wav, eat.wav, gameover.wav, powerup.wav) trong thư mục `src/snake`

## Hướng dẫn cài đặt

### 1. Cài đặt môi trường

#### Cài đặt XAMPP
- Tải và cài đặt XAMPP từ trang chính thức
- Khởi động Apache và MySQL trong XAMPP Control Panel
- Truy cập phpMyAdmin qua `http://localhost/phpmyadmin` để quản lý cơ sở dữ liệu

#### Cài đặt NetBeans
- Tải và cài đặt NetBeans IDE từ trang chính thức
- Đảm bảo NetBeans được cấu hình với JDK 8 hoặc cao hơn

#### Tải MySQL Connector
- Tải file `mysql-connector-java` (ví dụ: `mysql-connector-java-8.0.x.jar`) từ Maven Repository
- Lưu file JAR vào một thư mục dễ truy cập

### 2. Thiết lập cơ sở dữ liệu

Mở phpMyAdmin và tạo cơ sở dữ liệu:

```sql
CREATE DATABASE SnakeGameDB;
```

Chọn cơ sở dữ liệu `SnakeGameDB` và chạy các câu lệnh SQL sau để tạo bảng:

```sql
USE SnakeGameDB;

CREATE TABLE Player (
    playerID VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE playerinfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    playerID VARCHAR(36),
    score INT NOT NULL,
    play_time DATETIME NOT NULL,
    FOREIGN KEY (playerID) REFERENCES Player(playerID)
);

CREATE TABLE Settings (
    player_id VARCHAR(36) PRIMARY KEY,
    theme VARCHAR(20) DEFAULT 'CLASSIC',
    sound_volume_percent INT DEFAULT 50,
    FOREIGN KEY (player_id) REFERENCES Player(playerID)
);

CREATE TABLE GameState (
    player_id VARCHAR(36) PRIMARY KEY,
    state BLOB,
    FOREIGN KEY (player_id) REFERENCES Player(playerID)
);
```

Kiểm tra thông tin kết nối trong file `DatabaseManager.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/SnakeGameDB";
private static final String USER = "root";
private static final String PASSWORD = "";
```

> **Lưu ý**: Nếu MySQL sử dụng mật khẩu khác, cập nhật biến `PASSWORD` tương ứng.

### 3. Cấu hình dự án trên NetBeans

#### Tạo dự án mới
- Mở NetBeans, chọn **File > New Project > Java > Java Application**
- Đặt tên dự án (ví dụ: `SnakeGame`) và chọn thư mục lưu trữ

#### Thêm mã nguồn
- Tạo package `snake` trong thư mục **Source Packages**
- Sao chép các file mã nguồn vào package `snake`:
  - `Snake.java`
  - `GameFrame.java`
  - `GamePanel.java`
  - `MenuPanel.java`
  - `ScoreboardPanel.java`
  - `SettingPanel.java`
  - `PauseMenu.java`
  - `DatabaseManager.java`
  - `GameState.java`
  - `PowerUp.java`
  - `PowerUpNotifications.java`
  - `Particle.java`
  - `ParticleSystem.java`
  - `Theme.java`

#### Thêm thư viện MySQL Connector
- Chuột phải vào dự án, chọn **Properties > Libraries > Add JAR/Folder**
- Chọn file `mysql-connector-java-8.0.x.jar` đã tải

#### Thêm tài nguyên âm thanh
- Tạo thư mục `src/snake` trong thư mục dự án
- Thêm các file âm thanh vào thư mục `src/snake`:
  - `background.wav`
  - `eat.wav`
  - `gameover.wav`
  - `powerup.wav`

> **Lưu ý**: Nếu không có file âm thanh, bạn có thể thay bằng các file WAV khác hoặc chỉnh sửa mã để bỏ qua âm thanh.

### 4. Chạy trò chơi

1. Đảm bảo XAMPP đang chạy (Apache và MySQL)
2. Trong NetBeans, chuột phải vào file `Snake.java` và chọn **Run File**
3. Trò chơi sẽ khởi động với giao diện menu chính:
   - Nhập tên người chơi và nhấn **BẮT ĐẦU** hoặc phím **Enter** để chơi
   - Chọn **CÀI ĐẶT** (phím **C**) để thay đổi chủ đề hoặc âm lượng:
     - **Classic**: Rắn xanh lá cây, táo màu đỏ
     - **Neon**: Rắn xanh dương, táo màu tím  
     - **Retro**: Rắn màu nâu, táo màu vàng
   - Chọn **BẢNG XẾP HẠNG** (phím **S**) để xem điểm số cao nhất
   - Nhấn **THOÁT** (phím **ESC**) để thoát game

## 🎨 Themes & Customization

Trò chơi cung cấp 3 chủ đề khác nhau để người chơi có thể tùy chỉnh giao diện:

| Theme | Màu Rắn | Màu Táo | Mô tả |
|-------|---------|---------|--------|
| **Classic** | Xanh lá cây | Đỏ | Phong cách truyền thống của game Snake |
| **Neon** | Xanh dương | Tím | Phong cách hiện đại với màu sắc neon |
| **Retro** | Nâu | Vàng | Phong cách cổ điển với tông màu ấm |

## Hướng dẫn chơi

### Điều khiển
- Sử dụng phím **↑**, **↓**, **←**, **→** để di chuyển rắn
- Nhấn **ESC** để mở menu tạm dừng (tiếp tục, cài đặt, hoặc về menu chính)
- Nhấn **SPACE** khi game over để chơi lại
- Nhấn **ESC** khi game over để trở về menu chính

### Mục tiêu
- Ăn táo để tăng điểm và chiều dài rắn
- Tránh va vào chính mình (trừ khi có khiên bảo vệ)

### Vật phẩm tăng sức mạnh
- **Tăng tốc độ** (màu vàng): Giảm thời gian di chuyển
- **Gấp đôi điểm** (màu xanh): Điểm số nhân đôi trong 5 giây
- **Khiên** (màu tím): Bảo vệ rắn khỏi va chạm một lần

### Tính năng khác
- **Tốc độ**: Tăng dần sau mỗi 3 táo, hiển thị qua thanh tiến trình tốc độ
- **Lưu trữ**: Điểm số và trạng thái game được lưu tự động vào cơ sở dữ liệu khi game over hoặc vào menu tạm dừng

## Ghi chú

- **Cơ sở dữ liệu**: Đảm bảo MySQL trong XAMPP đang chạy trước khi khởi động trò chơi để tránh lỗi kết nối
- **Âm thanh**: Nếu thiếu file âm thanh hoặc gặp lỗi, kiểm tra đường dẫn trong thư mục `src/snake` hoặc vô hiệu hóa âm thanh trong mã nguồn
- **Font chữ**: Trò chơi sử dụng font Montserrat. Nếu font không có, hệ thống sẽ dùng font mặc định
- **Lỗi kết nối**: Nếu gặp lỗi kết nối cơ sở dữ liệu, kiểm tra `URL`, `USER`, và `PASSWORD` trong `DatabaseManager.java`

## Tác giả

**PVM** - Nhà phát triển trò chơi Rắn Săn Mồi

---

## 📞 Liên hệ & Hỗ trợ

Nếu bạn gặp vấn đề trong quá trình cài đặt hoặc sử dụng, vui lòng liên hệ với tác giả để được hỗ trợ.

## 📝 Giấy phép

Dự án này được phát triển cho mục đích học tập và giải trí.