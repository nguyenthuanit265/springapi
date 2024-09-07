```
http.cors(cors -> cors.configurationSource(corsConfigurationSource));
```

- Đoạn code này cấu hình CORS (Cross-Origin Resource Sharing) cho ứng dụng:

- CORS là một cơ chế cho phép nhiều tài nguyên khác nhau (fonts, JavaScript, v.v.) trên một trang web được yêu cầu từ
  một
  domain khác với domain của tài nguyên gốc.
- Phương thức cors() được sử dụng để cấu hình CORS trong Spring Security.

## Lambda expression cors -> cors.configurationSource(corsConfigurationSource) được sử dụng để cấu hình nguồn cấu hình CORS

##### corsConfigurationSource là một bean được định nghĩa ở nơi khác trong ứng dụng. Nó xác định các quy tắc CORS cụ thể, chẳng hạn như:

* Những origin nào được phép truy cập tài nguyên.
* Những phương thức HTTP nào được phép (GET, POST, etc.).
* Những header nào được phép trong request.
* Liệu credentials có được phép hay không.

Bằng cách sử dụng một nguồn cấu hình tùy chỉnh, bạn có thể kiểm soát chi tiết cách ứng dụng của mình xử lý các yêu cầu
cross-origin.
Cấu hình CORS đúng cách rất quan trọng để đảm bảo rằng API của bạn có thể được truy cập an toàn từ các ứng dụng client
chạy trên các domain khác nhau, đồng thời vẫn duy trì tính bảo mật cần thiết.