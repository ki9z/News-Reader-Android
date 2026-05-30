# Project review and fixes

## Đã rà soát nhanh

- Android app dùng Kotlin; phần người dùng cuối chủ yếu theo Activity/Fragment/XML, một số màn hình xác thực/quản trị dùng Jetpack Compose.
- Kiến trúc chính theo MVVM: UI -> ViewModel -> Repository -> Remote/Local data source -> Room/NewsAPI/backend proxy.
- Backend Node.js/Express đóng vai trò NewsAPI proxy, reader extractor, cache/rate limit, OTP, Firebase push notification và lưu device token.

## Phần đã chỉnh trong bản hoàn thiện backend

1. Home hiển thị chuyên mục tin tức thay vì chỉ hiển thị nguồn báo.
2. Reader Mode/Download/Offline đã nối với DetailViewModel, lưu nội dung bài viết đầy đủ hơn.
3. Privacy Policy và Help & Support đã có nội dung/điều hướng sử dụng được, không còn chỉ là mục trống.
4. Android chuyển sang backend proxy mode: `NEWS_BASE_URL=http://10.0.2.2:8080/`, `NEWS_API_KEY` để trống ở app.
5. Thêm `app/google-services.json` và cấu hình `FIREBASE_WEB_CLIENT_ID` cho Google/Firebase sign-in.
6. Thêm Google Sign-In button trong LoginScreen, đăng nhập qua FirebaseAuth và gửi Firebase ID token về backend.
7. OTP email dùng SendGrid qua backend endpoint `/api/auth/otp/request` và `/api/auth/otp/verify`.
8. OTP SMS dùng Twilio Verify qua cùng endpoint với `channel=sms`.
9. Push notification đã có Firebase Admin SDK ở backend và app tự đăng ký FCM token lên backend khi chạy backend proxy.
10. Device store backend đã chuyển sang PostgreSQL khi có `DATABASE_URL`, fallback memory nếu chưa cấu hình database.
11. Cache/OTP cooldown/rate limit phụ đã dùng Upstash Redis REST khi có cấu hình, fallback memory nếu Redis lỗi.
12. Backend tự tạo bảng PostgreSQL: `users`, `devices`, `device_topics`, `otp_sessions`, `notification_logs`.
13. Thêm tài liệu chạy đầy đủ tại `RUN_WITH_FULL_SERVICES.md`, `RUN_WITH_YOUR_CONFIG.md`, `backend/README.md`.

## Phần vẫn cần lưu ý khi chạy thật

- URL PostgreSQL dạng `postgres.railway.internal` chỉ hoạt động khi backend chạy trong cùng Railway project. Nếu chạy backend local, hãy dùng public DATABASE_URL hoặc deploy backend lên Railway.
- `APP_CLIENT_TOKEN=change_this_demo_token` chỉ là token bảo vệ cơ bản cho đồ án. Nếu deploy public, nên đổi thành chuỗi riêng mạnh hơn.
- Admin module vẫn chủ yếu dùng Room/local data, phù hợp đồ án; chưa phải CMS production nhiều tài khoản/role/phân quyền trên server.
- Không public các file thật: `local.properties`, `backend/.env`, `backend/firebase-service-account.json`, `app/google-services.json`.

## Gợi ý test nhanh sau khi mở Android Studio

1. Chạy backend: `cd backend && npm install && npm run dev`, mở `http://localhost:8080/health`.
2. Chạy Android Emulator, Home phải tải được tin qua `http://10.0.2.2:8080/`.
3. Home -> kiểm tra hàng `Chuyên mục` và chọn từng chuyên mục.
4. Search, Detail, Reader Mode, Bookmark, Downloads, Reading History.
5. Forgot password -> gửi OTP email và xác thực OTP.
6. SMS OTP nếu có màn hình liên quan -> nhập số E.164, ví dụ `+84901234567`.
7. Login -> Tiếp tục với Google.
8. Profile -> bật thông báo, backend `/api/devices` phải thấy device token nếu backend và Firebase đã chạy đúng.
9. Backend -> gửi test notification bằng endpoint `/api/notifications/test`.
