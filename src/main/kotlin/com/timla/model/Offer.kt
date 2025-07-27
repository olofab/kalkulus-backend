package com.timla.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import com.timla.dto.ItemDTO
import com.timla.model.Item


@Entity
data class Offer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String = "",
    var status: String = "draft",
    var customer: String = "",
    var contactPerson: String = "",
    var phone: String = "",
    var email: String = "",
    var address: String = "",
    var description: String? = null,
    var validUntil: LocalDate? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var includeVat: Boolean = true,
    var companyId: Long = 0,
    var notes: String? = "",

    @OneToMany(mappedBy = "offer", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    var items: MutableList<Item> = mutableListOf()
)
