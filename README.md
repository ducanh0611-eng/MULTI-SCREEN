# MultiGrid Workspace - Ứng Dụng Đa Nhiệm Chia Màn Hình Nhiều Ngăn Trên Android

Ứng dụng cho phép chia màn hình thiết bị Android thành nhiều ngăn độc lập (**1 ngăn, 2 ngăn dọc, 2 ngăn ngang, 3 ngăn, 4 ngăn 2x2**), hỗ trợ chạy đồng thời nhiều ứng dụng/dịch vụ/web app cùng lúc, và **tự động ghi nhớ chính xác trạng thái, vị trí, đường dẫn của từng ngăn sau khi tắt để khi mở lại sẽ khôi phục 100% mà không phải thao tác lại**.

---

## 🌟 Các tính năng chính

1. **Chia màn hình linh hoạt:**
   - Chế độ 1 ngăn toàn màn hình.
   - Chế độ 2 ngăn dọc (Trên - Dưới).
   - Chế độ 2 ngăn ngang (Trái - Phải).
   - Chế độ 3 ngăn (1 ngăn trên lớn, 2 ngăn dưới nhỏ).
   - Chế độ 4 ngăn (2x2 Quad Grid) chạy 4 app đồng thời.

2. **Chức năng GHI NHỚ TỰ ĐỘNG (Auto-Save & Restore Session):**
   - Tự động lưu chế độ layout đang chọn vào `SharedPreferences`.
   - Tự động lưu URL, tên ứng dụng và trạng thái âm thanh của từng ngăn (Slot 0, 1, 2, 3) ngay khi có thay đổi hoặc khi tắt app (`onPause`, `onStop`).
   - **Khi tắt app đi (hoặc khởi động lại điện thoại), mở lại app sẽ tự động load đúng 100% các ứng dụng và vị trí như lần cuối sử dụng.**

3. **Điều khiển tiện ích cho từng ô:**
   - **Đổi ứng dụng nhanh:** Bấm vào icon App để chọn các app phổ biến (YouTube, TikTok, Facebook, Messenger, Instagram, X, ChatGPT, TradingView, Shopee...) hoặc gõ link web bất kỳ.
   - **Bật / Tắt âm thanh riêng (Mute/Unmute):** Cho phép tắt tiếng từng ô riêng lẻ để tránh 2-4 video phát đè âm thanh vào nhau.
   - **Phóng to tạm thời (Maximize):** Bấm nút phóng to 1 ô bất kỳ để xem chi tiết, bấm lại thu nhỏ về lại lưới.
   - **Quay lại (Back) / Tải lại (Reload)** độc lập cho từng ô.
   - Nút **Tải lại tất cả** trên thanh công cụ.

---

## 🚀 Hướng dẫn Build file APK để cài lên điện thoại

### Cách 1: Sử dụng Android Studio (Khuyên dùng)
1. Tải và cài đặt [Android Studio](https://developer.android.com/studio) (nếu chưa có).
2. Mở Android Studio, chọn **Open**, trỏ tới thư mục:
   `MultiScreenGridApp`
3. Chờ Gradle đồng bộ (Sync) xong.
4. Trên thanh menu, chọn:
   **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
5. Sau khi build xong, bấm vào chữ **locate** ở góc dưới bên phải để nhận file `app-debug.apk`.
6. Chép file `.apk` vào điện thoại Android và cài đặt.

---

### Cách 2: Tự động Build APK Online qua GitHub Actions (Không cần cài Android Studio)
Nếu máy tính của bạn chưa có Android Studio hoặc không muốn cài nặng máy:
1. Tạo một repository mới trên GitHub (miễn phí).
2. Tải toàn bộ thư mục `MultiScreenGridApp` lên GitHub.
3. GitHub sẽ tự động kích hoạt file `.github/workflows/build-apk.yml`.
4. Vào tab **Actions** trên GitHub, chờ khoảng 2-3 phút cho quá trình build hoàn tất.
5. Tải file **MultiGrid-Workspace-Debug-APK** về giải nén ra sẽ có sẵn file `app-debug.apk` để cài lên điện thoại ngay!

---

### Cách 3: Build bằng dòng lệnh (Gradle CLI)
Nếu bạn đã có JDK 17+ và Android SDK trên máy:
```bash
cd MultiScreenGridApp
./gradlew assembleDebug
```
File APK sẽ xuất hiện tại:
`app/build/outputs/apk/debug/app-debug.apk`
