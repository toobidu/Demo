# 🛒 Demo Hệ thống Bán Hàng

Dự án xây dựng hệ thống bán hàng với các chức năng quản lý người dùng, sản phẩm, đơn hàng và tài chính nội bộ. Hệ thống được phát triển bằng **Java Spring Boot**, tích hợp **PostgreSQL** và **Redis** (qua Docker), phục vụ vai trò phân quyền người dùng và tăng hiệu năng.

---

## 🧱 Công nghệ sử dụng

- ✅ Java 23 / Spring Boot
- 🐘 PostgreSQL (qua Docker)
- ⚡ Redis (phân quyền và cache)
- 🐳 Docker, Docker Compose
- 🛠️ Spring Security + Redis
- 🌐 RESTful API
- 📦 Maven

---

## 📌 Tính năng chính

### 1. 👤 Quản lý người dùng
- Phân loại: `Admin`, `Nhà in`, `Sale`
- Chức năng: CRUD, phân quyền với Redis, lọc và tìm kiếm

### 2. 📦 Quản lý sản phẩm
- Mỗi sản phẩm có:
    - 1 giá gốc (nhà in)
    - Nhiều giá bán theo rank của Sale và size áo

### 3. 🛒 Quản lý đơn hàng

| Vai trò     | Quyền xử lý đơn hàng                                          |
|-------------|---------------------------------------------------------------|
| Admin       | Xem toàn bộ đơn trừ các đơn bị Sale hủy                       |
| Sale        | CRUD đơn hàng của mình, chỉ hủy đơn khi ở trạng thái cho phép |
| Nhà in      | Xem đơn hàng theo trạng thái `Order`, `Processing`, ...       |

### 4. 💰 Quản lý tài chính nội bộ

- **Nạp tiền:** Sale yêu cầu → Admin duyệt → cộng vào tài khoản
- **Tạo đơn hàng:**
    - Chờ thanh toán → Order: trừ tiền Sale, cộng tiền Admin
- **Thanh toán nhà in:** Khi đơn sang trạng thái `Shipping`, cộng tiền nhà in

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

## 🚀 Hướng dẫn cài đặt và chạy bằng Docker

### 1. Clone dự án

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