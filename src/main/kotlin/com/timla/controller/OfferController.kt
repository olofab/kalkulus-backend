package com.timla.controller

import com.timla.dto.*
import com.timla.model.Item
import com.timla.model.Offer
import com.timla.model.OfferStatus
import com.timla.service.OfferService
import com.timla.security.JwtUtil
import com.timla.repository.OfferRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = ["https://kalkulus-frontend.vercel.app", "https://*.vercel.app"], allowCredentials = "true")
class OfferController(
    private val offerService: OfferService,
    private val jwtUtil: JwtUtil,
    private val offerRepository: OfferRepository
) {

    private fun getCompanyIdFromRequest(request: HttpServletRequest): Long {
        val token = request.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: throw RuntimeException("Token mangler")
        return jwtUtil.getCompanyId(token)
    }

    @GetMapping
    fun getAllOffers(request: HttpServletRequest): List<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.getAllOffers(companyId)
    }

    @GetMapping("/{id}")
    fun getOfferById(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        val offer = offerService.getOfferById(id, companyId)
        return if (offer != null) ResponseEntity.ok(offer)
        else ResponseEntity.notFound().build()
    }

    @PostMapping
    fun createOffer(@RequestBody request: Offer, httpRequest: HttpServletRequest): OfferResponse {
        val companyId = getCompanyIdFromRequest(httpRequest)
        return offerService.createOffer(request, companyId)
    }

   @PutMapping("/{id}")
    fun updateOffer(
        @PathVariable id: Long,
        @RequestBody updateOfferRequest: UpdateOfferRequest,
        request: HttpServletRequest
    ): ResponseEntity<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.updateOffer(id, updateOfferRequest, companyId)
    }

    @DeleteMapping("/{id}")
    fun deleteOffer(@PathVariable id: Long, request: HttpServletRequest): ResponseEntity<Void> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.deleteOffer(id, companyId)
    }

    @GetMapping("/search")
    fun searchOffers(
        @RequestParam query: String,
        request: HttpServletRequest
    ): List<OfferResponse> {
        val companyId = getCompanyIdFromRequest(request)
        return offerService.searchOffers(query, companyId)
    }

    // Update offer status
    @PutMapping("/{offerId}/status")
    fun updateOfferStatus(
        @PathVariable offerId: Long,
        @RequestBody @Valid request: UpdateOfferStatusRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<*> {
        return try {
            val companyId = getCompanyIdFromRequest(httpRequest)
            
            // Find and verify offer belongs to company
            val offer = offerRepository.findById(offerId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse("OFFER_NOT_FOUND", "Tilbud med ID $offerId ble ikke funnet"))

            if (offer.companyId != companyId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponse("ACCESS_DENIED", "Du har ikke tilgang til dette tilbudet"))
            }

            // Update status and timestamp
            offer.status = request.status
            offer.updatedAt = LocalDateTime.now()
            val savedOffer = offerRepository.save(offer)

            // Return response
            val response = UpdateOfferStatusResponse(
                success = true,
                offer = OfferStatusDto(
                    id = savedOffer.id,
                    status = savedOffer.status,
                    updatedAt = savedOffer.updatedAt
                )
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse("SERVER_ERROR", "Kunne ikke oppdatere status: ${e.message}"))
        }
    }

    // Generate PDF for offer
    @PostMapping("/{offerId}/pdf")
    fun generateOfferPdf(
        @PathVariable offerId: Long,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<*> {
        return try {
            val companyId = getCompanyIdFromRequest(httpRequest)
            
            // Find and verify offer belongs to company
            val offer = offerRepository.findById(offerId).orElse(null)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorResponse("OFFER_NOT_FOUND", "Tilbud med ID $offerId ble ikke funnet"))

            if (offer.companyId != companyId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponse("ACCESS_DENIED", "Du har ikke tilgang til dette tilbudet"))
            }

            // Validate offer has required data
            if (offer.items.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse("INSUFFICIENT_DATA", "Tilbudet må inneholde minst én vare for å generere PDF"))
            }

            // Generate PDF (placeholder implementation)
            val pdfContent = generatePdfContent(offer)
            val dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val filename = "tilbud-${offerId}-${dateStr}.pdf"

            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_PDF
                setContentDispositionFormData("attachment", filename)
            }

            ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent)

        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse("PDF_GENERATION_ERROR", "Kunne ikke generere PDF: ${e.message}"))
        }
    }

    private fun generatePdfContent(offer: Offer): ByteArray {
        // Create a basic PDF structure
        // This is a minimal PDF - for production use a proper PDF library like iText
        val content = buildString {
            append("%PDF-1.4\n")
            append("1 0 obj\n")
            append("<<\n")
            append("/Type /Catalog\n")
            append("/Pages 2 0 R\n")
            append(">>\n")
            append("endobj\n")
            
            append("2 0 obj\n")
            append("<<\n")
            append("/Type /Pages\n")
            append("/Kids [3 0 R]\n")
            append("/Count 1\n")
            append(">>\n")
            append("endobj\n")
            
            append("3 0 obj\n")
            append("<<\n")
            append("/Type /Page\n")
            append("/Parent 2 0 R\n")
            append("/MediaBox [0 0 612 792]\n")
            append("/Contents 4 0 R\n")
            append("/Resources <<\n")
            append("/Font <<\n")
            append("/F1 5 0 R\n")
            append(">>\n")
            append(">>\n")
            append(">>\n")
            append("endobj\n")

            // Create content stream
            val textContent = buildString {
                appendLine("TILBUD #${offer.id}")
                appendLine("")
                appendLine("KUNDEINFORMASJON:")
                appendLine("Kunde: ${offer.customer}")
                appendLine("Kontaktperson: ${offer.contactPerson}")
                appendLine("E-post: ${offer.email}")
                appendLine("Telefon: ${offer.phone}")
                appendLine("Adresse: ${offer.address}")
                appendLine("")
                
                if (!offer.description.isNullOrBlank()) {
                    appendLine("BESKRIVELSE:")
                    appendLine(offer.description)
                    appendLine("")
                }
                
                appendLine("VARER OG TJENESTER:")
                var total = 0.0
                offer.items.forEach { item ->
                    val lineTotal = item.quantity * item.unitPrice
                    total += lineTotal
                    appendLine("${item.name} - ${item.quantity} stk a ${String.format("%.2f", item.unitPrice)} kr = ${String.format("%.2f", lineTotal)} kr")
                }
                
                appendLine("")
                appendLine("Subtotal: ${String.format("%.2f", total)} kr")
                if (offer.includeVat) {
                    val vat = total * 0.25
                    val totalWithVat = total + vat
                    appendLine("MVA (25%): ${String.format("%.2f", vat)} kr")
                    appendLine("Total inkl. MVA: ${String.format("%.2f", totalWithVat)} kr")
                } else {
                    appendLine("Total eks. MVA: ${String.format("%.2f", total)} kr")
                }
                
                appendLine("")
                appendLine("Status: ${offer.status}")
                appendLine("Opprettet: ${offer.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}")
                
                offer.validUntil?.let { validUntil ->
                    appendLine("Gyldig til: ${validUntil.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}")
                }
                
                if (!offer.notes.isNullOrBlank()) {
                    appendLine("")
                    appendLine("NOTATER:")
                    appendLine(offer.notes)
                }
            }

            val contentLength = textContent.length + 50 // Approximate length with PDF commands
            
            append("4 0 obj\n")
            append("<<\n")
            append("/Length ${contentLength}\n")
            append(">>\n")
            append("stream\n")
            append("BT\n")
            append("/F1 12 Tf\n")
            append("50 750 Td\n")
            
            // Add text content with line breaks
            textContent.lines().forEachIndexed { index, line ->
                val escapedLine = line.replace("(", "\\(").replace(")", "\\)")
                append("(${escapedLine}) Tj\n")
                append("0 -15 Td\n") // Move down for next line
            }
            
            append("ET\n")
            append("endstream\n")
            append("endobj\n")
            
            // Font object
            append("5 0 obj\n")
            append("<<\n")
            append("/Type /Font\n")
            append("/Subtype /Type1\n")
            append("/BaseFont /Helvetica\n")
            append(">>\n")
            append("endobj\n")
            
            // Cross-reference table
            append("xref\n")
            append("0 6\n")
            append("0000000000 65535 f \n")
            append("0000000010 65535 n \n")
            append("0000000079 65535 n \n")
            append("0000000173 65535 n \n")
            append("0000000301 65535 n \n")
            append("0000000380 65535 n \n")
            
            append("trailer\n")
            append("<<\n")
            append("/Size 6\n")
            append("/Root 1 0 R\n")
            append(">>\n")
            append("startxref\n")
            append("492\n")
            append("%%EOF\n")
        }
        
        return content.toByteArray()
    }
}
