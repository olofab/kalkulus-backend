package com.timla.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class Offer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false)
    var status: String = "draft", // vurder enum senere

    @Column(nullable = false)
    var customer: String = "",

    @Column(nullable = false)
    var contactPerson: String = "",

    var phone: String = "",

    var email: String = "",

    var address: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    var validUntil: LocalDate? = null,

    var createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "offer", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    var items: MutableList<Item> = mutableListOf()
) {
    val totalSum: Double
        get() = items.sumOf { it.unitPrice * it.quantity }
}
