-- Initial database schema for production deployment
-- This is a complete schema that should work whether the database is empty or has existing data

-- Create user_type enum if it doesn't exist
DO $$ BEGIN
    CREATE TYPE user_type AS ENUM ('INTERNAL', 'SUBCONTRACTOR', 'ADMIN');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create offer_status enum if it doesn't exist  
DO $$ BEGIN
    CREATE TYPE offer_status AS ENUM ('DRAFT', 'PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED', 'COMPLETED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Create company table
CREATE TABLE IF NOT EXISTS company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    org_number VARCHAR(255),
    address VARCHAR(255),
    postal_code VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    website VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create users table (note: 'user' is a reserved word in PostgreSQL, so we use 'users')
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255),
    user_type user_type NOT NULL DEFAULT 'INTERNAL',
    company_id BIGINT REFERENCES company(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create category table
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    company_id BIGINT NOT NULL REFERENCES company(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create offer table
CREATE TABLE IF NOT EXISTS offer (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    status offer_status NOT NULL DEFAULT 'DRAFT',
    customer VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    address TEXT,
    description TEXT,
    valid_until DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    include_vat BOOLEAN DEFAULT true,
    company_id BIGINT NOT NULL REFERENCES company(id),
    notes TEXT
);

-- Create item_template table
CREATE TABLE IF NOT EXISTS item_template (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    unit_price DECIMAL(10,2) NOT NULL,
    company_id BIGINT NOT NULL REFERENCES company(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create item table
CREATE TABLE IF NOT EXISTS item (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    category_id BIGINT REFERENCES category(id),
    offer_id BIGINT NOT NULL REFERENCES offer(id) ON DELETE CASCADE,
    item_template_id BIGINT REFERENCES item_template(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create item_template_category junction table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS item_template_category (
    item_template_id BIGINT NOT NULL REFERENCES item_template(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES category(id) ON DELETE CASCADE,
    PRIMARY KEY (item_template_id, category_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_company_id ON users(company_id);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_category_company_id ON category(company_id);
CREATE INDEX IF NOT EXISTS idx_offer_company_id ON offer(company_id);
CREATE INDEX IF NOT EXISTS idx_offer_status ON offer(status);
CREATE INDEX IF NOT EXISTS idx_item_template_company_id ON item_template(company_id);
CREATE INDEX IF NOT EXISTS idx_item_offer_id ON item(offer_id);
CREATE INDEX IF NOT EXISTS idx_item_category_id ON item(category_id);
CREATE INDEX IF NOT EXISTS idx_item_template_id ON item(item_template_id);

-- Update existing data if columns exist but need migration
-- This handles cases where the database already exists with old schema

-- Migrate user_type if the column exists as varchar
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'user_type' AND data_type = 'character varying'
    ) THEN
        -- Update existing user_type values to match enum
        UPDATE users SET user_type = 
            CASE 
                WHEN UPPER(user_type::text) = 'ADMIN' THEN 'ADMIN'
                WHEN UPPER(user_type::text) = 'USER' THEN 'INTERNAL'
                WHEN UPPER(user_type::text) = 'SUBCONTRACTOR' THEN 'SUBCONTRACTOR'
                ELSE 'INTERNAL'
            END;
        
        -- Change column type to enum
        ALTER TABLE users ALTER COLUMN user_type TYPE user_type USING user_type::user_type;
    END IF;
END $$;

-- Migrate offer status if the column exists as varchar
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'offer' AND column_name = 'status' AND data_type = 'character varying'
    ) THEN
        -- Update existing status values to match enum
        UPDATE offer SET status = 
            CASE 
                WHEN LOWER(status) = 'draft' THEN 'DRAFT'
                WHEN LOWER(status) = 'pending' THEN 'PENDING'
                WHEN LOWER(status) = 'accepted' THEN 'ACCEPTED'
                WHEN LOWER(status) = 'rejected' THEN 'REJECTED'
                WHEN LOWER(status) = 'expired' THEN 'EXPIRED'
                WHEN LOWER(status) = 'completed' THEN 'COMPLETED'
                ELSE 'DRAFT'
            END;
        
        -- Change column type to enum
        ALTER TABLE offer ALTER COLUMN status TYPE offer_status USING status::offer_status;
    END IF;
END $$;
