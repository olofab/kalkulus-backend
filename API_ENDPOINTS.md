# New Offer API Endpoints Documentation

## 1. Status Update Endpoint

### Endpoint
```
PUT /api/offers/{offerId}/status
```

### Description
Updates the status of an existing offer.

### Headers
- `Authorization: Bearer {jwt-token}`
- `Content-Type: application/json`

### Request Body
```json
{
  "status": "ACCEPTED"
}
```

### Valid Status Values
- `DRAFT` - "Utkast"
- `PENDING` - "Venter på svar" 
- `ACCEPTED` - "Akseptert"
- `REJECTED` - "Avvist"
- `EXPIRED` - "Utløpt"
- `COMPLETED` - "Fullført"

### Success Response (200 OK)
```json
{
  "success": true,
  "offer": {
    "id": 123,
    "status": "ACCEPTED",
    "updatedAt": "2025-08-13T10:30:00"
  }
}
```

### Error Responses

#### Offer Not Found (404)
```json
{
  "error": "OFFER_NOT_FOUND",
  "message": "Tilbud med ID 123 ble ikke funnet"
}
```

#### Invalid Status (400)
```json
{
  "error": "INVALID_STATUS", 
  "message": "Status 'INVALID' er ikke gyldig. Gyldige verdier: [DRAFT, PENDING, ACCEPTED, REJECTED, EXPIRED, COMPLETED]"
}
```

#### Access Denied (403)
```json
{
  "error": "ACCESS_DENIED",
  "message": "Du har ikke tilgang til dette tilbudet"
}
```

---

## 2. PDF Generation Endpoint

### Endpoint
```
POST /api/offers/{offerId}/pdf
```

### Description
Generates and downloads a PDF version of the offer.

### Headers
- `Authorization: Bearer {jwt-token}`
- `Content-Type: application/json`

### Request Body
```json
{}
```
*Empty body for now - can be extended with format options later*

### Success Response (200 OK)
- **Content-Type**: `application/pdf`
- **Content-Disposition**: `attachment; filename="tilbud-{offerId}-{date}.pdf"`
- **Body**: Binary PDF data

### Error Responses

#### Offer Not Found (404)
```json
{
  "error": "OFFER_NOT_FOUND",
  "message": "Tilbud med ID 123 ble ikke funnet"
}
```

#### Insufficient Data (400)
```json
{
  "error": "INSUFFICIENT_DATA",
  "message": "Tilbudet må inneholde minst én vare for å generere PDF"
}
```

#### PDF Generation Error (500)
```json
{
  "error": "PDF_GENERATION_ERROR",
  "message": "Kunne ikke generere PDF: [error details]"
}
```

---

## Frontend Integration Examples

### Status Update
```typescript
const updateOfferStatus = async (offerId: number, status: string) => {
  const response = await fetch(`/api/offers/${offerId}/status`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ status })
  });
  
  return await response.json();
};
```

### PDF Download
```typescript
const downloadOfferPdf = async (offerId: number) => {
  const response = await fetch(`/api/offers/${offerId}/pdf`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: '{}'
  });
  
  if (response.ok) {
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `tilbud-${offerId}.pdf`;
    a.click();
  }
};
```

## Notes

- **PDF Generation**: Currently returns a text-based placeholder. For production, integrate a proper PDF library like iText or PDFBox.
- **Security**: Both endpoints validate JWT tokens and check company ownership.
- **Status Colors**: Frontend can use the provided color mapping for status display.
- **Migration**: The database migration `V3__convert_offer_status_to_enum.sql` has been added to convert existing offers to use the new enum.
