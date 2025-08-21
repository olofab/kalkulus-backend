# Email Configuration Guide

## Overview
The email service allows you to send offer PDFs via email to customers. The system generates a professional HTML email with the offer details and attaches a PDF copy.

## Email Endpoint

### POST /api/offers/{offerId}/email

Sends an offer PDF via email to a specified recipient.

**Path Parameters:**
- `offerId` (Long) - The ID of the offer to send

**Request Body:**
```json
{
  "email": "customer@example.com",
  "subject": "Custom subject line (optional)",
  "message": "Custom message to include in email (optional)"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tilbudet ble sendt til customer@example.com",
  "emailSentTo": "customer@example.com"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Kunne ikke sende e-post: Error details"
}
```

## Environment Variables

### For Development (application.yml dev profile):
```yaml
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
SMTP_FROM=noreply@kalkulus.no
COMPANY_NAME=Kalkulus
```

### For Production/Railway:
Set these environment variables in your Railway deployment:

```bash
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-production-email@yourdomain.com
SMTP_PASSWORD=your-secure-app-password
SMTP_FROM=noreply@yourdomain.com
COMPANY_NAME=Your Company Name
```

## Gmail Setup Example

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password:**
   - Go to Google Account Settings
   - Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"
   - Use this password as `SMTP_PASSWORD`

3. **Configure environment variables:**
   ```bash
   SMTP_HOST=smtp.gmail.com
   SMTP_PORT=587
   SMTP_USERNAME=youremail@gmail.com
   SMTP_PASSWORD=generated-app-password
   SMTP_FROM=youremail@gmail.com
   ```

## Other Email Providers

### SendGrid:
```bash
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=your-sendgrid-api-key
```

### Mailgun:
```bash
SMTP_HOST=smtp.mailgun.org
SMTP_PORT=587
SMTP_USERNAME=your-mailgun-username
SMTP_PASSWORD=your-mailgun-password
```

### Office 365:
```bash
SMTP_HOST=smtp.office365.com
SMTP_PORT=587
SMTP_USERNAME=your-office365-email
SMTP_PASSWORD=your-office365-password
```

## Testing the Email Functionality

### 1. Using cURL:
```bash
curl -X POST http://localhost:8081/api/offers/1/email \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "email": "test@example.com",
    "subject": "Your Custom Subject",
    "message": "Thank you for your interest in our services!"
  }'
```

### 2. Expected Email Content:
- Professional HTML email template
- Offer details including items, pricing, and totals
- PDF attachment with complete offer details
- Custom message if provided
- Company branding

## Email Template Features

The generated email includes:
- **Header** with company name and offer number
- **Custom message** (if provided)
- **Offer details** including:
  - Customer information
  - Items and pricing
  - Total calculations (with/without VAT)
  - Dates and validity
- **PDF attachment** with complete offer
- **Professional footer** with company signature

## Error Handling

The endpoint handles various error scenarios:
- **401 Unauthorized**: Missing or invalid JWT token
- **403 Forbidden**: User doesn't own the offer
- **404 Not Found**: Offer doesn't exist
- **400 Bad Request**: Offer has no items or invalid email format
- **500 Internal Server Error**: SMTP configuration issues or email sending failures

## Security

- Requires valid JWT authentication
- Users can only send emails for offers they own (same company)
- Email addresses are validated
- No sensitive information is logged

## Production Considerations

1. **Use a dedicated email service** (SendGrid, Mailgun, etc.) instead of personal Gmail
2. **Set up SPF/DKIM records** for your domain to improve deliverability
3. **Monitor email sending** for bounce rates and delivery issues
4. **Set rate limits** to prevent abuse
5. **Use environment variables** for all sensitive configuration
