CREATE TABLE users
(
    id           SERIAL PRIMARY KEY,
    username     VARCHAR(100) UNIQUE NOT NULL,
    password     TEXT                NOT NULL,
    firstname    VARCHAR(100)        NOT NULL,
    lastname     VARCHAR(100)        NOT NULL,
    phone        VARCHAR(50)         NOT NULL,
    address      VARCHAR(200)        NOT NULL,
    email        VARCHAR(150) UNIQUE NOT NULL,
    rank         VARCHAR(50)         NOT NULL,
    type_account VARCHAR(50)         NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_rank ON users(rank);
CREATE INDEX idx_users_type_account ON users(type_account);


CREATE TABLE user_roles
(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);


CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE role_permissions
(
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles (id),
    FOREIGN KEY (permission_id) REFERENCES permissions (id)
);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);


CREATE TABLE permissions
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE wallets
(
    id         SERIAL PRIMARY KEY,
    user_id    INT NOT NULL,
    balance    DECIMAL(12, 2) DEFAULT 0,
    created_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX idx_wallets_user_id ON wallets(user_id);

Create table transactions
(
    id           SERIAL PRIMARY KEY,
    from_user_id INT         NOT NULL,
    to_user_id   INT         NOT NULL,
    user_id      INT         NOT NULL,
    amount       DECIMAL(12, 2) DEFAULT 0,
    status       VARCHAR(50) NOT NULL,
    type         VARCHAR(50) NOT NULL,
    order_id     INT         NOT NULL,
    created_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (from_user_id) REFERENCES users (id),
    FOREIGN KEY (to_user_id) REFERENCES users (id)
);
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_from_user_id ON transactions(from_user_id);
CREATE INDEX idx_transactions_to_user_id ON transactions(to_user_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_order_id ON transactions(order_id);


CREATE TABLE products
(
    id             SERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    original_price DECIMAL(12, 2) DEFAULT 0,
    type           VARCHAR(50)  NOT NULL,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_products_type ON products(type);


CREATE TABLE product_prices
(
    id         SERIAL PRIMARY KEY,
    product_id INT         NOT NULL,
    rank       VARCHAR(50) NOT NULL,
    type       VARCHAR(50) NOT NULL,
    sale_price DECIMAL(12, 2) DEFAULT 0,
    created_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE INDEX idx_product_prices_product_id ON product_prices(product_id);
CREATE INDEX idx_product_prices_rank_type ON product_prices(rank, type);


CREATE TABLE orders
(
    id                 SERIAL PRIMARY KEY,
    user_id            INT         NOT NULL,
    status             VARCHAR(50) NOT NULL,
    total_price        DECIMAL(12, 2) DEFAULT 0,
    printing_price     DECIMAL(12, 2) DEFAULT 0,
    shipping_price     DECIMAL(12, 2) DEFAULT 0,
    pre_shipping_price DECIMAL(12, 2) DEFAULT 0,
    created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);


CREATE TABLE order_items
(
    id         SERIAL PRIMARY KEY,
    order_id   INT         NOT NULL,
    product_id INT         NOT NULL,
    quantity   INT         NOT NULL,
    size       VARCHAR(50) NOT NULL,
    sale_price DECIMAL(12, 2) DEFAULT 0,
    unit_price DECIMAL(12, 2) DEFAULT 0,
    created_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_items_size ON order_items(size);


CREATE table dictionaries
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(50)  NOT NULL,
    description TEXT,
    created_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

CREATE table dictionary_items
(
    id            SERIAL PRIMARY KEY,
    dictionary_id INT         NOT NULL,
    code          VARCHAR(100) NOT NULL,
    value          VARCHAR(50)  NOT NULL,
    description   TEXT,
    created_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dictionary_id) REFERENCES dictionaries (id)
);
CREATE INDEX idx_dictionary_items_dict_id ON dictionary_items(dictionary_id);
CREATE UNIQUE INDEX idx_dictionary_items_code ON dictionary_items(code);
