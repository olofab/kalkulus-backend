package com.kalkulus.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class Offer(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var title: String = "",

    var status: String = "draft", // eller bruk enum hvis du vil være streng

    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var customer: String = "",

    @OneToMany(mappedBy = "offer", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    var items: MutableList<Item> = mutableListOf()
) {
    val totalSum: Double
        get() = items.sumOf { it.unitPrice * it.quantity }
}
