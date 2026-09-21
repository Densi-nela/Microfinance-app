package com.shieldflow.claim.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import java.time.Instant

data class ClaimRequest(
    @get:NotBlank(message = "Policy number is required")
    @get:Pattern(regexp = "^POL-\\d{6}$", message = "Policy number must match format POL-XXXXXX")
    val policyNumber: String,

    @get:NotBlank(message = "Claim type is required")
    val claimType: String,

    @get:NotBlank(message = "Claimant name is required")
    val claimantName: String,

    @get:NotBlank(message = "Claimant email is required")
    @get:Email(message = "Please provide a valid email address")
    val claimantEmail: String,

    @get:NotBlank(message = "Claimant phone is required")
    val claimantPhone: String,

    val healthDetails: Map<String, Any>? = null,
    val motorDetails: Map<String, Any>? = null,
    val generalDetails: Map<String, Any>? = null,
    val supportingDocuments: List<Map<String, Any>>? = null,
    val estimates: Map<String, Any>? = null
)

data class ClaimResponse(
    val id: Long,
    val reference: String,
    val policyNumber: String,
    val claimType: String,
    val claimantName: String,
    val claimantEmail: String,
    val claimantPhone: String,
    val status: String,
    val submittedAt: Instant,
    val healthDetails: Map<String, Any>? = null,
    val motorDetails: Map<String, Any>? = null,
    val generalDetails: Map<String, Any>? = null,
    val supportingDocuments: List<Map<String, Any>> = emptyList(),
    val estimates: Map<String, Any> = emptyMap()
)
