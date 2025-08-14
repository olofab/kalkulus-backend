package com.timla.model

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
data class ItemTemplate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String = "",

    var unitPrice: Double = 0.0,

    @Column(name = "company_id")
    var companyId: Long = 0,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "item_template_category",
        joinColumns = [JoinColumn(name = "item_template_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    var categories: MutableList<Category> = mutableListOf()
)