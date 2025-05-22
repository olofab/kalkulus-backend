package com.timla.model

import jakarta.persistence.*

@Entity
data class ItemTemplate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var unitPrice: Double = 0.0
)
