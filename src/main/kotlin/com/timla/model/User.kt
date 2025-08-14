package com.timla.model

import jakarta.persistence.*

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["email", "company_id"])])
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var name: String = "",

    @Column(nullable = false)
    var email: String = "",

    @Column(nullable = false)
    val passwordHash: String = "",

    var isAdmin: Boolean = false,

    var userType: UserType = UserType.INTERNAL,

   @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    var company: Company = Company()
)
