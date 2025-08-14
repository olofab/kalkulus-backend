package com.timla.model

import jakarta.persistence.*

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["email", "company_id"])])
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "full_name")
    var name: String = "",

    @Column(name = "username")
    var username: String = "",

    @Column(nullable = false)
    var email: String = "",

    @Column(name = "password", nullable = false)
    val passwordHash: String = "",

    @Column(name = "is_admin")
    var isAdmin: Boolean = false,

    var userType: UserType = UserType.INTERNAL,

   @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    var company: Company = Company()
)
