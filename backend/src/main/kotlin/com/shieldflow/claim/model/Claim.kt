package com.shieldflow.claim.model

import com.shieldflow.claim.util.JsonListConverter
import com.shieldflow.claim.util.JsonMapConverter
import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "claims")
class Claim(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "reference", unique = true, nullable = false)
    var reference: String = "",

    @Column(name = "user_id", nullable = true)
    var userId: Long? = null,

    @Column(name = "policy_number", nullable = false)
    var policyNumber: String = "",

    @Column(name = "claim_type", nullable = false)
    var claimType: String = "",

    @Column(name = "claimant_name", nullable = false)
    var claimantName: String = "",

    @Column(name = "claimant_email", nullable = false)
    var claimantEmail: String = "",

    @Column(name = "claimant_phone", nullable = false)
    var claimantPhone: String = "",

    @Column(name = "status", nullable = false)
    var status: String = "pending_review",

    @Column(name = "submitted_at", nullable = false)
    var submittedAt: Instant = Instant.now(),

    @Convert(converter = JsonMapConverter::class)
    @Column(name = "details", columnDefinition = "text")
    var details: Map<String, Any> = emptyMap(),

    @Convert(converter = JsonListConverter::class)
    @Column(name = "supporting_documents", columnDefinition = "text")
    var supportingDocuments: List<Map<String, Any>> = emptyList(),

    @Convert(converter = JsonMapConverter::class)
    @Column(name = "estimates", columnDefinition = "text")
    var estimates: Map<String, Any> = emptyMap()
)
