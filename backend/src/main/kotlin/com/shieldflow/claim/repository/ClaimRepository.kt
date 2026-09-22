package com.shieldflow.claim.repository

import com.shieldflow.claim.model.Claim
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClaimRepository : JpaRepository<Claim, Long> {
    fun findByReference(reference: String): Claim?
    fun findAllByOrderBySubmittedAtDesc(): List<Claim>
    fun findAllByUserIdOrUserIdIsNullOrderBySubmittedAtDesc(userId: Long): List<Claim>
}
