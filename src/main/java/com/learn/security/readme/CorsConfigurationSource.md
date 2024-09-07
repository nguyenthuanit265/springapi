# Giải thích chi tiết

1. **@Bean**
    * Đánh dấu phương thức là một bean được quản lý bởi Spring container.

2. **CorsConfiguration**
    * Tạo đối tượng mới để cấu hình quy tắc CORS.

3. **setAllowedOrigins()**
    * Đặt danh sách các origin được phép gửi request.
    * Ví dụ: `"https://example.com"`, `"http://localhost:3000"`

4. **setAllowedMethods()**
    * Đặt các phương thức HTTP được phép.
    * Thường bao gồm: GET, POST, PUT, DELETE, OPTIONS

5. **setAllowedHeaders()**
    * Đặt các header được phép trong request.
    * Ví dụ: Authorization, Content-Type, X-Requested-With

6. **setExposedHeaders()**
    * Đặt các header mà server muốn expose cho client.

7. **setAllowCredentials(true)**
    * Cho phép gửi credentials (như cookies) trong CORS requests.

8. **setMaxAge(3600L)**
    * Đặt thời gian cache cho kết quả của preflight request (tính bằng giây).

9. **UrlBasedCorsConfigurationSource**
    * Tạo nguồn cấu hình CORS dựa trên URL.

10. **registerCorsConfiguration("/**", configuration)**
    * Áp dụng cấu hình CORS cho tất cả các đường dẫn.