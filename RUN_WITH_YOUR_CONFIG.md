# Run configuration

Bản này đã cấu hình theo hướng backend proxy để app Android không phải giữ NewsAPI key.

## 1. Chạy backend

```bash
cd backend
npm install
npm run dev
```

Kiểm tra:

```bash
curl http://localhost:8080/health
```

## 2. Chạy Android Emulator

`local.properties` đã đặt:

```properties
NEWS_BASE_URL=http://10.0.2.2:8080/
NEWS_API_KEY=
BACKEND_APP_TOKEN=change_this_demo_token
NEWS_API_DAILY_LIMIT=90
```

Mở Android Studio, Sync Gradle, sau đó Run app.

## 3. Nếu chạy điện thoại thật

Điện thoại thật không gọi được `10.0.2.2`. Hãy deploy backend hoặc dùng HTTPS tunnel, rồi đổi:

```properties
NEWS_BASE_URL=https://your-backend-url/
```

## 4. Lưu ý bảo mật

Không commit hoặc public các file thật:

```text
local.properties
backend/.env
backend/firebase-service-account.json
app/google-services.json
```
