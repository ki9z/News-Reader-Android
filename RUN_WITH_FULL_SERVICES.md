# Chạy News Reader với backend đầy đủ

## 1. Backend

```bash
cd backend
npm install
npm run dev
```

Kiểm tra backend:

```bash
curl http://localhost:8080/health
```

Khi chạy trên Android Emulator, app gọi backend qua:

```properties
NEWS_BASE_URL=http://10.0.2.2:8080/
BACKEND_APP_TOKEN=change_this_demo_token
NEWS_API_KEY=
```

## 2. Android app

File Firebase client đã đặt tại:

```text
app/google-services.json
```

Build/run app bằng Android Studio hoặc:

```bash
./gradlew :app:assembleDebug
```

## 3. Các dịch vụ đã được nối vào code

- NewsAPI: backend giữ API key, Android gọi qua backend proxy.
- PostgreSQL: backend tự tạo bảng `users`, `devices`, `device_topics`, `otp_sessions`, `notification_logs` khi khởi động.
- Upstash Redis REST: dùng cho cache bài viết/tin tức, OTP email, cooldown, giới hạn thử OTP.
- Twilio Verify: dùng cho OTP SMS qua endpoint `/api/auth/otp/request` và `/api/auth/otp/verify` với `channel=sms`.
- SendGrid: dùng cho OTP email qua endpoint `/api/auth/otp/request` và `/api/auth/otp/verify` với `channel=email`.
- Firebase Admin SDK: dùng để gửi push notification FCM qua `/api/notifications/test` và breaking-news job.
- Firebase Android: app nhận FCM token, đăng ký device token lên backend khi đang dùng backend proxy.

## 4. Lưu ý Railway PostgreSQL

Nếu dùng URL dạng `postgres.railway.internal`, backend phải được deploy trong cùng Railway project thì mới kết nối được. Nếu chạy backend trên máy local, hãy đổi sang public DATABASE_URL của Railway. Nếu URL internal không kết nối được, backend vẫn chạy và tự fallback memory cho device store.

## 5. Bảo mật

Không commit các file/cấu hình thật này lên GitHub công khai:

- `local.properties`
- `backend/.env`
- `backend/firebase-service-account.json`
- `app/google-services.json` nếu repo public
