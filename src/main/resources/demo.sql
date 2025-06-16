-- Bảng users (unchanged)
CREATE TABLE users (
                       id            SERIAL PRIMARY KEY,
                       user_name     VARCHAR(100) UNIQUE NOT NULL,
                       first_name    VARCHAR(100) NOT NULL,
                       last_name     VARCHAR(100) NOT NULL,
                       email         VARCHAR(255) UNIQUE NOT NULL,
                       address       VARCHAR(255),
                       phone         VARCHAR(20) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       type_account  VARCHAR(50) NOT NULL CHECK (type_account IN ('admin', 'sale', 'print_house')),
                       rank          VARCHAR(50) NOT NULL, -- Liên kết với dictionary_items (code của user_rank)
                       created_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                       updated_at    TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_users_user_name ON users (user_name);
CREATE INDEX idx_users_email ON users (email);

-- Bảng roles (unchanged)
CREATE TABLE roles (
                       id          INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       role_name   VARCHAR(100) UNIQUE NOT NULL,
                       description TEXT
);

CREATE INDEX idx_roles_role_name ON roles (role_name);

-- Bảng permissions (unchanged)
CREATE TABLE permissions (
                             id              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             permission_name VARCHAR(100) UNIQUE NOT NULL,
                             description     TEXT
);

CREATE INDEX idx_permissions_permission_name ON permissions (permission_name);

-- Bảng role_permissions (unchanged)
CREATE TABLE role_permissions (
                                  role_id       INT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
                                  permission_id INT NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
                                  PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role ON role_permissions (role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions (permission_id);

-- Bảng user_roles (unchanged)
CREATE TABLE user_roles (
                            user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                            role_id INT NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user ON user_roles (user_id);
CREATE INDEX idx_user_roles_role ON user_roles (role_id);

-- Bảng wallets (unchanged)
CREATE TABLE wallets (
                         id          SERIAL PRIMARY KEY,
                         user_id     INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                         wallet_name VARCHAR(100),
                         balance     NUMERIC(18, 2) DEFAULT 0 CHECK (balance >= 0),
                         created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                         updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_wallets_user ON wallets (user_id);

-- Bảng products (unchanged)
CREATE TABLE products (
                          id          SERIAL PRIMARY KEY,
                          sku         VARCHAR(100) UNIQUE NOT NULL,
                          name        VARCHAR(255) NOT NULL,
                          base_price  NUMERIC(18, 2) NOT NULL CHECK (base_price >= 0),
                          description TEXT,
                          created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                          updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_products_sku ON products (sku);
CREATE INDEX idx_products_name ON products (name);

-- Bảng product_prices (unchanged)
CREATE TABLE product_prices (
                                id         SERIAL PRIMARY KEY,
                                product_id INT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
                                rank       VARCHAR(50) NOT NULL, -- Liên kết với dictionary_items (code của user_rank)
                                size       VARCHAR(20) NOT NULL, -- Liên kết với dictionary_items (code của product_size)
                                price      NUMERIC(18, 2) NOT NULL CHECK (price >= 0),
                                UNIQUE (product_id, rank, size)
);

CREATE INDEX idx_product_prices_product ON product_prices (product_id);

-- Bảng product_attributes (unchanged)
CREATE TABLE product_attributes (
                                    id              SERIAL PRIMARY KEY,
                                    product_id      INT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
                                    attribute_key   VARCHAR(100) NOT NULL,
                                    attribute_value VARCHAR(255) NOT NULL,
                                    UNIQUE (product_id, attribute_key, attribute_value)
);

CREATE INDEX idx_product_attributes_product ON product_attributes (product_id);
CREATE INDEX idx_product_attributes_key_value ON product_attributes (attribute_key, attribute_value);

-- Bảng dictionaries (unchanged)
CREATE TABLE dictionaries (
                              id   SERIAL PRIMARY KEY,
                              code TEXT UNIQUE NOT NULL,
                              name TEXT NOT NULL
);

-- Bảng dictionary_items (unchanged)
CREATE TABLE dictionary_items (
                                  id            SERIAL PRIMARY KEY,
                                  dictionary_id INTEGER NOT NULL REFERENCES dictionaries (id) ON DELETE CASCADE,
                                  code          TEXT NOT NULL,
                                  name          TEXT NOT NULL,
                                  UNIQUE (dictionary_id, code)
);

-- Bảng orders (modified: added print_price, ship_price, pre_ship_price)
CREATE TABLE orders (
                        id              SERIAL PRIMARY KEY,
                        user_id         INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                        wallet_id       INT NOT NULL REFERENCES wallets (id),
                        status          VARCHAR(50) NOT NULL CHECK (status IN ('pending_payment', 'order', 'processing', 'shipping', 'done', 'cancelled')),
                        total_amount    NUMERIC(18, 2) NOT NULL CHECK (total_amount >= 0),
                        print_price     NUMERIC(18, 2) NOT NULL CHECK (print_price >= 0),
                        ship_price      NUMERIC(18, 2) NOT NULL CHECK (ship_price >= 0),
                        pre_ship_price  NUMERIC(18, 2) NOT NULL CHECK (pre_ship_price >= 0),
                        created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                        updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_wallet ON orders (wallet_id);

-- Bảng order_items (modified: removed attribute_key, attribute_value; added sale_price, updated_at)
CREATE TABLE order_items (
                             id              SERIAL PRIMARY KEY,
                             order_id        INT NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
                             product_id      INT NOT NULL REFERENCES products (id),
                             quantity        INT NOT NULL CHECK (quantity > 0),
                             original_price  NUMERIC(18, 2) NOT NULL CHECK (original_price >= 0),
                             sale_price      NUMERIC(18, 2) NOT NULL CHECK (sale_price >= 0),
                             created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                             updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_order_items_order ON order_items (order_id);
CREATE INDEX idx_order_items_product ON order_items (product_id);

-- Bảng transactions (unchanged)
CREATE TABLE transactions (
                              id               SERIAL PRIMARY KEY,
                              from_wallet_id   INT REFERENCES wallets (id),
                              to_wallet_id     INT REFERENCES wallets (id),
                              amount           NUMERIC(18, 2) NOT NULL CHECK (amount > 0),
                              transaction_type VARCHAR(50) NOT NULL CHECK (transaction_type IN ('deposit', 'order_payment', 'payment_to_print_house')),
                              admin_id         INT REFERENCES users (id) ON DELETE SET NULL,
                              order_id         INT REFERENCES orders (id) ON DELETE SET NULL,
                              created_at       TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_transactions_from_wallet ON transactions (from_wallet_id);
CREATE INDEX idx_transactions_to_wallet ON transactions (to_wallet_id);

INSERT INTO roles (role_name, description)
VALUES
    ('ADMIN', 'Người quản trị hệ thống, có toàn quyền quản lý.'),
    ('SALE', 'Nhân viên bán hàng, phụ trách xử lý đơn và chăm sóc khách hàng.'),
    ('PRINTER_HOUSE', 'Bộ phận in ấn, xử lý và sản xuất tài liệu, đơn hàng in.');


-- -- Trigger cập nhật updated_at (updated to include order_items)
-- CREATE OR REPLACE FUNCTION update_timestamp()
--     RETURNS TRIGGER AS $$
-- BEGIN
--     NEW.updated_at = NOW();
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_update_users_updated_at
--     BEFORE UPDATE ON users
--     FOR EACH ROW EXECUTE FUNCTION update_timestamp();
--
-- CREATE TRIGGER trg_update_wallets_updated_at
--     BEFORE UPDATE ON wallets
--     FOR EACH ROW EXECUTE FUNCTION update_timestamp();
--
-- CREATE TRIGGER trg_update_products_updated_at
--     BEFORE UPDATE ON products
--     FOR EACH ROW EXECUTE FUNCTION update_timestamp();
--
-- CREATE TRIGGER trg_update_orders_updated_at
--     BEFORE UPDATE ON orders
--     FOR EACH ROW EXECUTE FUNCTION update_timestamp();
--
-- CREATE TRIGGER trg_update_order_items_updated_at
--     BEFORE UPDATE ON order_items
--     FOR EACH ROW EXECUTE FUNCTION update_timestamp();
--
-- -- Trigger cập nhật số dư ví (unchanged)
-- CREATE OR REPLACE FUNCTION update_wallet_balance()
--     RETURNS TRIGGER AS $$
-- BEGIN
--     IF NEW.from_wallet_id IS NOT NULL THEN
--         IF EXISTS (SELECT 1 FROM wallets WHERE id = NEW.from_wallet_id AND balance >= NEW.amount) THEN
--             UPDATE wallets
--             SET balance = balance - NEW.amount, updated_at = NOW()
--             WHERE id = NEW.from_wallet_id;
--         ELSE
--             RAISE EXCEPTION 'Insufficient balance in wallet ID %', NEW.from_wallet_id;
--         END IF;
--     END IF;
--
--     IF NEW.to_wallet_id IS NOT NULL THEN
--         UPDATE wallets
--         SET balance = balance + NEW.amount, updated_at = NOW()
--         WHERE id = NEW.to_wallet_id;
--     END IF;
--
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_update_wallet_balance
--     AFTER INSERT ON transactions
--     FOR EACH ROW EXECUTE FUNCTION update_wallet_balance();
--
-- -- Trigger kiểm tra chuyển trạng thái đơn hàng (unchanged)
-- CREATE OR REPLACE FUNCTION check_order_status_transition()
--     RETURNS TRIGGER AS $$
-- BEGIN
--     IF TG_OP = 'UPDATE' THEN
--         IF NEW.status = 'cancelled' AND OLD.status NOT IN ('pending_payment', 'order') THEN
--             RAISE EXCEPTION 'Order can only be cancelled in pending_payment or order status';
--         END IF;
--         IF NEW.status = 'order' AND OLD.status = 'pending_payment' THEN
--             DECLARE
--                 admin_wallet_id INT;
--             BEGIN
--                 SELECT w.id INTO admin_wallet_id
--                 FROM wallets w
--                          JOIN users u ON w.user_id = u.id
--                 WHERE u.type_account = 'admin'
--                 LIMIT 1;
--
--                 IF admin_wallet_id IS NOT NULL THEN
--                     INSERT INTO transactions (from_wallet_id, to_wallet_id, amount, transaction_type, order_id, created_at)
--                     VALUES (NEW.wallet_id, admin_wallet_id, NEW.total_amount, 'order_payment', NEW.id, NOW());
--                 ELSE
--                     RAISE EXCEPTION 'No admin wallet found';
--                 END IF;
--             END;
--         END IF;
--     END IF;
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_check_order_status_transition
--     BEFORE UPDATE ON orders
--     FOR EACH ROW EXECUTE FUNCTION check_order_status_transition();
--
-- -- Trigger thanh toán cho nhà in khi đơn hàng chuyển sang shipping (updated to use print_price)
-- CREATE OR REPLACE FUNCTION create_print_house_transaction()
--     RETURNS TRIGGER AS $$
-- DECLARE
--     print_house_wallet_id INT;
--     admin_wallet_id INT;
-- BEGIN
--     IF TG_OP = 'UPDATE' AND NEW.status = 'shipping' AND OLD.status != 'shipping' THEN
--         SELECT w.id INTO print_house_wallet_id
--         FROM wallets w
--                  JOIN users u ON w.user_id = u.id
--         WHERE u.type_account = 'print_house'
--         LIMIT 1;
--
--         SELECT w.id INTO admin_wallet_id
--         FROM wallets w
--                  JOIN users u ON w.user_id = u.id
--         WHERE u.type_account = 'admin'
--         LIMIT 1;
--
--         IF print_house_wallet_id IS NOT NULL AND admin_wallet_id IS NOT NULL THEN
--             INSERT INTO transactions (from_wallet_id, to_wallet_id, amount, transaction_type, order_id, created_at)
--             VALUES (admin_wallet_id, print_house_wallet_id, NEW.print_price, 'payment_to_print_house', NEW.id, NOW());
--         END IF;
--     END IF;
--     RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_create_print_house_transaction
--     AFTER UPDATE OF status ON orders
--     FOR EACH ROW EXECUTE FUNCTION create_print_house_transaction();

-- -- Mock Data
-- -- 1. Bảng dictionaries
-- INSERT INTO dictionaries (code, name) VALUES
--                                           ('USER_RANK', 'User Rank'),
--                                           ('PRODUCT_SIZE', 'Product Size');
--
-- -- 2. Bảng dictionary_items
-- INSERT INTO dictionary_items (dictionary_id, code, name) VALUES
--                                                              ((SELECT id FROM dictionaries WHERE code = 'USER_RANK'), 'BRONZE', 'Bronze'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'USER_RANK'), 'SILVER', 'Silver'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'USER_RANK'), 'GOLD', 'Gold'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'USER_RANK'), 'PLATINUM', 'Platinum'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'USER_RANK'), 'DIAMOND', 'Diamond'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'PRODUCT_SIZE'), 'S', 'Small'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'PRODUCT_SIZE'), 'M', 'Medium'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'PRODUCT_SIZE'), 'L', 'Large'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'PRODUCT_SIZE'), 'XL', 'Extra Large'),
--                                                              ((SELECT id FROM dictionaries WHERE code = 'PRODUCT_SIZE'), 'XXL', 'Double Extra Large');
--
-- -- 3. Bảng users
-- INSERT INTO users (user_name, first_name, last_name, email, address, phone, password_hash, type_account, rank, created_at, updated_at) VALUES
--                                                                                                                                            ('admin_boss', 'Hieu', 'Nguyen', 'admin1@printshop.com', '123 Admin St, Hanoi', '0901234567', '$2a$10$hash1', 'admin', 'DIAMOND', NOW(), NOW()),
--                                                                                                                                            ('admin_pro', 'Linh', 'Pham', 'admin2@printshop.com', '456 Admin Rd, HCMC', '0902345678', '$2a$10$hash2', 'admin', 'DIAMOND', NOW(), NOW()),
--                                                                                                                                            ('sale_bronze', 'Minh', 'Tran', 'minh.sale@printshop.com', '789 Sale Ave, Da Nang', '0913456789', '$2a$10$hash3', 'sale', 'BRONZE', NOW(), NOW()),
--                                                                                                                                            ('sale_silver', 'Anh', 'Le', 'anh.sale@printshop.com', '321 Sale Lane, Hanoi', '0924567890', '$2a$10$hash4', 'sale', 'SILVER', NOW(), NOW()),
--                                                                                                                                            ('sale_gold', 'Tuan', 'Vu', 'tuan.sale@printshop.com', '654 Sale Rd, HCMC', '0935678901', '$2a$10$hash5', 'sale', 'GOLD', NOW(), NOW()),
--                                                                                                                                            ('sale_platinum', 'Mai', 'Ho', 'mai.sale@printshop.com', '987 Sale St, Hue', '0946789012', '$2a$10$hash6', 'sale', 'PLATINUM', NOW(), NOW()),
--                                                                                                                                            ('print_master', 'Lan', 'Nguyen', 'lan.print@printshop.com', '111 Print Ave, Hanoi', '0957890123', '$2a$10$hash7', 'print_house', 'BRONZE', NOW(), NOW()),
--                                                                                                                                            ('print_expert', 'Khoa', 'Dang', 'khoa.print@printshop.com', '222 Print Rd, HCMC', '0968901234', '$2a$10$hash8', 'print_house', 'BRONZE', NOW(), NOW()),
--                                                                                                                                            ('sale_diamond', 'Vy', 'Bui', 'vy.sale@printshop.com', '333 Diamond St, Hanoi', '0979012345', '$2a$10$hash9', 'sale', 'DIAMOND', NOW(), NOW()),
--                                                                                                                                            ('sale_newbie', 'Dat', 'Ly', 'dat.sale@printshop.com', '444 Newbie Lane, Da Nang', '0980123456', '$2a$10$hash10', 'sale', 'BRONZE', NOW(), NOW());
--
-- -- 4. Bảng roles
-- INSERT INTO roles (role_name, description) VALUES
--                                                ('ADMIN', 'Full system access'),
--                                                ('SALE', 'Manage orders and customers'),
--                                                ('PRINT_HOUSE', 'Handle printing tasks');
--
-- -- 5. Bảng permissions
-- INSERT INTO permissions (permission_name, description) VALUES
--                                                            ('MANAGE_USERS', 'Create, update, delete users'),
--                                                            ('VIEW_ALL_ORDERS', 'View all orders except cancelled sale orders'),
--                                                            ('MANAGE_ORDERS', 'Create, update, cancel orders'),
--                                                            ('MANAGE_PRINTING', 'Handle printing tasks'),
--                                                            ('APPROVE_DEPOSIT', 'Approve sale deposits'),
--                                                            ('VIEW_OWN_ORDERS', 'View own orders');
--
-- -- 6. Bảng role_permissions
-- INSERT INTO role_permissions (role_id, permission_id) VALUES
--                                                           ((SELECT id FROM roles WHERE role_name = 'ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'MANAGE_USERS')),
--                                                           ((SELECT id FROM roles WHERE role_name = 'ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'VIEW_ALL_ORDERS')),
--                                                           ((SELECT id FROM roles WHERE role_name = 'ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'MANAGE_ORDERS')),
--                                                           ((SELECT id FROM roles WHERE role_name = 'ADMIN'), (SELECT id FROM permissions WHERE permission_name = 'APPROVE_DEPOSIT')),
--                                                           ((SELECT id FROM roles WHERE role_name = 'SALE'), (SELECT id FROM permissions WHERE permission_name = 'VIEW_OWN_ORDERS')),
--                                                           ((SELECT id FROM roles WHERE role_name = 'SALE'), (SELECT id FROM permissions WHERE permission_name = 'MANAGE_ORDERS')),
--                                                           ((SELECT id FROM roles WHERE role_name = 'PRINT_HOUSE'), (SELECT id FROM permissions WHERE permission_name = 'MANAGE_PRINTING'));
--
-- -- 7. Bảng user_roles
-- INSERT INTO user_roles (user_id, role_id) VALUES
--                                               ((SELECT id FROM users WHERE user_name = 'admin_boss'), (SELECT id FROM roles WHERE role_name = 'ADMIN')),
--                                               ((SELECT id FROM users WHERE user_name = 'admin_pro'), (SELECT id FROM roles WHERE role_name = 'ADMIN')),
--                                               ((SELECT id FROM users WHERE user_name = 'sale_bronze'), (SELECT id FROM roles WHERE role_name = 'SALE')),
--                                               ((SELECT id FROM users WHERE user_name = 'sale_silver'), (SELECT id FROM roles WHERE role_name = 'SALE')),
--                                               ((SELECT id FROM users WHERE user_name = 'sale_gold'), (SELECT id FROM roles WHERE role_name = 'SALE')),
--                                               ((SELECT id FROM users WHERE user_name = 'sale_platinum'), (SELECT id FROM roles WHERE role_name = 'SALE')),
--                                               ((SELECT id FROM users WHERE user_name = 'print_master'), (SELECT id FROM roles WHERE role_name = 'PRINT_HOUSE')),
--                                               ((SELECT id FROM users WHERE user_name = 'print_expert'), (SELECT id FROM roles WHERE role_name = 'PRINT_HOUSE')),
--                                               ((SELECT id FROM users WHERE user_name = 'sale_diamond'), (SELECT id FROM roles WHERE role_name = 'SALE')),
--                                               ((SELECT id FROM users WHERE user_name = 'sale_newbie'), (SELECT id FROM roles WHERE role_name = 'SALE'));
--
-- -- 8. Bảng wallets
-- INSERT INTO wallets (user_id, wallet_name, balance, created_at, updated_at) VALUES
--                                                                                 ((SELECT id FROM users WHERE user_name = 'admin_boss'), 'Admin Boss Wallet', 5000000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'admin_pro'), 'Admin Pro Wallet', 3000000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'sale_bronze'), 'Bronze Sale Wallet', 100000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'sale_silver'), 'Silver Sale Wallet', 200000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'sale_gold'), 'Gold Sale Wallet', 300000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'sale_platinum'), 'Platinum Sale Wallet', 400000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'print_master'), 'Print Master Wallet', 1000000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'print_expert'), 'Print Expert Wallet', 800000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'sale_diamond'), 'Diamond Sale Wallet', 500000.00, NOW(), NOW()),
--                                                                                 ((SELECT id FROM users WHERE user_name = 'sale_newbie'), 'Newbie Sale Wallet', 50000.00, NOW(), NOW());
--
-- -- 9. Bảng products
-- INSERT INTO products (sku, name, base_price, description, created_at, updated_at) VALUES
--                                                                                       ('TSHIRT001', 'Custom T-Shirt', 80000.00, 'High-quality custom t-shirt', NOW(), NOW()),
--                                                                                       ('TSHIRT002', 'V-Neck T-Shirt', 85000.00, 'Stylish v-neck t-shirt', NOW(), NOW()),
--                                                                                       ('POSTER001', 'A3 Poster', 40000.00, 'Vivid color A3 poster', NOW(), NOW()),
--                                                                                       ('POSTER002', 'A4 Poster', 30000.00, 'Compact A4 poster', NOW(), NOW()),
--                                                                                       ('MUG001', 'Custom Mug', 60000.00, 'Personalized ceramic mug', NOW(), NOW()),
--                                                                                       ('BAG001', 'Tote Bag', 70000.00, 'Eco-friendly tote bag', NOW(), NOW());
--
-- -- 10. Bảng product_prices
-- INSERT INTO product_prices (product_id, rank, size, price) VALUES
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'BRONZE', 'S', 100000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'BRONZE', 'M', 110000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'SILVER', 'S', 95000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'GOLD', 'S', 90000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'PLATINUM', 'S', 85000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'DIAMOND', 'S', 80000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT002'), 'BRONZE', 'M', 115000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'TSHIRT002'), 'GOLD', 'M', 95000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'POSTER001'), 'BRONZE', 'L', 50000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'POSTER001'), 'DIAMOND', 'L', 45000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'POSTER002'), 'BRONZE', 'M', 40000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'MUG001'), 'BRONZE', 'M', 80000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'MUG001'), 'GOLD', 'M', 70000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'BAG001'), 'BRONZE', 'L', 90000.00),
--                                                                ((SELECT id FROM products WHERE sku = 'BAG001'), 'PLATINUM', 'L', 80000.00);
--
-- -- 11. Bảng product_attributes
-- INSERT INTO product_attributes (product_id, attribute_key, attribute_value) VALUES
--                                                                                 ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'color', 'Black'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'color', 'White'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'TSHIRT001'), 'material', 'Cotton'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'TSHIRT002'), 'color', 'Blue'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'TSHIRT002'), 'material', 'Polyester'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'POSTER001'), 'material', 'Glossy Paper'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'POSTER002'), 'material', 'Matte Paper'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'MUG001'), 'color', 'White'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'BAG001'), 'color', 'Beige'),
--                                                                                 ((SELECT id FROM products WHERE sku = 'BAG001'), 'material', 'Canvas');
--
-- -- 12. Bảng orders (updated with print_price, ship_price, pre_ship_price)
-- INSERT INTO orders (user_id, wallet_id, status, total_amount, print_price, ship_price, pre_ship_price, created_at, updated_at) VALUES
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_bronze'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_bronze')), 'pending_payment', 210000.00, 160000.00, 30000.00, 20000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_silver'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_silver')), 'order', 190000.00, 150000.00, 25000.00, 15000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_gold'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_gold')), 'processing', 270000.00, 225000.00, 30000.00, 15000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_platinum'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_platinum')), 'shipping', 170000.00, 130000.00, 25000.00, 15000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_diamond'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_diamond')), 'done', 80000.00, 60000.00, 15000.00, 5000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_newbie'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_newbie')), 'cancelled', 110000.00, 90000.00, 15000.00, 5000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_bronze'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_bronze')), 'order', 40000.00, 30000.00, 7000.00, 3000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_silver'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_silver')), 'pending_payment', 90000.00, 70000.00, 15000.00, 5000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_gold'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_gold')), 'shipping', 180000.00, 140000.00, 25000.00, 15000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_platinum'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_platinum')), 'processing', 45000.00, 35000.00, 7000.00, 3000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_diamond'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_diamond')), 'order', 95000.00, 75000.00, 15000.00, 5000.00, NOW(), NOW()),
--                                                                                                                                    ((SELECT id FROM users WHERE user_name = 'sale_newbie'), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_newbie')), 'pending_payment', 80000.00, 60000.00, 15000.00, 5000.00, NOW(), NOW());
--
-- -- 13. Bảng order_items (updated: removed attribute_key, attribute_value; added sale_price, updated_at)
-- INSERT INTO order_items (order_id, product_id, quantity, original_price, sale_price, created_at, updated_at) VALUES
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 210000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_bronze')), (SELECT id FROM products WHERE sku = 'TSHIRT001'), 2, 100000.00, 90000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 210000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_bronze')), (SELECT id FROM products WHERE sku = 'POSTER002'), 1, 40000.00, 36000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 190000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_silver')), (SELECT id FROM products WHERE sku = 'TSHIRT001'), 2, 95000.00, 85000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 270000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_gold')), (SELECT id FROM products WHERE sku = 'TSHIRT002'), 3, 95000.00, 85000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 170000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_platinum')), (SELECT id FROM products WHERE sku = 'BAG001'), 2, 85000.00, 75000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 80000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_diamond') AND status = 'done'), (SELECT id FROM products WHERE sku = 'MUG001'), 1, 80000.00, 72000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 110000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_newbie')), (SELECT id FROM products WHERE sku = 'TSHIRT001'), 1, 110000.00, 99000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 40000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_bronze')), (SELECT id FROM products WHERE sku = 'POSTER002'), 1, 40000.00, 36000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 90000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_silver')), (SELECT id FROM products WHERE sku = 'BAG001'), 1, 90000.00, 81000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 180000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_gold')), (SELECT id FROM products WHERE sku = 'TSHIRT002'), 2, 90000.00, 81000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 45000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_platinum')), (SELECT id FROM products WHERE sku = 'POSTER001'), 1, 45000.00, 40500.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 95000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_diamond')), (SELECT id FROM products WHERE sku = 'TSHIRT001'), 1, 95000.00, 85000.00, NOW(), NOW()),
--                                                                                                                  ((SELECT id FROM orders WHERE total_amount = 80000.00 AND user_id = (SELECT id FROM users WHERE user_name = 'sale_newbie') AND status = 'pending_payment'), (SELECT id FROM products WHERE sku = 'MUG001'), 1, 80000.00, 72000.00, NOW(), NOW());
--
-- -- 14. Bảng transactions
-- INSERT INTO transactions (from_wallet_id, to_wallet_id, amount, transaction_type, admin_id, order_id, created_at) VALUES
--                                                                                                                       (NULL, (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_bronze')), 200000.00, 'deposit', (SELECT id FROM users WHERE user_name = 'admin_boss'), NULL, NOW()),
--                                                                                                                       (NULL, (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_silver')), 300000.00, 'deposit', (SELECT id FROM users WHERE user_name = 'admin_pro'), NULL, NOW()),
--                                                                                                                       (NULL, (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_gold')), 400000.00, 'deposit', (SELECT id FROM users WHERE user_name = 'admin_boss'), NULL, NOW()),
--                                                                                                                       (NULL, (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_platinum')), 500000.00, 'deposit', (SELECT id FROM users WHERE user_name = 'admin_pro'), NULL, NOW()),
--                                                                                                                       ((SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_silver')), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'admin_boss')), 190000.00, 'order_payment', NULL, (SELECT id FROM orders WHERE total_amount = 190000.00), NOW()),
--                                                                                                                       ((SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_bronze')), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'admin_boss')), 40000.00, 'order_payment', NULL, (SELECT id FROM orders WHERE total_amount = 40000.00), NOW()),
--                                                                                                                       ((SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'sale_diamond')), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'admin_boss')), 95000.00, 'order_payment', NULL, (SELECT id FROM orders WHERE total_amount = 95000.00), NOW()),
--                                                                                                                       ((SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'admin_boss')), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'print_master')), 130000.00, 'payment_to_print_house', NULL, (SELECT id FROM orders WHERE total_amount = 170000.00), NOW()),
--                                                                                                                       ((SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'admin_boss')), (SELECT id FROM wallets WHERE user_id = (SELECT id FROM users WHERE user_name = 'print_expert')), 140000.00, 'payment_to_print_house', NULL, (SELECT id FROM orders WHERE total_amount = 180000.00), NOW());