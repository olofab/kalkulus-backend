package com.timla.model

import jakarta.persistence.*

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String = "",

    @Column(name = "company_id")
    var companyId: Long = 0
)
