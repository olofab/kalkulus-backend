-- V3__ensure_all_user_columns_exist.sql
-- Comprehensive migration to ensure all required user columns exist and have correct types
-- This migration is idempotent and safe to run multiple times

-- 1) Ensure is_admin column exists
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='users' AND column_name='is_admin'
  ) THEN
    ALTER TABLE public.users ADD COLUMN is_admin boolean NOT NULL DEFAULT false;
    RAISE NOTICE 'Added is_admin column to users table';
  ELSE
    RAISE NOTICE 'is_admin column already exists';
  END IF;
END $$;

-- 2) Ensure full_name column exists (should exist from V1, but let's be safe)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='users' AND column_name='full_name'
  ) THEN
    ALTER TABLE public.users ADD COLUMN full_name text NOT NULL DEFAULT '';
    RAISE NOTICE 'Added full_name column to users table';
  ELSE
    RAISE NOTICE 'full_name column already exists';
  END IF;
END $$;

-- 3) Ensure username column exists (should exist from V1, but let's be safe)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='users' AND column_name='username'
  ) THEN
    ALTER TABLE public.users ADD COLUMN username text;
    RAISE NOTICE 'Added username column to users table';
  ELSE
    RAISE NOTICE 'username column already exists';
  END IF;
END $$;

-- 4) Ensure password column exists (should exist from V1, but let's be safe)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='users' AND column_name='password'
  ) THEN
    ALTER TABLE public.users ADD COLUMN password text NOT NULL DEFAULT '';
    RAISE NOTICE 'Added password column to users table';
  ELSE
    RAISE NOTICE 'password column already exists';
  END IF;
END $$;

-- 5) Convert user_type from enum to text if needed
DO $$
DECLARE
  col_type text;
  col_udt_name text;
BEGIN
  SELECT data_type, udt_name
  INTO col_type, col_udt_name
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='users' AND column_name='user_type';

  IF col_type IS NULL THEN
    -- Column missing: create as text
    ALTER TABLE public.users ADD COLUMN user_type text NOT NULL DEFAULT 'INTERNAL';
    RAISE NOTICE 'Added user_type column as text';
  ELSIF col_type = 'USER-DEFINED' AND col_udt_name = 'user_type' THEN
    -- Convert enum -> text
    ALTER TABLE public.users
      ALTER COLUMN user_type TYPE text USING user_type::text;
    RAISE NOTICE 'Converted user_type from enum to text';
  ELSIF col_type = 'text' THEN
    RAISE NOTICE 'user_type column is already text type';
  ELSE
    RAISE NOTICE 'user_type column has unexpected type: % (udt: %)', col_type, col_udt_name;
  END IF;
END $$;

-- 6) Update any existing ordinal values to string values
DO $$
BEGIN
  -- Convert ordinal 0 to 'INTERNAL'
  UPDATE public.users 
  SET user_type = 'INTERNAL' 
  WHERE user_type ~ '^0$';
  
  -- Convert ordinal 1 to 'SUBCONTRACTOR'
  UPDATE public.users 
  SET user_type = 'SUBCONTRACTOR' 
  WHERE user_type ~ '^1$';
  
  -- Convert ordinal 2 to 'ADMIN'
  UPDATE public.users 
  SET user_type = 'ADMIN' 
  WHERE user_type ~ '^2$';
  
  RAISE NOTICE 'Updated any existing ordinal user_type values to string values';
END $$;

-- 7) Show final column information for verification
DO $$
DECLARE
  col_info RECORD;
BEGIN
  RAISE NOTICE 'Final users table column information:';
  FOR col_info IN 
    SELECT column_name, data_type, is_nullable, column_default, udt_name
    FROM information_schema.columns 
    WHERE table_schema='public' AND table_name='users' 
    ORDER BY ordinal_position
  LOOP
    RAISE NOTICE '  %: % (nullable: %, default: %, udt: %)', 
      col_info.column_name, col_info.data_type, col_info.is_nullable, 
      col_info.column_default, col_info.udt_name;
  END LOOP;
END $$;
