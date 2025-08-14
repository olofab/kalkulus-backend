-- First, add a temporary column for the new enum values
ALTER TABLE users ADD COLUMN user_type_temp SMALLINT;

-- Update the temp column based on existing string values
-- Adjust these mappings based on your actual enum values
UPDATE users SET user_type_temp = 
  CASE 
    WHEN user_type = 'ADMIN' THEN 0
    WHEN user_type = 'USER' THEN 1
    WHEN user_type = 'MANAGER' THEN 2
    -- Add more mappings as needed based on your UserType enum
    ELSE 0
  END;

-- Drop the old column and rename the temp column
ALTER TABLE users DROP COLUMN user_type;
ALTER TABLE users RENAME COLUMN user_type_temp TO user_type;

-- Make it NOT NULL if needed
ALTER TABLE users ALTER COLUMN user_type SET NOT NULL;
