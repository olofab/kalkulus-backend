-- V4__fix_offer_status_enum_to_text.sql
-- Convert offer.status from PostgreSQL enum to text to work with Hibernate EnumType.STRING
-- This migration is idempotent and safe to run multiple times

-- Convert offer.status from enum to text
DO $$
DECLARE
  col_type text;
  col_udt_name text;
BEGIN
  SELECT data_type, udt_name
  INTO col_type, col_udt_name
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='offer' AND column_name='status';

  IF col_type IS NULL THEN
    -- Column missing: create as text
    ALTER TABLE public.offer ADD COLUMN status text NOT NULL DEFAULT 'DRAFT';
    RAISE NOTICE 'Added status column as text to offer table';
  ELSIF col_type = 'USER-DEFINED' AND col_udt_name = 'offer_status' THEN
    -- Convert enum -> text
    ALTER TABLE public.offer
      ALTER COLUMN status TYPE text USING status::text;
    RAISE NOTICE 'Converted offer.status from enum to text';
  ELSIF col_type = 'text' THEN
    RAISE NOTICE 'offer.status column is already text type';
  ELSE
    RAISE NOTICE 'offer.status column has unexpected type: % (udt: %)', col_type, col_udt_name;
  END IF;
END $$;

-- Update any existing ordinal values to string values if they exist
-- This handles the case where Hibernate previously inserted ordinal values
DO $$
BEGIN
  -- Convert ordinal 0 to 'DRAFT'
  UPDATE public.offer 
  SET status = 'DRAFT' 
  WHERE status ~ '^0$';
  
  -- Convert ordinal 1 to 'PENDING'
  UPDATE public.offer 
  SET status = 'PENDING' 
  WHERE status ~ '^1$';
  
  -- Convert ordinal 2 to 'ACCEPTED'
  UPDATE public.offer 
  SET status = 'ACCEPTED' 
  WHERE status ~ '^2$';
  
  -- Convert ordinal 3 to 'REJECTED'
  UPDATE public.offer 
  SET status = 'REJECTED' 
  WHERE status ~ '^3$';
  
  -- Convert ordinal 4 to 'EXPIRED'
  UPDATE public.offer 
  SET status = 'EXPIRED' 
  WHERE status ~ '^4$';
  
  -- Convert ordinal 5 to 'COMPLETED'
  UPDATE public.offer 
  SET status = 'COMPLETED' 
  WHERE status ~ '^5$';
  
  RAISE NOTICE 'Updated any existing ordinal offer.status values to string values';
END $$;

-- Show final column information for verification
DO $$
DECLARE
  col_info RECORD;
BEGIN
  RAISE NOTICE 'Final offer table status column information:';
  SELECT column_name, data_type, is_nullable, column_default, udt_name
  INTO col_info
  FROM information_schema.columns 
  WHERE table_schema='public' AND table_name='offer' AND column_name='status';
  
  IF FOUND THEN
    RAISE NOTICE '  %: % (nullable: %, default: %, udt: %)', 
      col_info.column_name, col_info.data_type, col_info.is_nullable, 
      col_info.column_default, col_info.udt_name;
  ELSE
    RAISE NOTICE '  status column not found!';
  END IF;
END $$;
