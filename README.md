# 🛒 Demo Hệ thống Bán Hàng

Dự án phát triển hệ thống bán hàng với các chức năng quản lý người dùng, sản phẩm, đơn hàng và tài chính nội bộ. Hệ thống được xây dựng bằng **Java Spring Boot**, tích hợp **PostgreSQL** và **Redis** (qua Docker) để đảm bảo phân quyền người dùng hiệu quả và tối ưu hóa hiệu năng.

---

## 🧱 Công nghệ sử dụng

- 🌱 Java 23 / Spring Boot
- 🐘 PostgreSQL (qua Docker)
- ⚡ Redis (phân quyền và cache)
- 🐳 Docker, Docker Compose
- 🛠🛡️ Spring Security + Redis
- 🌐 RESTful API
- 📦 Maven

---

## 📌 Tính năng chính

### 1. 👤 Quản lý người dùng
- Phân loại: `SUPER_ADMIN`, `Admin`, `Nhà in`, `Sale`
- Chức năng: Quản lý CRUD, phân quyền qua Redis, hỗ trợ lọc và tìm kiếm
- Quyền hạn:
  - ✅ `SUPER_ADMIN`: Có toàn quyền với mọi vai trò, bao gồm cấp quyền cho `Admin`, `Nhà in`, `Sale` và các `Admin` khác.
  - ✅ `Admin`: Chỉ được thao tác với `Sale` và `Nhà in`.
  - 🚫 `Admin`: Không được phép thao tác với `Admin` khác hoặc `SUPER_ADMIN`.

### 2. 📦 Quản lý sản phẩm
- Mỗi sản phẩm bao gồm:
  - Giá gốc (dành cho nhà in)
  - Nhiều mức giá bán dựa trên rank của Sale và kích thước áo

### 3. 🛒 Quản lý đơn hàng

| Vai trò         | Quyền xử lý đơn hàng                                          |
|-----------------|---------------------------------------------------------------|
| SUPER_ADMIN     | Toàn quyền xem và thao tác với mọi đơn hàng, bao gồm cả đơn bị Sale hủy |
| Admin           | Xem toàn bộ đơn hàng, trừ các đơn bị Sale hủy                 |
| Sale            | Quản lý CRUD đơn hàng của mình, chỉ hủy đơn ở trạng thái cho phép |
| Nhà in          | Xem đơn hàng theo trạng thái: `Order`, `Processing`, ...      |

---
### 4. 💰 Quản lý tài chính nội bộ

- **Nạp tiền**: Sale gửi yêu cầu → Admin duyệt → Cộng tiền vào tài khoản Sale
- **Tạo đơn hàng**:
  - Trạng thái `Chờ thanh toán` → `Order`: Trừ tiền tài khoản Sale, cộng tiền tài khoản Admin
- **Thanh toán nhà in**: Khi đơn hàng chuyển sang trạng thái `Shipping`, cộng tiền vào tài khoản nhà in

---

## 🧾 Quy tắc đơn hàng

| Trạng thái         | Hành động cho phép                                  |
|--------------------|-----------------------------------------------------|
| `Chờ thanh toán`   | Có thể hủy hoặc chuyển sang `Order`                 |
| `Order`            | Có thể hủy, trừ tiền Sale, cộng tiền Admin          |
| `Processing`       | Không thể hủy                                       |
| `Shipping`         | Không thể hủy, cộng tiền cho nhà in                 |
| `Done`             | Hoàn thành, không thể thay đổi                      |

---

## 🔐 Luồng phân quyền động

Hệ thống sử dụng cơ chế phân quyền động dựa trên **JWT** và **Redis** để kiểm soát truy cập API một cách an toàn và hiệu quả. Quy trình xử lý như sau:

1. **Gửi yêu cầu**: Người dùng gửi yêu cầu API kèm **JWT token** trong header.
2. **Xác thực JWT**: `JwtAuthenticationFilter` phân tích token, trích xuất thông tin người dùng và tạo đối tượng `Authentication` cho Spring Security.
3. **Kiểm tra quyền**: Spring Security sử dụng `CustomPermissionEvaluator` để đánh giá quyền truy cập dựa trên vai trò và hành động yêu cầu.
4. **Tra cứu Redis**: `RedisService` truy vấn thông tin quyền được lưu trữ trong Redis để đảm bảo hiệu suất cao và độ trễ thấp.
5. **Quyết định truy cập**: Nếu quyền hợp lệ, yêu cầu được phép thực thi; nếu không, hệ thống trả về lỗi 403 (Forbidden).

**Sơ đồ luồng phân quyền**:

```plaintext
Người dùng → [JWT Token] → JwtAuthenticationFilter → Spring Security
                                       ↓
                              CustomPermissionEvaluator
                                       ↓
                                  RedisService
                                       ↓
                          [Cho phép ✅ / Từ chối ❌] → API
```

---

## 🚀 Hướng dẫn cài đặt, chạy bằng Docker và test bằng Postman

```bash
git clone https://github.com/toobidu/Demo.git
cd Demo
```

```bash
docker build -t demo-app .
```

```bash
docker-compose up --build
```
🔗 [Postman Collection](https://www.postman.co/workspace/My-Workspace~02e24c2e-eb7c-48e1-8ed3-5079b32df085/collection/40920755-457b1317-f589-4667-a883-a401e013c594?action=share&creator=40920755)