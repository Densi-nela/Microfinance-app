package com.shieldflow.claim.service

import com.shieldflow.claim.dto.ClaimRequest
import com.shieldflow.claim.dto.ClaimResponse
import com.shieldflow.claim.model.Claim
import com.shieldflow.claim.repository.ClaimRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import kotlin.random.Random

@Service
class ClaimService(private val claimRepository: ClaimRepository) {

    @Transactional(readOnly = true)
    fun getClaimsForUser(userId: Long, role: String): List<ClaimResponse> {
        val claims = if (role.uppercase() == "ADMIN") {
            claimRepository.findAllByOrderBySubmittedAtDesc()
        } else {
            claimRepository.findAllByUserIdOrUserIdIsNullOrderBySubmittedAtDesc(userId)
        }
        return claims.map { toResponseDto(it) }
    }

    @Transactional
    fun createClaim(request: ClaimRequest, userId: Long?): ClaimResponse {
        val reference = generateUniqueReference(request.claimType)
        
        // Extract type-specific details to map into single DB column
        val detailsMap = when (request.claimType.lowercase()) {
            "health" -> request.healthDetails ?: emptyMap()
            "motor" -> request.motorDetails ?: emptyMap()
            "general" -> request.generalDetails ?: emptyMap()
            else -> emptyMap()
        }

        val claim = Claim(
            reference = reference,
            userId = userId,
            policyNumber = request.policyNumber,
            claimType = request.claimType,
            claimantName = request.claimantName,
            claimantEmail = request.claimantEmail,
            claimantPhone = request.claimantPhone,
            status = "pending_review",
            submittedAt = Instant.now(),
            details = detailsMap,
            supportingDocuments = request.supportingDocuments ?: emptyList(),
            estimates = request.estimates ?: emptyMap()
        )

        val savedClaim = claimRepository.save(claim)
        return toResponseDto(savedClaim)
    }

    @Transactional
    fun updateClaimStatus(claimId: Long, newStatus: String): ClaimResponse? {
        val claim = claimRepository.findById(claimId).orElse(null) ?: return null
        
        // Validate status type
        val status = newStatus.lowercase().trim()
        if (status in listOf("pending_review", "under_review", "approved", "paid")) {
            claim.status = status
            val savedClaim = claimRepository.save(claim)
            return toResponseDto(savedClaim)
        }
        return null
    }

    private fun generateUniqueReference(claimType: String): String {
        var isUnique = false
        var reference = ""
        val suffix = claimType.firstOrNull()?.uppercaseChar() ?: 'G'
        
        while (!isUnique) {
            val randNum = Random.nextInt(100000, 999999)
            reference = "CLM-$randNum-$suffix"
            if (claimRepository.findByReference(reference) == null) {
                isUnique = true
            }
        }
        return reference
    }

    private fun toResponseDto(claim: Claim): ClaimResponse {
        val health = if (claim.claimType.lowercase() == "health") claim.details else null
        val motor = if (claim.claimType.lowercase() == "motor") claim.details else null
        val general = if (claim.claimType.lowercase() == "general") claim.details else null

        return ClaimResponse(
            id = claim.id ?: 0L,
            reference = claim.reference,
            policyNumber = claim.policyNumber,
            claimType = claim.claimType,
            claimantName = claim.claimantName,
            claimantEmail = claim.claimantEmail,
            claimantPhone = claim.claimantPhone,
            status = claim.status,
            submittedAt = claim.submittedAt,
            healthDetails = health,
            motorDetails = motor,
            generalDetails = general,
            supportingDocuments = claim.supportingDocuments,
            estimates = claim.estimates
        )
    }
}
