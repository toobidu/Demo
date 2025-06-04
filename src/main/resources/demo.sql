-- 1. Dictionaries & Dictionary Items
CREATE TABLE dictionaries (
                              id SERIAL PRIMARY KEY,
                              code VARCHAR(100) NOT NULL UNIQUE,
                              name VARCHAR(50) NOT NULL,
                              description TEXT,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dictionary_items (
                                  id SERIAL PRIMARY KEY,
                                  dictionary_id INT NOT NULL,
                                  code VARCHAR(100) NOT NULL,
                                  value VARCHAR(50) NOT NULL,
                                  description TEXT,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (dictionary_id) REFERENCES dictionaries (id) ON DELETE CASCADE,
                                  CONSTRAINT unique_code_per_dictionary UNIQUE (dictionary_id, code)
);
CREATE INDEX idx_dictionary_items_dict_id ON dictionary_items(dictionary_id);
CREATE INDEX idx_dictionary_items_code ON dictionary_items(code);
CREATE INDEX idx_dictionary_items_value ON dictionary_items(value);

-- 2. Users
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       password TEXT NOT NULL,
                       firstname VARCHAR(100) NOT NULL,
                       lastname VARCHAR(100) NOT NULL,
                       phone VARCHAR(50) NOT NULL CHECK (phone ~ '^[0-9]{10,15}$'),
    address VARCHAR(200) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    rank_code VARCHAR(100),
    type_account_code VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_rank_code ON users(rank_code);
CREATE INDEX idx_users_type_account_code ON users(type_account_code);

-- 3. Roles & User Roles
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- 4. Permissions & Role Permissions
CREATE TABLE permissions (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(50) NOT NULL UNIQUE,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_permissions (
                                  role_id INT NOT NULL,
                                  permission_id INT NOT NULL,
                                  PRIMARY KEY (role_id, permission_id),
                                  FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
                                  FOREIGN KEY (permission_id) REFERENCES permissions (id) ON DELETE CASCADE
);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- 5. Wallets
CREATE TABLE wallets (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         balance DECIMAL(12, 2) DEFAULT 0 CHECK (balance >= 0),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX idx_wallets_user_id ON wallets(user_id);

-- 6. Products
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          original_price DECIMAL(12, 2) DEFAULT 0 CHECK (original_price >= 0),
                          type_code VARCHAR(100) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_products_type_code ON products(type_code);

-- 7. Product Prices
CREATE TABLE product_prices (
                                id SERIAL PRIMARY KEY,
                                product_id INT NOT NULL,
                                type_code VARCHAR(100) NOT NULL,
                                sale_price DECIMAL(12, 2) DEFAULT 0 CHECK (sale_price >= 0),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_product_prices_product_id ON product_prices(product_id);
CREATE INDEX idx_product_prices_type_code ON product_prices(type_code);

-- 8. Attributes
CREATE TABLE attributes (
                            id SERIAL PRIMARY KEY,
                            code VARCHAR(100) NOT NULL UNIQUE,
                            name VARCHAR(100) NOT NULL,
                            description TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_attributes_code ON attributes(code);

-- 9. Orders
CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL,
                        status_code VARCHAR(100) NOT NULL,
                        total_price DECIMAL(12, 2) DEFAULT 0 CHECK (total_price >= 0),
                        printing_price DECIMAL(12, 2) DEFAULT 0 CHECK (printing_price >= 0),
                        shipping_price DECIMAL(12, 2) DEFAULT 0 CHECK (shipping_price >= 0),
                        pre_shipping_price DECIMAL(12, 2) DEFAULT 0 CHECK (pre_shipping_price >= 0),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status_code ON orders(status_code);

-- 10. Order Items
CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id INT NOT NULL,
                             product_id INT NOT NULL,
                             quantity INT NOT NULL CHECK (quantity > 0),
                             size_code VARCHAR(100) NOT NULL,
                             sale_price DECIMAL(12, 2) DEFAULT 0 CHECK (sale_price >= 0),
                             unit_price DECIMAL(12, 2) DEFAULT 0 CHECK (unit_price >= 0),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_items_size_code ON order_items(size_code);

-- 11. Transactions
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              from_user_id INT NOT NULL,
                              to_user_id INT NOT NULL,
                              user_id INT NOT NULL,
                              amount DECIMAL(12, 2) DEFAULT 0 CHECK (amount >= 0),
                              status_code VARCHAR(100) NOT NULL,
                              type_code VARCHAR(100) NOT NULL,
                              order_id INT NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL,
                              FOREIGN KEY (from_user_id) REFERENCES users (id) ON DELETE SET NULL,
                              FOREIGN KEY (to_user_id) REFERENCES users (id) ON DELETE SET NULL,
                              FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE SET NULL
);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_from_user_id ON transactions(from_user_id);
CREATE INDEX idx_transactions_to_user_id ON transactions(to_user_id);
CREATE INDEX idx_transactions_status_code ON transactions(status_code);
CREATE INDEX idx_transactions_type_code ON transactions(type_code);
CREATE INDEX idx_transactions_order_id ON transactions(order_id);

-- 12. Triggers

-- Users
CREATE OR REPLACE FUNCTION check_users_rank_code() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.rank_code IS NOT NULL AND NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.rank_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'user_rank')
    ) THEN
        RAISE EXCEPTION 'Invalid rank_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_users_rank_code
    BEFORE INSERT OR UPDATE ON users
                         FOR EACH ROW EXECUTE FUNCTION check_users_rank_code();

CREATE OR REPLACE FUNCTION check_users_type_account_code() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.type_account_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'account_type')
    ) THEN
        RAISE EXCEPTION 'Invalid type_account_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_users_type_account_code
    BEFORE INSERT OR UPDATE ON users
                         FOR EACH ROW EXECUTE FUNCTION check_users_type_account_code();

-- Transactions
CREATE OR REPLACE FUNCTION check_transactions_status_code() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.status_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'transaction_status')
    ) THEN
        RAISE EXCEPTION 'Invalid status_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_transactions_status_code
    BEFORE INSERT OR UPDATE ON transactions
                         FOR EACH ROW EXECUTE FUNCTION check_transactions_status_code();

CREATE OR REPLACE FUNCTION check_transactions_type_code() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.type_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'transaction_type')
    ) THEN
        RAISE EXCEPTION 'Invalid type_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_transactions_type_code
    BEFORE INSERT OR UPDATE ON transactions
                         FOR EACH ROW EXECUTE FUNCTION check_transactions_type_code();

-- Products
CREATE OR REPLACE FUNCTION check_products_type_code() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.type_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'product_type')
    ) THEN
        RAISE EXCEPTION 'Invalid type_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_products_type_code
    BEFORE INSERT OR UPDATE ON products
                         FOR EACH ROW EXECUTE FUNCTION check_products_type_code();

-- Product Prices
CREATE OR REPLACE FUNCTION check_product_prices_type_code() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.type_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'product_type')
    ) THEN
        RAISE EXCEPTION 'Invalid type_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_product_prices_type_code
    BEFORE INSERT OR UPDATE ON product_prices
                         FOR EACH ROW EXECUTE FUNCTION check_product_prices_type_code();

-- Order Items
CREATE OR REPLACE FUNCTION check_order_items_size_code() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.size_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'size')
    ) THEN
        RAISE EXCEPTION 'Invalid size_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_order_items_size_code
    BEFORE INSERT OR UPDATE ON order_items
                         FOR EACH ROW EXECUTE FUNCTION check_order_items_size_code();

-- Orders
CREATE OR REPLACE FUNCTION check_orders_status_code() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM dictionary_items WHERE code = NEW.status_code AND dictionary_id = (SELECT id FROM dictionaries WHERE code = 'order_status')
    ) THEN
        RAISE EXCEPTION 'Invalid status_code';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_check_orders_status_code
    BEFORE INSERT OR UPDATE ON orders
                         FOR EACH ROW EXECUTE FUNCTION check_orders_status_code();

CREATE TABLE refresh_tokens (
                                id SERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                token TEXT NOT NULL,
                                expiry_date TIMESTAMP NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                revoked BOOLEAN DEFAULT FALSE,
                                CONSTRAINT fk_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users(id)
                                        ON DELETE CASCADE
);
CREATE TABLE product_attributes (
                                    id SERIAL PRIMARY KEY,
                                    product_id INT NOT NULL,
                                    attribute_id INT NOT NULL,
                                    value TEXT NOT NULL,

                                    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                                    FOREIGN KEY (attribute_id) REFERENCES attributes(id) ON DELETE CASCADE
);
CREATE INDEX idx_product_attributes_product_id ON product_attributes(product_id);
CREATE INDEX idx_product_attributes_attribute_id ON product_attributes(attribute_id);
