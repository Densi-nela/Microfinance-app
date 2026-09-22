package com.shieldflow.claim.controller

import com.shieldflow.claim.dto.AuthResponse
import com.shieldflow.claim.dto.LoginRequest
import com.shieldflow.claim.dto.RegisterRequest
import com.shieldflow.claim.model.User
import com.shieldflow.claim.repository.UserRepository
import com.shieldflow.claim.util.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["*"])
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<Any> {
        // Check if email already exists
        if (userRepository.findByEmail(request.email.lowercase().trim()) != null) {
            return ResponseEntity.badRequest().body(mapOf("email" to "Email is already registered"))
        }

        // Validate role input
        val role = request.role.uppercase().trim()
        if (role != "CUSTOMER" && role != "ADMIN") {
            return ResponseEntity.badRequest().body(mapOf("role" to "Role must be CUSTOMER or ADMIN"))
        }

        // Encrypt the password
        val passwordHash = passwordEncoder.encode(request.password)

        val user = User(
            email = request.email.lowercase().trim(),
            passwordHash = passwordHash,
            fullName = request.fullName.trim(),
            role = role
        )

        val savedUser = userRepository.save(user)

        // Generate Token automatically on sign up for instantaneous login
        val token = jwtUtil.generateToken(
            savedUser.id!!,
            savedUser.email,
            savedUser.role,
            savedUser.fullName
        )

        val response = AuthResponse(
            token = token,
            id = savedUser.id!!,
            email = savedUser.email,
            fullName = savedUser.fullName,
            role = savedUser.role
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> {
        val user = userRepository.findByEmail(request.email.lowercase().trim())
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Invalid email or password"))

        // Verify the password hash
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Invalid email or password"))
        }

        // Generate the token
        val token = jwtUtil.generateToken(
            user.id!!,
            user.email,
            user.role,
            user.fullName
        )

        val response = AuthResponse(
            token = token,
            id = user.id!!,
            email = user.email,
            fullName = user.fullName,
            role = user.role
        )

        return ResponseEntity.ok(response)
    }
}
