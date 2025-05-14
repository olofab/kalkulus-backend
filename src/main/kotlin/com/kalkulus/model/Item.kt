package com.kalkulus.model

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
data class Item(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String = "",
    var unitPrice: Double = 0.0,
    var quantity: Int = 1,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    @JsonBackReference
    var offer: Offer? = null
)
