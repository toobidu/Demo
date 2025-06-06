
-- Bảng users
CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    user_name      VARCHAR(100) UNIQUE NOT NULL,
    first_name     VARCHAR(100)        NOT NULL,
    last_name      VARCHAR(100)        NOT NULL,
    email         VARCHAR(255) UNIQUE NOT NULL,
    address       VARCHAR(255),
    phone         VARCHAR(20) UNIQUE  NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    full_name     VARCHAR(255),
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_users_user_name ON users (user_name);
CREATE INDEX idx_users_email ON users (email);


-- Bảng roles (vai trò)
CREATE TABLE roles
(
    id          SERIAL PRIMARY KEY,
    role_name   VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

CREATE INDEX idx_roles_role_name ON roles (role_name);


-- Bảng permissions (quyền)
CREATE TABLE permissions
(
    id              SERIAL PRIMARY KEY,
    permission_name VARCHAR(100) UNIQUE NOT NULL,
    description     TEXT
);

CREATE INDEX idx_permissions_permission_name ON permissions (permission_name);


-- Bảng ánh xạ role - permission (mối quan hệ nhiều nhiều)
CREATE TABLE role_permissions
(
    role_id       INT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id INT NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role ON role_permissions (role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions (permission_id);


-- Bảng ánh xạ user - role (mối quan hệ nhiều nhiều)
CREATE TABLE user_roles
(
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles (user_id);
CREATE INDEX idx_user_roles_role ON user_roles (role_id);


-- Bảng wallets (1 user nhiều ví)
CREATE TABLE wallets
(
    id          SERIAL PRIMARY KEY,
    user_id     INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    wallet_name VARCHAR(100),
    balance     NUMERIC(18, 2)           DEFAULT 0 CHECK (balance >= 0),
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_wallets_user ON wallets (user_id);


-- Bảng products
CREATE TABLE products
(
    id          SERIAL PRIMARY KEY,
    sku         VARCHAR(100) UNIQUE NOT NULL,
    name        VARCHAR(255)        NOT NULL,
    base_price  NUMERIC(18, 2)      NOT NULL, -- Giá gốc nhập từ Nhà in
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_products_sku ON products (sku);
CREATE INDEX idx_products_name ON products (name);

CREATE TABLE product_prices (
                                id SERIAL PRIMARY KEY,
                                product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
                                rank VARCHAR(50) NOT NULL, -- Gold, Platinum, Diamond
                                size VARCHAR(20) NOT NULL, -- S, M, L, XL
                                price NUMERIC(18,2) NOT NULL CHECK (price >= 0),
                                UNIQUE(product_id, rank, size)
);
CREATE INDEX idx_product_prices_product ON product_prices(product_id);



-- Bảng product_attributes (mô hình EAV để lưu thuộc tính linh hoạt)
CREATE TABLE product_attributes
(
    id              SERIAL PRIMARY KEY,
    product_id      INT          NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    attribute_key   VARCHAR(100) NOT NULL, -- ví dụ: size, color, rank_gold_price,...
    attribute_value VARCHAR(255) NOT NULL,
    UNIQUE (product_id, attribute_key, attribute_value)
);

CREATE INDEX idx_product_attributes_product ON product_attributes (product_id);
CREATE INDEX idx_product_attributes_key_value ON product_attributes (attribute_key, attribute_value);


-- Bảng orders
CREATE TABLE orders
(
    id           SERIAL PRIMARY KEY,
    user_id      INT            NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    wallet_id    INT            NOT NULL REFERENCES wallets (id),
    status       VARCHAR(50)    NOT NULL CHECK (status IN ('pending_payment', 'order', 'processing', 'shipping', 'done',
                                                           'cancelled')),
    total_amount NUMERIC(18, 2) NOT NULL CHECK (total_amount >= 0),
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_wallet ON orders (wallet_id);


-- Bảng order_items (một đơn có nhiều món)
CREATE TABLE order_items
(
    id              SERIAL PRIMARY KEY,
    order_id        INT            NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id      INT            NOT NULL REFERENCES products (id),
    quantity        INT            NOT NULL CHECK (quantity > 0),
    original_price      NUMERIC(18, 2) NOT NULL CHECK (original_price >= 0),
    attribute_key   VARCHAR(100), -- ví dụ: size, rank,...
    attribute_value VARCHAR(255), -- ví dụ: M, Gold,...
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_order_items_order ON order_items (order_id);
CREATE INDEX idx_order_items_product ON order_items (product_id);


-- Bảng transactions ghi lại các giao dịch nội bộ (ví dụ: nạp tiền, thanh toán đơn, chuyển tiền)
CREATE TABLE transactions
(
    id               SERIAL PRIMARY KEY,
    from_wallet_id   INT REFERENCES wallets (id),
    to_wallet_id     INT REFERENCES wallets (id),
    amount           NUMERIC(18, 2) NOT NULL CHECK (amount > 0),
    transaction_type VARCHAR(50)    NOT NULL CHECK (transaction_type IN
                                                    ('deposit', 'order_payment', 'payment_to_print_house')),
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_transactions_from_wallet ON transactions (from_wallet_id);
CREATE INDEX idx_transactions_to_wallet ON transactions (to_wallet_id);


-- Trigger cập nhật updated_at cho users, wallets, products, orders khi UPDATE
CREATE
OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= NOW();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_update_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_wallets_updated_at
    BEFORE UPDATE
    ON wallets
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_products_updated_at
    BEFORE UPDATE
    ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trg_update_orders_updated_at
    BEFORE UPDATE
    ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE OR REPLACE FUNCTION update_wallet_balance()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.from_wallet_id IS NOT NULL THEN
UPDATE wallets
SET balance = balance - NEW.amount,
    updated_at = NOW()
WHERE id = NEW.from_wallet_id;
END IF;

    IF NEW.to_wallet_id IS NOT NULL THEN
UPDATE wallets
SET balance = balance + NEW.amount,
    updated_at = NOW()
WHERE id = NEW.to_wallet_id;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_wallet_balance
    AFTER INSERT ON transactions
    FOR EACH ROW
    EXECUTE FUNCTION update_wallet_balance();


CREATE OR REPLACE FUNCTION check_order_status_transition()
RETURNS TRIGGER AS $$
BEGIN
    -- Kiểm tra hủy đơn
    IF NEW.status = 'cancelled' THEN
        IF OLD.status NOT IN ('pending_payment', 'order') THEN
            RAISE EXCEPTION 'Đơn hàng chỉ được hủy khi trạng thái là pending_payment hoặc order';
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 1. roles
INSERT INTO roles (role_name, description) VALUES
                                               ('admin', 'Quản trị hệ thống'),
                                               ('sale', 'Nhân viên bán hàng'),
                                               ('print_house', 'Nhà in');

-- 2. permissions
INSERT INTO permissions (permission_name, description) VALUES
                                                           ('view_all_orders', 'Xem tất cả đơn hàng'),
                                                           ('manage_own_orders', 'Quản lý đơn hàng của mình'),
                                                           ('view_print_house_orders', 'Xem đơn hàng liên quan nhà in'),
                                                           ('cancel_order', 'Hủy đơn hàng'),
                                                           ('manage_users', 'Quản lý người dùng');

-- 3. role_permissions
-- admin có tất cả quyền
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.role_name = 'admin';

-- sale có quyền quản lý đơn hàng và hủy đơn
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.role_name = 'sale' AND p.permission_name IN ('manage_own_orders', 'cancel_order');

-- print_house chỉ được xem đơn hàng liên quan
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.role_name = 'print_house' AND p.permission_name = 'view_print_house_orders';

-- 4. users
INSERT INTO users (user_name, first_name, last_name, email, address, phone, password_hash, full_name) VALUES
                                                                                                       ('admin01', 'Nguyen', 'Admin', 'admin01@example.com', '123 Admin St', '0900000001', 'hash_admin01', 'Nguyen Admin'),
                                                                                                       ('sale01', 'Tran', 'Sale', 'sale01@example.com', '456 Sale Ave', '0900000002', 'hash_sale01', 'Tran Sale'),
                                                                                                       ('sale02', 'Le', 'Sale', 'sale02@example.com', '789 Sale Rd', '0900000003', 'hash_sale02', 'Le Sale'),
                                                                                                       ('print01', 'Pham', 'Print', 'print01@example.com', '321 Print Blvd', '0900000004', 'hash_print01', 'Pham Print'),
                                                                                                       ('print02', 'Hoang', 'Print', 'print02@example.com', '654 Print Ln', '0900000005', 'hash_print02', 'Hoang Print');

-- 5. user_roles
-- admin01 là admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name = 'admin01' AND r.role_name = 'admin';

-- sale01, sale02 là sale
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name IN ('sale01', 'sale02') AND r.role_name = 'sale';

-- print01, print02 là nhà in
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.user_name IN ('print01', 'print02') AND r.role_name = 'print_house';

-- 6. wallets (mỗi user có 1 ví)
INSERT INTO wallets (user_id, wallet_name, balance) VALUES
                                                        ((SELECT id FROM users WHERE user_name = 'admin01'), 'Admin Wallet', 10000),
                                                        ((SELECT id FROM users WHERE user_name = 'sale01'), 'Sale Wallet 1', 500000),  -- tăng từ 5000 lên 500000
                                                        ((SELECT id FROM users WHERE user_name = 'sale02'), 'Sale Wallet 2', 500000),  -- tăng tương tự
                                                        ((SELECT id FROM users WHERE user_name = 'print01'), 'Print Wallet 1', 0),
                                                        ((SELECT id FROM users WHERE user_name = 'print02'), 'Print Wallet 2', 0);


-- 7. products
INSERT INTO products (sku, name, base_price, description) VALUES
                                                              ('PRD001', 'Áo Thun Basic', 100000, 'Áo thun cotton basic'),
                                                              ('PRD002', 'Áo Sơ Mi Nam', 200000, 'Áo sơ mi nam tay dài'),
                                                              ('PRD003', 'Quần Jean', 300000, 'Quần jean nam chất liệu denim'),
                                                              ('PRD004', 'Mũ Lưỡi Trai', 50000, 'Mũ lưỡi trai unisex'),
                                                              ('PRD005', 'Giày Thể Thao', 400000, 'Giày thể thao thời trang');

-- 8. product_attributes (ví dụ size, màu sắc)
INSERT INTO product_attributes (product_id, attribute_key, attribute_value) VALUES
                                                                                ((SELECT id FROM products WHERE sku = 'PRD001'), 'size', 'S'),
                                                                                ((SELECT id FROM products WHERE sku = 'PRD001'), 'size', 'M'),
                                                                                ((SELECT id FROM products WHERE sku = 'PRD001'), 'size', 'L'),
                                                                                ((SELECT id FROM products WHERE sku = 'PRD001'), 'color', 'Đen'),
                                                                                ((SELECT id FROM products WHERE sku = 'PRD001'), 'color', 'Trắng');

-- 9. product_prices (giá theo rank và size)
INSERT INTO product_prices (product_id, rank, size, price) VALUES
                                                               ((SELECT id FROM products WHERE sku = 'PRD001'), 'Gold', 'S', 120000),
                                                               ((SELECT id FROM products WHERE sku = 'PRD001'), 'Gold', 'M', 125000),
                                                               ((SELECT id FROM products WHERE sku = 'PRD001'), 'Platinum', 'S', 130000),
                                                               ((SELECT id FROM products WHERE sku = 'PRD001'), 'Diamond', 'L', 150000),
                                                               ((SELECT id FROM products WHERE sku = 'PRD002'), 'Gold', 'M', 220000);

-- 10. orders
INSERT INTO orders (user_id, wallet_id, status, total_amount) VALUES
                                                                  ((SELECT id FROM users WHERE user_name = 'sale01'), (SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 1'), 'pending_payment', 250000),
                                                                  ((SELECT id FROM users WHERE user_name = 'sale01'), (SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 1'), 'order', 150000),
                                                                  ((SELECT id FROM users WHERE user_name = 'sale02'), (SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 2'), 'processing', 300000),
                                                                  ((SELECT id FROM users WHERE user_name = 'sale02'), (SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 2'), 'shipping', 400000),
                                                                  ((SELECT id FROM users WHERE user_name = 'sale02'), (SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 2'), 'done', 100000);

-- 11. order_items
INSERT INTO order_items (order_id, product_id, quantity, original_price, attribute_key, attribute_value) VALUES
                                                                                                         ((SELECT id FROM orders LIMIT 1), (SELECT id FROM products WHERE sku = 'PRD001'), 2, 120000, 'size', 'S'),
((SELECT id FROM orders LIMIT 1), (SELECT id FROM products WHERE sku = 'PRD004'), 1, 50000, NULL, NULL),
((SELECT id FROM orders OFFSET 1 LIMIT 1), (SELECT id FROM products WHERE sku = 'PRD002'), 1, 220000, 'size', 'M'),
((SELECT id FROM orders OFFSET 2 LIMIT 1), (SELECT id FROM products WHERE sku = 'PRD003'), 3, 300000, NULL, NULL),
((SELECT id FROM orders OFFSET 3 LIMIT 1), (SELECT id FROM products WHERE sku = 'PRD005'), 1, 400000, NULL, NULL);

-- 12. transactions
INSERT INTO transactions (from_wallet_id, to_wallet_id, amount, transaction_type) VALUES
                                                                                      ((SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 1'), (SELECT id FROM wallets WHERE wallet_name = 'Admin Wallet'), 250000, 'order_payment'),
                                                                                      (NULL, (SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 1'), 500000, 'deposit'),
                                                                                      ((SELECT id FROM wallets WHERE wallet_name = 'Admin Wallet'), (SELECT id FROM wallets WHERE wallet_name = 'Print Wallet 1'), 100000, 'payment_to_print_house'),
                                                                                      ((SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 2'), (SELECT id FROM wallets WHERE wallet_name = 'Admin Wallet'), 150000, 'order_payment'),
                                                                                      (NULL, (SELECT id FROM wallets WHERE wallet_name = 'Sale Wallet 2'), 300000, 'deposit');



INSERT INTO products (sku, name, base_price, description) VALUES
                                                              ('SKU001', 'Green Tea Classic', 50000, 'Trà xanh truyền thống, thanh mát'),
                                                              ('SKU002', 'Black Coffee Premium', 70000, 'Cà phê đen nguyên chất, hương vị đậm đà'),
                                                              ('SKU003', 'Milk Tea Special', 60000, 'Trà sữa thơm ngon, béo ngậy'),
                                                              ('SKU004', 'Fruit Juice Mix', 45000, 'Nước ép trái cây hỗn hợp tươi mát'),
                                                              ('SKU005', 'Chocolate Drink', 55000, 'Đồ uống cacao sữa thơm ngon');

-- Product 1 (Green Tea Classic)
INSERT INTO product_attributes (product_id, attribute_key, attribute_value) VALUES
                                                                                ((SELECT id FROM products WHERE sku = 'SKU001'), 'size', 'S'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU001'), 'size', 'M'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU001'), 'color', 'Green'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU001'), 'material', 'Tea Leaves'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU001'), 'rank', 'Gold');

-- Product 2 (Black Coffee Premium)
INSERT INTO product_attributes (product_id, attribute_key, attribute_value) VALUES
                                                                                ((SELECT id FROM products WHERE sku = 'SKU002'), 'size', 'M'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU002'), 'size', 'L'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU002'), 'color', 'Black'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU002'), 'material', 'Coffee Beans'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU002'), 'rank', 'Platinum');

-- Product 3 (Milk Tea Special)
INSERT INTO product_attributes (product_id, attribute_key, attribute_value) VALUES
                                                                                ((SELECT id FROM products WHERE sku = 'SKU003'), 'size', 'S'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU003'), 'size', 'M'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU003'), 'size', 'L'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU003'), 'color', 'Brown'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU003'), 'material', 'Milk, Tea Leaves'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU003'), 'rank', 'Diamond');

-- Product 4 (Fruit Juice Mix)
INSERT INTO product_attributes (product_id, attribute_key, attribute_value) VALUES
                                                                                ((SELECT id FROM products WHERE sku = 'SKU004'), 'size', 'M'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU004'), 'color', 'Orange'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU004'), 'material', 'Mixed Fruits');

-- Product 5 (Chocolate Drink)
INSERT INTO product_attributes (product_id, attribute_key, attribute_value) VALUES
                                                                                ((SELECT id FROM products WHERE sku = 'SKU005'), 'size', 'S'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU005'), 'size', 'M'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU005'), 'color', 'Dark Brown'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU005'), 'material', 'Cocoa, Milk'),
                                                                                ((SELECT id FROM products WHERE sku = 'SKU005'), 'rank', 'Gold');

