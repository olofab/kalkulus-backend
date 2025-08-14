package com.timla.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
data class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String = "",
    var quantity: Int = 1,
    var unitPrice: Double = 0.0,

    @Column(name = "category_id")
    var categoryId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    @JsonIgnore // prevent infinite recursion
    var offer: Offer? = null
) {
    constructor() : this(0, "", 0, 0.0, null, null)
}
