# 🌟 VocabMaster - Ứng dụng Ôn tập Từ vựng chuẩn Quizlet

Ứng dụng web học từ vựng tiếng Anh chuyên sâu, phong cách Quizlet, được xây dựng theo yêu cầu với **Spring Boot** (Backend), **Vue 3 + Tailwind CSS** (Frontend) và **H2 Persistent Database** (Cơ sở dữ liệu lưu trữ vĩnh viễn trên máy tính).

---

## 🚀 Cách mở và sử dụng ứng dụng

Hệ thống hiện **đang chạy sẵn sàng**. Bạn chỉ cần mở trình duyệt và truy cập:

👉 **[http://localhost:8888](http://localhost:8888)**

Khi muốn khởi động lại sau này:
- **Cách 1 (Nhanh nhất):** Nhấp đúp vào file [`start.bat`](file:///c:/Users/Admin/Downloads/TA/start.bat) ở thư mục gốc. Trình duyệt sẽ tự động mở lên.
- **Cách 2:** Mở PowerShell tại thư mục này và chạy:
  ```powershell
  java -jar backend/target/demo-0.0.1-SNAPSHOT.jar
  ```

---

## 🎯 Tính năng nổi bật

### 1. 🗂️ Thẻ ghi nhớ 3D (Flashcards Mode)
- **Lật thẻ 3D mượt mà:** Nhấp vào thẻ hoặc nhấn phím <kbd>Space</kbd> để lật giữa mặt tiếng Anh (Từ vựng + Phiên âm IPA) và mặt tiếng Việt (Nghĩa + Từ loại).
- **Phát âm chuẩn bản xứ:** Tích hợp **Web Speech API** phát âm tiếng Anh tự nhiên chỉ với 1 cú click (hoặc nhấn phím <kbd>A</kbd>).
- **Gắn sao lưu ý:** Đánh dấu các từ khó nhớ bằng phím <kbd>S</kbd> hoặc nút sao màu vàng.
- **Tự động chạy (Auto-play Slideshow):** Tự động phát âm, lật thẻ và chuyển câu theo chu kỳ.
- **Phím tắt tiện lợi:**
  - <kbd>Space</kbd> / <kbd>Enter</kbd>: Lật thẻ
  - <kbd>←</kbd> / <kbd>→</kbd>: Thẻ trước / Thẻ tiếp theo
  - <kbd>A</kbd> / <kbd>↑</kbd>: Nghe phát âm
  - <kbd>S</kbd>: Gắn sao / Bỏ gắn sao
  - <kbd>1</kbd>: Đánh dấu "Cần học lại"
  - <kbd>2</kbd>: Đánh dấu "Đã thuộc từ này"

### 2. 💡 Học thông minh (Smart Learn Mode)
- Tự động tạo câu hỏi trắc nghiệm 4 đáp án (Tiếng Anh ⇄ Nghĩa tiếng Việt).
- Tích hợp phát âm ngay khi hiển thị câu hỏi.
- **Cơ chế lặp lại ngắt quãng:** Các từ trả lời sai sẽ được tự động xếp vào cuối hàng đợi để bạn ôn luyện lại cho đến khi đạt 100% độ chính xác.
- Hiệu ứng pháo hoa chúc mừng khi hoàn thành toàn bộ lượt học.

### 3. ⚡ Ghép thẻ tốc độ (Match Game)
- Game tìm cặp ghép thẻ tương tự như **Quizlet Match**.
- Đồng hồ bấm giờ tính theo từng phần mười giây.
- Lưu trữ và vinh danh kỷ lục thời gian nhanh nhất của bạn.

### 4. 📝 Kiểm tra (Test / Exam Mode)
- Tùy chọn số lượng câu hỏi (10 câu, 20 câu hoặc toàn bộ).
- Đa dạng loại câu hỏi: **Trắc nghiệm**, **Đúng/Sai**, và **Điền từ tự luận**.
- Chấm điểm ngay lập tức với bảng tổng kết điểm số, tỷ lệ % và phân tích chi tiết từng câu đúng/sai kèm giải thích.

### 5. 📋 Quản lý từ vựng & Thêm từ mới hàng ngày
- **Thêm từng từ:** Nhập từ, phiên âm, từ loại, nghĩa tiếng Việt, câu ví dụ và bản dịch.
- **Nhập hàng loạt (Bulk Import):** Bạn có thể dán danh sách hàng chục, hàng trăm từ theo định dạng tab (`word \t /ipa/ \t (v) meaning`) hoặc dấu gạch ngang (`-`), hai chấm (`:`). Hệ thống có **bảng xem trước (live preview)** kết quả nhận diện trước khi lưu vào DB.
- **Phân chia theo ngày (Sets):** Dễ dàng tạo các bộ từ riêng như `Day 1`, `Day 2`, `Day 3`, `Từ vựng hôm nay` để học tập cuốn chiếu mỗi ngày.
- **Xuất file:** Nút "Xuất file" cho phép tải toàn bộ danh sách từ về máy tính dưới dạng file text bất cứ lúc nào.

---

## 🗄️ Cơ sở dữ liệu & Lưu trữ (Database)

- **Hệ quản trị CSDL:** H2 Database (File persistent mode).
- **Vị trí lưu file:** `backend/data/vocabdb.mv.db`.
- Toàn bộ từ vựng bạn thêm mới, chỉnh sửa, đánh dấu sao, số lần trả lời đúng/sai và mức độ thuộc bài đều được lưu trực tiếp vào file CSDL trên ổ cứng, **không bao giờ bị mất** khi tắt ứng dụng hoặc khởi động lại máy tính.
