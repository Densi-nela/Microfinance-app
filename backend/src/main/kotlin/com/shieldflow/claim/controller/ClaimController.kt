package com.shieldflow.claim.controller

import com.shieldflow.claim.dto.ClaimRequest
import com.shieldflow.claim.dto.ClaimResponse
import com.shieldflow.claim.service.ClaimService
import com.shieldflow.claim.util.JwtUtil
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = ["*"])
class ClaimController(
    private val claimService: ClaimService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping
    fun getAllClaims(@RequestHeader("Authorization", required = false) authHeader: String?): ResponseEntity<Any> {
        val tokenInfo = getAuthenticatedUser(authHeader) 
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized. Please log in."))

        val claims = claimService.getClaimsForUser(tokenInfo.userId, tokenInfo.role)
        return ResponseEntity.ok(claims)
    }

    @PostMapping
    fun createClaim(
        @RequestHeader("Authorization", required = false) authHeader: String?,
        @Valid @RequestBody request: ClaimRequest
    ): ResponseEntity<Any> {
        val tokenInfo = getAuthenticatedUser(authHeader)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized. Please log in."))

        val response = claimService.createClaim(request, tokenInfo.userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PatchMapping("/{id}/status")
    fun updateClaimStatus(
        @RequestHeader("Authorization", required = false) authHeader: String?,
        @PathVariable id: Long,
        @RequestBody body: Map<String, String>
    ): ResponseEntity<Any> {
        val tokenInfo = getAuthenticatedUser(authHeader)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized. Please log in."))

        if (tokenInfo.role != "ADMIN") {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mapOf("error" to "Access Denied. Admin privilege required."))
        }

        val newStatus = body["status"] 
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "Status field is required"))

        val response = claimService.updateClaimStatus(id, newStatus)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to "Claim not found"))

        return ResponseEntity.ok(response)
    }

    private fun getAuthenticatedUser(authHeader: String?): com.shieldflow.claim.util.DecodedJwtInfo? {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null
        val token = authHeader.substring(7)
        return jwtUtil.verifyToken(token)
    }
}
