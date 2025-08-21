package com.timla.service

import com.timla.model.Offer
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {

    @Value("\${spring.mail.from:noreply@kalkulus.no}")
    private lateinit var fromEmail: String

    @Value("\${app.company.name:Kalkulus}")
    private lateinit var companyName: String

    fun sendOfferEmail(
        offer: Offer,
        pdfContent: ByteArray,
        recipientEmail: String,
        customSubject: String? = null,
        customMessage: String? = null
    ) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        // Email details
        helper.setFrom(fromEmail, companyName)
        helper.setTo(recipientEmail)
        
        // Subject
        val subject = customSubject ?: "Tilbud #${offer.id} fra ${companyName}"
        helper.setSubject(subject)

        // Email body
        val emailBody = buildEmailBody(offer, customMessage)
        helper.setText(emailBody, true) // true = HTML content

        // Attach PDF
        val dateStr = offer.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val filename = "tilbud-${offer.id}-${dateStr}.pdf"
        helper.addAttachment(filename, ByteArrayResource(pdfContent))

        // Send email
        mailSender.send(message)
    }

    private fun buildEmailBody(offer: Offer, customMessage: String?): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .header { background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px; }
                    .content { margin: 20px 0; }
                    .offer-details { background-color: #e9ecef; padding: 15px; border-radius: 5px; margin: 15px 0; }
                    .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #dee2e6; font-size: 0.9em; color: #6c757d; }
                    .total { font-weight: bold; font-size: 1.1em; color: #28a745; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>Tilbud fra ${companyName}</h2>
                    <p>Takk for din interesse! Vi sender deg hermed tilbud #${offer.id}.</p>
                </div>

                <div class="content">
                    ${if (!customMessage.isNullOrBlank()) "<p><strong>Melding:</strong><br>${customMessage}</p>" else ""}
                    
                    <div class="offer-details">
                        <h3>Tilbudsdetaljer</h3>
                        <p><strong>Tilbud nr:</strong> ${offer.id}</p>
                        <p><strong>Kunde:</strong> ${offer.customer}</p>
                        <p><strong>Kontaktperson:</strong> ${offer.contactPerson}</p>
                        ${if (!offer.description.isNullOrBlank()) "<p><strong>Beskrivelse:</strong> ${offer.description}</p>" else ""}
                        <p><strong>Opprettet:</strong> ${offer.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}</p>
                        ${offer.validUntil?.let { "<p><strong>Gyldig til:</strong> ${it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}</p>" } ?: ""}
                        
                        <h4>Varer og tjenester:</h4>
                        <ul>
                            ${offer.items.joinToString("") { item ->
                                val lineTotal = item.quantity * item.unitPrice
                                "<li>${item.name} - ${item.quantity} stk × ${String.format("%.2f", item.unitPrice)} kr = ${String.format("%.2f", lineTotal)} kr</li>"
                            }}
                        </ul>
                        
                        ${calculateOfferTotal(offer)}
                    </div>

                    <p>Du finner det komplette tilbudet som PDF-vedlegg til denne e-posten.</p>
                    
                    <p>Har du spørsmål eller ønsker å diskutere tilbudet, er du velkommen til å ta kontakt med oss.</p>
                </div>

                <div class="footer">
                    <p>Med vennlig hilsen,<br><strong>${companyName}</strong></p>
                    <p><em>Denne e-posten er sendt automatisk fra vårt tilbudssystem.</em></p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun calculateOfferTotal(offer: Offer): String {
        val subtotal = offer.items.sumOf { it.quantity * it.unitPrice }
        
        return if (offer.includeVat) {
            val vat = subtotal * 0.25
            val totalWithVat = subtotal + vat
            """
                <p><strong>Subtotal:</strong> ${String.format("%.2f", subtotal)} kr</p>
                <p><strong>MVA (25%):</strong> ${String.format("%.2f", vat)} kr</p>
                <p class="total"><strong>Total inkl. MVA:</strong> ${String.format("%.2f", totalWithVat)} kr</p>
            """.trimIndent()
        } else {
            """<p class="total"><strong>Total eks. MVA:</strong> ${String.format("%.2f", subtotal)} kr</p>"""
        }
    }
}
