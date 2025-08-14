-- Add is_admin column to users table
-- This migration is idempotent and can be run multiple times safely

DO $$
BEGIN
  -- Add is_admin column if it doesn't exist
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public' AND table_name = 'users' AND column_name = 'is_admin'
  ) THEN
    ALTER TABLE public.users
      ADD COLUMN is_admin boolean NOT NULL DEFAULT false;
  END IF;
END $$;
