package com.shieldflow.claim.controller

import com.shieldflow.claim.dto.ClaimRequest
import com.shieldflow.claim.dto.ClaimResponse
import com.shieldflow.claim.service.ClaimService
import javax.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = ["http://localhost:5173"])
class ClaimController(private val claimService: ClaimService) {

    @GetMapping
    fun getAllClaims(): ResponseEntity<List<ClaimResponse>> {
        val claims = claimService.getAllClaims()
        return ResponseEntity.ok(claims)
    }

    @PostMapping
    fun createClaim(@Valid @RequestBody request: ClaimRequest): ResponseEntity<ClaimResponse> {
        val response = claimService.createClaim(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
