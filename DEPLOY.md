# 🌐 Hướng Dẫn Deploy Dự Án VocabMaster Miễn Phí 100%

Dự án VocabMaster đã được đóng gói thống nhất: **Vue 3 Frontend đã được nhúng trực tiếp vào Spring Boot Backend (Single Fat JAR)**. Điều này giúp bạn chỉ cần deploy **1 dịch vụ duy nhất** mà không lo về cấu hình domain hay lỗi CORS.

Dưới đây là 3 cách deploy hoàn toàn **miễn phí 100%**, không cần thẻ visa/mastercard.

---

## 🏆 Cách 1: Render.com + Neon.tech (Khuyên dùng nhất - Chuẩn Cloud vĩnh viễn)

### 📌 Tại sao nên dùng Neon cho Database?
Các dịch vụ hosting miễn phí (như Render, Koyeb) đều sử dụng **ổ cứng tạm thời (Ephemeral Storage)**. Khi server khởi động lại, file CSDL H2 cục bộ sẽ bị reset. 
👉 Giải pháp: Kết nối tới **Neon.tech** (PostgreSQL Serverless Miễn phí vĩnh viễn, dung lượng 500MB đủ chứa hàng trăm ngàn từ vựng).

### Bước 1: Tạo Database PostgreSQL miễn phí trên Neon
1. Truy cập [https://neon.tech](https://neon.tech) và đăng ký tài khoản miễn phí (bằng GitHub/Google).
2. Tạo project mới (ví dụ: `vocab-db`, chọn region **Singapore** để có tốc độ nhanh nhất tại Việt Nam).
3. Tại Dashboard của Neon, ở mục **Connection Details**, chọn tab **JDBC** hoặc xem chuỗi kết nối:
   - Host (ví dụ: `ep-xyz.ap-southeast-1.aws.neon.tech`)
   - Database name (mặc định: `neondb`)
   - User & Password

### Bước 2: Đẩy dự án lên GitHub
1. Mở terminal tại thư mục dự án `C:\Users\Admin\Downloads\TA`:
   ```bash
   git init
   git add .
   git commit -m "Deploy VocabMaster"
   ```
2. Tạo 1 repository mới trên GitHub (Private hoặc Public tùy bạn).
3. Đẩy mã nguồn lên GitHub:
   ```bash
   git remote add origin https://github.com/<tai-khoan-cua-ban>/<ten-repo>.git
   git branch -M main
   git push -u origin main
   ```

### Bước 3: Deploy lên Render.com
1. Đăng ký tài khoản miễn phí tại [https://render.com](https://render.com).
2. Nhấn nút **New +** -> Chọn **Web Service**.
3. Chọn **Build and deploy from a Git repository** -> Kết nối tới repository bạn vừa đẩy lên.
4. Điền các thông tin:
   - **Name**: `vocabmaster` (hoặc tên tùy thích)
   - **Region**: Singapore
   - **Language**: Docker (Render sẽ tự động đọc file `Dockerfile` đã tạo sẵn trong dự án)
   - **Instance Type**: **Free** ($0/tháng)
5. Kéo xuống mục **Environment Variables** và thêm các biến sau:
   - `SPRING_DATASOURCE_URL` = `jdbc:postgresql://<neon-host>/neondb?sslmode=require`
   - `SPRING_DATASOURCE_USERNAME` = `<username-cua-neon>`
   - `SPRING_DATASOURCE_PASSWORD` = `<password-cua-neon>`
   - `SPRING_DATASOURCE_DRIVER` = `org.postgresql.Driver`
6. Nhấn **Create Web Service**. 
   Render sẽ tự động build frontend Vue, build backend Spring Boot và khởi chạy ứng dụng!
7. Sau ~2-3 phút, bạn sẽ nhận được đường link HTTPS miễn phí dạng:
   👉 `https://vocabmaster-xxxx.onrender.com`

---

## ⚡ Cách 2: Koyeb.com (Miễn phí - Không bị Sleep)

Render ở gói Free sẽ tự động "ngủ" nếu không có người truy cập sau 15 phút (khi có người vào lại mất ~30s để thức dậy). Nếu bạn muốn server luôn chạy 24/7:

1. Đăng ký tài khoản tại [https://koyeb.com](https://koyeb.com).
2. Chọn **Create App** -> **GitHub**.
3. Chọn repository chứa dự án.
4. Koyeb sẽ phát hiện file `Dockerfile`.
5. Chọn gói **Free** (Nano instance 512MB RAM).
6. Thêm các biến môi trường kết nối tới Neon Database như ở Cách 1.
7. Bấm **Deploy**. Bạn sẽ có một đường link dạng `https://<app-name>.koyeb.app`.

---

## 🚀 Cách 3: Cloudflare Tunnel (Nhanh nhất - Học ngay trên điện thoại)

Nếu bạn chỉ muốn:
- Học từ vựng trên điện thoại của mình ở bất cứ đâu (4G, Wifi trường học/quán cafe).
- Không muốn tạo tài khoản GitHub hay setup database trên mây.
- Giữ nguyên toàn bộ dữ liệu H2 đang có trên máy tính hiện tại.

👉 **Cloudflare Tunnel** là giải pháp hoàn hảo (hoàn toàn miễn phí, an toàn, không cần mở port modem):

1. Tải công cụ Cloudflare Tunnel:
   Chạy lệnh trong PowerShell:
   ```powershell
   winget install Cloudflare.cloudflared
   ```
2. Khởi động ứng dụng của bạn bằng file `start.bat` (chạy trên port 8888).
3. Mở thêm 1 cửa sổ PowerShell mới và gõ:
   ```powershell
   cloudflared tunnel --url http://localhost:8888
   ```
4. Cloudflare sẽ tạo ra ngay 1 đường link công khai dạng:
   👉 `https://random-words-xyz.trycloudflare.com`
5. Bạn chỉ cần mở link đó trên điện thoại là có thể học và thêm từ vựng bình thường!
