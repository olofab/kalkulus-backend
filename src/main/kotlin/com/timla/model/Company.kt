package com.timla.model

import jakarta.persistence.*

@Entity
@Table(name = "company")
data class Company(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String = "",

    @Column(name = "org_number", unique = true, nullable = false)
    var organizationNumber: String = "",

    @Transient
    var industry: String = "",

    @Transient
    var hourlyRate: Double = 0.0,

    @Transient
    var machineRate: Double = 0.0,

    @Transient
    var fuelRate: Double = 0.0,

    @Transient
    var vat: Double? = 25.0 // standard mva
)
