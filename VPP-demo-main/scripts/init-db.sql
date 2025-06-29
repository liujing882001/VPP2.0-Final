-- VPP 2.0 虚拟电厂管理系统 - 数据库初始化脚本

-- 创建数据库扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- 授权
GRANT ALL PRIVILEGES ON DATABASE vpp_db TO vpp_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO vpp_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO vpp_user;

-- 创建基础表结构示例
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS power_devices (
    id BIGSERIAL PRIMARY KEY,
    device_code VARCHAR(50) UNIQUE NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    device_type VARCHAR(50) NOT NULL,
    capacity DECIMAL(10,2),
    location VARCHAR(200),
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_power_devices_code ON power_devices(device_code);

-- 插入初始数据
INSERT INTO roles (name, description) VALUES 
    ('ADMIN', '系统管理员'),
    ('OPERATOR', '操作员'),
    ('VIEWER', '查看者')
ON CONFLICT (name) DO NOTHING;

COMMIT; 