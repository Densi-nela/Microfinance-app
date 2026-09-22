package com.shieldflow.claim.model

import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "email", unique = true, nullable = false)
    var email: String = "",

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String = "",

    @Column(name = "full_name", nullable = false)
    var fullName: String = "",

    @Column(name = "role", nullable = false)
    var role: String = "CUSTOMER", // 'CUSTOMER' or 'ADMIN'

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()
)
