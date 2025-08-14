-- Migration to add is_admin column and fix user_type enum handling
-- This migration is idempotent and safe to run multiple times

-- Add is_admin column if it does not exist
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'users'
      AND column_name = 'is_admin'
  ) THEN
    ALTER TABLE public.users
      ADD COLUMN is_admin boolean NOT NULL DEFAULT false;
    RAISE NOTICE 'Added is_admin column to users table';
  ELSE
    RAISE NOTICE 'is_admin column already exists in users table';
  END IF;
END $$;

-- Convert user_type from enum to text to work with Hibernate EnumType.STRING
DO $$
BEGIN
  -- Check if user_type column exists and what type it is
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'users'
      AND column_name = 'user_type'
      AND udt_name = 'user_type'  -- This indicates it's our custom enum type
  ) THEN
    -- Convert enum column to text
    ALTER TABLE public.users
      ALTER COLUMN user_type TYPE text USING user_type::text;
    RAISE NOTICE 'Converted user_type column from enum to text';
  ELSIF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'users'
      AND column_name = 'user_type'
      AND data_type = 'text'
  ) THEN
    RAISE NOTICE 'user_type column is already text type';
  ELSE
    RAISE NOTICE 'user_type column not found or has unexpected type';
  END IF;
END $$;

-- Update any existing ordinal values to string values if they exist
-- This handles the case where Hibernate previously inserted ordinal values
DO $$
BEGIN
  -- Convert ordinal 0 to 'INTERNAL'
  UPDATE public.users 
  SET user_type = 'INTERNAL' 
  WHERE user_type = '0' OR user_type ~ '^[0-9]+$' AND user_type::integer = 0;
  
  -- Convert ordinal 1 to 'SUBCONTRACTOR'
  UPDATE public.users 
  SET user_type = 'SUBCONTRACTOR' 
  WHERE user_type = '1' OR user_type ~ '^[0-9]+$' AND user_type::integer = 1;
  
  -- Convert ordinal 2 to 'ADMIN'
  UPDATE public.users 
  SET user_type = 'ADMIN' 
  WHERE user_type = '2' OR user_type ~ '^[0-9]+$' AND user_type::integer = 2;
  
  RAISE NOTICE 'Updated any existing ordinal user_type values to string values';
END $$;
