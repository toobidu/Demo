-- Thêm dữ liệu vào bảng roles
INSERT INTO roles (role_name, description)
VALUES ('SUPER_ADMIN', 'Đấng toàn năng.'),
       ('ADMIN', 'Người quản trị hệ thống, có toàn quyền quản lý.'),
       ('SALE', 'Nhân viên bán hàng, phụ trách xử lý đơn và chăm sóc khách hàng.'),
       ('PRINTER_HOUSE', 'Bộ phận in ấn, xử lý và sản xuất tài liệu, đơn hàng in.');

-- Thêm dữ liệu vào bảng permissions
INSERT INTO permissions (id, permission_name, description) VALUES
-- User Management
(1, 'view_users', 'Xem danh sách người dùng'),
(2, 'create_user', 'Tạo người dùng'),
(3, 'update_user', 'Cập nhật người dùng'),
(4, 'delete_user', 'Xóa người dùng'),

-- Role Management
(5, 'view_roles', 'Xem danh sách vai trò'),
(6, 'create_role', 'Tạo vai trò'),
(7, 'update_role', 'Cập nhật vai trò'),
(8, 'delete_role', 'Xóa vai trò'),

-- Permission Management
(9, 'view_permissions', 'Xem danh sách quyền'),
(10, 'create_permission', 'Tạo quyền'),
(11, 'update_permission', 'Cập nhật quyền'),
(12, 'delete_permission', 'Xóa quyền'),

-- Product Management
(13, 'view_product', 'Xem sản phẩm'),
(14, 'view_products', 'Xem danh sách sản phẩm'),
(15, 'create_product', 'Tạo sản phẩm'),
(16, 'update_product', 'Cập nhật sản phẩm'),
(17, 'delete_product', 'Xóa sản phẩm'),

-- Product Attributes
(18, 'view_product_attribute', 'Xem thuộc tính sản phẩm'),
(19, 'view_product_attributes', 'Xem danh sách thuộc tính sản phẩm'),
(20, 'create_product_attribute', 'Tạo thuộc tính sản phẩm'),
(21, 'update_product_attribute', 'Cập nhật thuộc tính sản phẩm'),
(22, 'delete_product_attribute', 'Xóa thuộc tính sản phẩm'),

-- Product Prices
(23, 'view_product_price', 'Xem giá sản phẩm'),
(24, 'view_product_prices', 'Xem danh sách giá sản phẩm'),
(25, 'create_product_price', 'Tạo giá sản phẩm'),
(26, 'update_product_price', 'Cập nhật giá sản phẩm'),
(27, 'delete_product_price', 'Xóa giá sản phẩm'),

-- Order Management
(28, 'view_order', 'Xem đơn hàng'),
(29, 'create_order', 'Tạo đơn hàng'),
(30, 'update_order', 'Cập nhật đơn hàng'),
(31, 'cancel_order', 'Hủy đơn hàng'),
(32, 'view_user_orders', 'Xem đơn hàng của người dùng'),
(33, 'view_admin_orders', 'Xem đơn hàng của admin'),
(34, 'view_printhouse_orders', 'Xem đơn hàng của nhà in'),

-- Order Items
(35, 'view_order_item', 'Xem chi tiết đơn hàng'),
(36, 'view_order_items', 'Xem danh sách chi tiết đơn hàng'),
(37, 'create_order_item', 'Tạo chi tiết đơn hàng'),
(38, 'update_order_item', 'Cập nhật chi tiết đơn hàng'),
(39, 'delete_order_item', 'Xóa chi tiết đơn hàng'),

-- Financial & Status Permissions (bổ sung mới)
(40, 'change_order_status', 'Thay đổi trạng thái đơn hàng (dành cho admin)'),
(41, 'deduct_balance_on_order', 'Trừ tiền tài khoản Sale khi đặt đơn'),
(42, 'credit_admin_on_payment', 'Cộng tiền cho Admin khi đơn hàng xác nhận'),
(43, 'credit_printhouse_on_shipping', 'Cộng tiền cho Nhà in khi giao hàng'),
(44, 'refund_money_on_cancelled_order', 'Hoàn tiền cho Sale khi hủy đơn'),
(45, 'deposit_money', 'Sale gửi yêu cầu nạp tiền'),
(46, 'approve_deposit', 'Admin duyệt yêu cầu nạp tiền'),
(47, 'view_wallet', 'Xem ví của người dùng');