package com.foodblr.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        jakarta.persistence.UniqueConstraint(name = "uk_users_email", columnNames = ["email"])  
    ]
)
class User(
    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = true)
    var name: String? = null,

    @Column(nullable = false)
    var authProvider: String,

    @field:CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @field:UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: Instant? = null
)
