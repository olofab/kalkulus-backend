-- Convert offer status to enum
-- First, create the enum type
CREATE TYPE offer_status AS ENUM ('DRAFT', 'PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED', 'COMPLETED');

-- Update existing records to use proper enum values (case-insensitive mapping)
UPDATE offer SET status = 
    CASE 
        WHEN LOWER(status) = 'draft' THEN 'DRAFT'
        WHEN LOWER(status) = 'pending' THEN 'PENDING'
        WHEN LOWER(status) = 'accepted' THEN 'ACCEPTED'
        WHEN LOWER(status) = 'rejected' THEN 'REJECTED'
        WHEN LOWER(status) = 'expired' THEN 'EXPIRED'
        WHEN LOWER(status) = 'completed' THEN 'COMPLETED'
        ELSE 'DRAFT'  -- Default fallback
    END;

-- Change the column type to the enum
ALTER TABLE offer ALTER COLUMN status TYPE offer_status USING status::offer_status;
