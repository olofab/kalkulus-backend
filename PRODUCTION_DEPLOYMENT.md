# Railway Environment Variables Setup Guide

## SMTP Configuration for Production

Gå til Railway dashboard og legg til disse environment variables:

### Påkrevde SMTP Variables:
```
SMTP_HOST=smtp.domeneshop.no
SMTP_PORT=587
SMTP_USERNAME=din-email-bruker
SMTP_PASSWORD=ditt-sikre-passord
SMTP_FROM=noreply@dittdomene.no
COMPANY_NAME=Ditt Firmanavn
```

### Steg-for-steg i Railway:

1. **Gå til Railway Dashboard**
   - Logg inn på railway.app
   - Velg ditt kalkulus-backend prosjekt

2. **Åpne Environment Variables**
   - Klikk på din service
   - Gå til "Variables" tab
   - Klikk "New Variable"

3. **Legg til hver variabel:**
   ```
   Variable Name: SMTP_HOST
   Value: smtp.domeneshop.no
   
   Variable Name: SMTP_PORT
   Value: 587
   
   Variable Name: SMTP_USERNAME
   Value: din-email-bruker
   
   Variable Name: SMTP_PASSWORD
   Value: ditt-sikre-passord
   
   Variable Name: SMTP_FROM
   Value: noreply@dittdomene.no
   
   Variable Name: COMPANY_NAME
   Value: Ditt Firmanavn
   ```

4. **Deploy**
   - Etter å ha lagt til alle variables, vil Railway automatisk redeploy
   - Eller klikk "Deploy" hvis den ikke gjør det automatisk

## Verifiser Deployment

Etter deployment, test at alt fungerer:

1. **Health Check:**
   ```bash
   curl https://kalkulus-backend-production.up.railway.app/health
   ```

2. **Test Email Endpoint:**
   ```bash
   curl -X POST https://kalkulus-backend-production.up.railway.app/api/offers/1/email \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -d '{"email": "test@example.com"}'
   ```

## Alternative SMTP Providers

Hvis du vil bruke andre email-providers:

### Gmail Business:
```
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-business-email@yourdomain.com
SMTP_PASSWORD=your-app-password
```

### SendGrid:
```
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=your-sendgrid-api-key
```

### Mailgun:
```
SMTP_HOST=smtp.mailgun.org
SMTP_PORT=587
SMTP_USERNAME=your-mailgun-username
SMTP_PASSWORD=your-mailgun-password
```

## Sikkerhet i Produksjon

- ✅ Flyway migreringer er aktivert
- ✅ CORS er konfigurert for Vercel domener
- ✅ JWT authentication er påkrevd for email endpoint
- ✅ Validering av email-adresser
- ✅ Error handling for SMTP-feil
- ✅ Ingen test-endepunkter i produksjon

## Email Endpoint Usage

Frontend kan nå bruke dette endepunktet:

**POST** `https://kalkulus-backend-production.up.railway.app/api/offers/{offerId}/email`

**Headers:**
```
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

**Body:**
```json
{
  "email": "customer@example.com",
  "subject": "Tilbud fra Timla (valgfri)",
  "message": "Takk for din interesse! (valgfri)"
}
```
