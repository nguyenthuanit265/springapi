```java
http.csrf(AbstractHttpConfigurer::disable);
```

- Đoạn code này vô hiệu hóa bảo vệ CSRF (Cross-Site Request Forgery) cho ứng dụng. Hãy phân tích chi tiết:

- CSRF là một loại tấn công web, trong đó kẻ tấn công lừa người dùng đã xác thực thực hiện các hành động không mong muốn
  trên một ứng dụng web mà họ đã đăng nhập.
- Trong Spring Security, bảo vệ CSRF được bật mặc định để ngăn chặn các cuộc tấn công này.
- AbstractHttpConfigurer::disable là một method reference trong Java, nó trỏ đến phương thức disable() của lớp
  AbstractHttpConfigurer. Khi được sử dụng, nó vô hiệu hóa hoàn toàn cấu hình CSRF.

#### Vô hiệu hóa CSRF có thể hữu ích trong một số trường hợp, ví dụ:

- Khi xây dựng API stateless không sử dụng cookies cho xác thực phiên.
- Khi sử dụng JWT hoặc token-based authentication.
- Trong môi trường phát triển hoặc testing.

Tuy nhiên, vô hiệu hóa CSRF có thể làm tăng nguy cơ bảo mật nếu không được xử lý cẩn thận. Nên cân nhắc kỹ lưỡng trước
khi áp dụng trong môi trường sản xuất.