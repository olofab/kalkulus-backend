# Railway Production Deployment Guide

## 🚀 Initial Setup on Railway

### 1. Environment Variables
Set these environment variables in your Railway project:

```bash
# Database (Railway PostgreSQL automatically provides DATABASE_URL)
DATABASE_URL=postgresql://username:password@host:port/database

# JWT Security
JWT_SECRET=your-super-secure-jwt-secret-key-at-least-32-characters

# Frontend URL (for CORS)
FRONTEND_URL=https://your-frontend-domain.com

# Spring Profile
SPRING_PROFILES_ACTIVE=railway
```

### 2. Railway PostgreSQL Plugin
1. Add PostgreSQL plugin to your Railway project
2. Railway will automatically provide `DATABASE_URL`
3. The database will persist between deployments

### 3. Initial Deployment
```bash
# Railway will automatically detect and deploy your Spring Boot app
# The V1__initial_schema.sql migration will run automatically
```

## 🔄 Safe Database Migration Strategy

### Migration File Naming Convention
```
V1__initial_schema.sql           # Complete initial schema
V2__add_user_preferences.sql     # Example future migration
V3__add_item_categories.sql      # Another example
```

### Rules for Safe Migrations

#### ✅ SAFE Operations (won't break existing data):
- Adding new tables
- Adding new columns with DEFAULT values
- Adding indexes
- Adding constraints that don't conflict with existing data
- Creating new enums
- Adding new enum values to existing enums (at the end)

#### ⚠️ CAREFUL Operations (need data migration):
- Renaming columns (requires UPDATE statements)
- Changing column types (use USING clause)
- Adding NOT NULL constraints to existing columns

#### ❌ DANGEROUS Operations (avoid in production):
- Dropping tables
- Dropping columns  
- Removing enum values
- Changing primary keys

## 📁 Example Future Migration

Create file: `V4__add_user_preferences.sql`

```sql
-- Safe migration example
-- Add new table for user preferences
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    theme VARCHAR(50) DEFAULT 'light',
    language VARCHAR(10) DEFAULT 'nb',
    notifications_enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id)
);

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id);

-- Add new optional column to existing table (with DEFAULT)
ALTER TABLE offer ADD COLUMN IF NOT EXISTS priority VARCHAR(20) DEFAULT 'NORMAL';

-- Create new enum for priority
DO $$ BEGIN
    CREATE TYPE offer_priority AS ENUM ('LOW', 'NORMAL', 'HIGH', 'URGENT');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

-- Update new column to use enum (safe because we added DEFAULT first)
ALTER TABLE offer ALTER COLUMN priority TYPE offer_priority USING priority::offer_priority;
```

## 🛡️ Migration Best Practices

### 1. Always Test Migrations
```bash
# Test locally first
docker compose down -v
docker compose up --build

# Check migration status
docker compose exec backend bash
# Inside container: 
# ./gradlew flywayInfo  # If Gradle Flyway plugin is configured
```

### 2. Backup Before Major Changes
```sql
-- Railway provides automatic backups, but for major changes:
-- Take manual snapshot of your database in Railway dashboard
```

### 3. Zero-Downtime Migrations
```sql
-- Example: Adding a new required field safely

-- Step 1: Add column as optional with default
ALTER TABLE users ADD COLUMN phone VARCHAR(20) DEFAULT '';

-- Step 2: Deploy application code that can handle both cases
-- (Your app should work with and without the phone field)

-- Step 3: In next migration, populate data
UPDATE users SET phone = 'N/A' WHERE phone = '';

-- Step 4: In another migration, add constraint if needed
ALTER TABLE users ALTER COLUMN phone SET NOT NULL;
```

## 🔍 Monitoring Migrations

### Check Migration Status
```bash
# In Railway logs, look for:
2024-01-15 10:30:00 INFO  o.f.core.internal.command.DbMigrate - Current version of schema "public": 3
2024-01-15 10:30:00 INFO  o.f.core.internal.command.DbMigrate - Migrating schema "public" to version 4 - add user preferences
2024-01-15 10:30:00 INFO  o.f.core.internal.command.DbMigrate - Successfully applied 1 migration to schema "public"
```

### Database Schema Version Query
```sql
-- Check which migrations have been applied
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

## 🆘 Emergency Rollback

If a migration causes issues:

1. **Immediate Fix**: Revert to previous deployment in Railway
2. **Database Fix**: Railway allows you to restore from backup
3. **Code Fix**: Create new migration to fix issues (never modify existing migrations)

```sql
-- Example rollback migration: V5__rollback_user_preferences.sql
DROP TABLE IF EXISTS user_preferences;
ALTER TABLE offer DROP COLUMN IF EXISTS priority;
DROP TYPE IF EXISTS offer_priority;
```

## 🚦 Deployment Checklist

Before each deployment:
- [ ] Test migration locally with existing data
- [ ] Review all new migrations for safety
- [ ] Ensure no breaking changes to API
- [ ] Check environment variables are set
- [ ] Monitor Railway logs during deployment
- [ ] Test critical application functions after deployment

## 📞 Production Monitoring

Railway provides:
- Database metrics
- Application logs  
- Performance monitoring
- Automatic backups
- Uptime monitoring

Always check Railway dashboard after deployments!
