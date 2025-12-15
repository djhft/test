-- MySQL schema for 个人财务记账系统
-- 使用方式：
-- 1) 在 MySQL 中创建数据库 (例如: CREATE DATABASE finance_db CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;)
-- 2) 切换到该数据库并执行本脚本：USE finance_db; SOURCE schema_mysql.sql;
-- 3) 根据需要调整字段、索引或字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- users table
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE,
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(32) UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  nickname VARCHAR(100),
  avatar TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- categories (user_id NULL 表示系统预置分类)
DROP TABLE IF EXISTS categories;
CREATE TABLE categories (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  name VARCHAR(100) NOT NULL,
  `type` VARCHAR(20) NOT NULL, -- 'expense' | 'income' | 'transfer'
  icon VARCHAR(100),
  color VARCHAR(20),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- transactions
DROP TABLE IF EXISTS transactions;
CREATE TABLE transactions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  category_id BIGINT NULL,
  amount DECIMAL(14,2) NOT NULL,
  currency VARCHAR(10) NOT NULL DEFAULT 'CNY',
  `type` VARCHAR(20) NOT NULL, -- 'expense' | 'income' | 'transfer'
  `date` DATE NOT NULL,
  note TEXT,
  tags JSON DEFAULT NULL, -- JSON array of tags
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
  INDEX idx_transactions_user_date (user_id, `date`),
  INDEX idx_transactions_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- budgets
DROP TABLE IF EXISTS budgets;
CREATE TABLE budgets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  category_id BIGINT NULL,
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,
  amount DECIMAL(14,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_budgets_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_budgets_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
  INDEX idx_budgets_user_period (user_id, period_start, period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- savings_accounts (目标/储蓄)
DROP TABLE IF EXISTS savings_accounts;
CREATE TABLE savings_accounts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(150) NOT NULL,
  target_amount DECIMAL(14,2),
  current_amount DECIMAL(14,2) DEFAULT 0,
  target_date DATE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_savings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_savings_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ai_requests
DROP TABLE IF EXISTS ai_requests;
CREATE TABLE ai_requests (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  input JSON,
  result JSON,
  status VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending | done | failed
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_ai_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
  INDEX idx_ai_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optional: sample system categories (uncomment to insert defaults)
-- INSERT INTO categories (user_id, name, `type`, icon, color) VALUES
--   (NULL, '餐饮', 'expense', 'utensils', '#f59e0b'),
--   (NULL, '购物', 'expense', 'shopping-bag', '#ef4444'),
--   (NULL, '工资', 'income', 'wallet', '#10b981'),
--   (NULL, '转账', 'transfer', 'exchange-alt', '#3b82f6');

SET FOREIGN_KEY_CHECKS = 1;