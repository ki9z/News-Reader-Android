# Bản sửa yêu cầu tăng view khi mở bài gốc

## Yêu cầu đã xử lý
Khi người dùng thực sự mở bài gốc, hệ thống sẽ tăng `view` của bài viết trong bảng `articles` của Room DB thêm 1.

## Logic sau khi sửa
- Nếu mở WebView ở chế độ bài gốc: tăng view 1 lần.
- Nếu đang ở Reader Mode và bấm chuyển sang bài gốc: tăng view 1 lần.
- Nếu bấm mở bằng trình duyệt ngoài từ màn WebView/Reader: tăng view 1 lần.
- Retry tải lại trang hoặc bấm mở trình duyệt sau khi đã ghi nhận sẽ không cộng lặp trong cùng một lần mở màn hình.
- Nếu bài viết chưa tồn tại trong bảng `articles`, app tự lưu bài viết vào DB trước, sau đó tăng view.
- Khi ghi lịch sử đọc, bookmark, download hoặc cập nhật bài trong admin, code giữ lại `view`, `status`, `createdAt` cũ để tránh reset lượt xem về 0.

## Các file đã sửa
- `app/src/main/src/com/ui/reader/ArticleWebViewFragment.kt`
- `app/src/main/src/com/viewmodel/detail/DetailViewModel.kt`
- `app/src/main/src/com/data/repository/NewsRepository.kt`
- `app/src/main/src/com/data/repository/NewsRepositoryImpl.kt`
- `app/src/main/src/com/data/repository/MockNewsRepository.kt`
- `app/src/main/src/com/data/local/dao/ArticleDao.kt`
- `app/src/main/src/com/data/repository/ProfileRepository.kt`
- `app/src/main/src/com/data/local/source/LocalNewsDataSourceImpl.kt`
- `app/src/main/src/com/data/repository/ArticleRepository.kt`

## Ghi chú kiểm thử
Trong môi trường ChatGPT hiện tại không tải được Gradle Wrapper vì không có Internet tới `services.gradle.org`, nên chưa chạy được lệnh build trực tiếp tại đây. Bạn mở project bằng Android Studio hoặc chạy:

```bash
./gradlew :app:assembleDebug
```

Sau đó kiểm tra bằng cách:
1. Mở app.
2. Vào chi tiết một bài báo.
3. Bấm mở bài gốc.
4. Quay lại màn admin/thống kê hoặc kiểm tra bảng `articles`.
5. Trường `view` của bài có URL tương ứng phải tăng thêm 1.
