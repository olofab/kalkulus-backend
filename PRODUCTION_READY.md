# 🚀 Railway Production Deployment - Complete Setup

## 📋 Pre-Deployment Checklist

### 1. Files Created/Modified for Production:
- ✅ `V1__initial_schema.sql` - Complete database schema with safe migrations
- ✅ `application-railway.yml` - Production configuration  
- ✅ `Dockerfile` - Optimized for Railway
- ✅ `HealthController.kt` - Health check endpoint
- ✅ `RAILWAY_DEPLOYMENT.md` - Complete deployment guide

### 2. Railway Project Setup:

#### Environment Variables to Set:
```bash
# Required
JWT_SECRET=your-super-secure-32-character-secret-key-here
FRONTEND_URL=https://your-frontend-domain.com
SPRING_PROFILES_ACTIVE=railway

# Optional (Railway handles these automatically with PostgreSQL plugin)
DATABASE_URL=postgresql://user:pass@host:port/db  # Auto-provided by Railway
PORT=8080                                          # Auto-provided by Railway
```

#### Add PostgreSQL Database:
1. In Railway dashboard: Add PostgreSQL plugin
2. Railway automatically provides `DATABASE_URL`
3. Database persists between deployments

## 🛠️ Deployment Steps

### Step 1: Connect GitHub Repository
1. Connect your GitHub repo to Railway
2. Railway will auto-deploy on pushes to main branch

### Step 2: Database Migration  
- On first deployment, `V1__initial_schema.sql` runs automatically
- Creates all tables, indexes, and enum types
- Handles existing data if database already exists

### Step 3: Verify Deployment
```bash
# Check health endpoint
curl https://your-app.railway.app/health

# Should return:
{
  "status": "UP",
  "timestamp": "2025-08-14T12:00:00",
  "service": "kalkulus-backend",
  "version": "0.0.1-SNAPSHOT"
}
```

## 🔄 Future Updates (Safe Database Changes)

### Adding New Features (Example):
Create new migration file: `V4__add_notifications.sql`

```sql
-- Always safe: Adding new table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Always safe: Adding new optional column with default
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Always safe: Adding indexes
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
```

### Safe Update Process:
1. Create new migration file with incremental version number
2. Test locally first: `docker compose down -v && docker compose up --build`
3. Push to GitHub - Railway auto-deploys
4. Monitor Railway logs for migration success
5. Test application functionality

## 🚨 Emergency Procedures

### If Migration Fails:
1. **Check Railway logs** for specific error
2. **Rollback deployment** in Railway dashboard
3. **Fix migration** and redeploy
4. **Never modify existing migration files** - create new ones

### If Application Won't Start:
1. Check environment variables are set correctly
2. Verify DATABASE_URL is provided by PostgreSQL plugin
3. Check Railway build logs for compilation errors

## 📊 Production Monitoring

### Railway Provides:
- **Database Metrics**: CPU, Memory, Connections
- **Application Logs**: Real-time log streaming  
- **Uptime Monitoring**: Automatic health checks
- **Database Backups**: Automatic daily backups
- **SSL Certificates**: Automatic HTTPS

### Custom Monitoring:
- Health endpoint: `GET /health`
- Application logs show migration status
- Database queries logged at INFO level

## 🔐 Security Considerations

### Railway Security Features:
- Private networking between services
- Environment variables encrypted at rest
- HTTPS/TLS termination
- Database connection encryption

### Application Security:
- JWT tokens for authentication
- CORS configured for your frontend domain
- SQL injection prevention via JPA
- Error details hidden in production

## 💡 Performance Optimization

### Database:
- Connection pooling configured (max 10 connections)
- Indexes on all foreign keys and search fields
- Query optimization via JPA

### Application:
- Production logging levels
- JPA `open-in-view` disabled
- Efficient Docker image caching

## 📈 Scaling Considerations

### When You Need to Scale:
- **Database**: Railway PostgreSQL can scale vertically
- **Application**: Railway supports horizontal scaling
- **Monitoring**: Use Railway metrics to identify bottlenecks

Your application is now production-ready! 🎉

### Quick Start Commands:
```bash
# 1. Set environment variables in Railway dashboard
# 2. Add PostgreSQL plugin  
# 3. Deploy from GitHub
# 4. Visit: https://your-app.railway.app/health
```
