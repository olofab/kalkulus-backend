package com.timla.controller

import com.timla.dto.CreateUserRequest
import com.timla.dto.UpdateUserRequest
import com.timla.dto.UserResponse
import com.timla.model.User
import com.timla.repository.UserRepository
import com.timla.security.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @GetMapping("/users")
    fun getUsers(httpRequest: HttpServletRequest): ResponseEntity<List<UserResponse>> {
        val token = httpRequest.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        val userId = jwtUtil.getUserId(token)
        val adminUser = userRepository.findById(userId).orElse(null)
            ?: return ResponseEntity.status(403).build()

        if (!adminUser.isAdmin) return ResponseEntity.status(403).build()

        val usersInCompany = userRepository.findByCompanyId(adminUser.company.id)

        return ResponseEntity.ok(
            usersInCompany.map { user ->
                UserResponse(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    companyId = user.company.id,
                    userType = user.userType,
                    isAdmin = user.isAdmin
                )
            }
        )
    }

    @PostMapping("/users")
    fun createUser(
        @RequestBody request: CreateUserRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<User> {
        val token = httpRequest.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        val userId = jwtUtil.getUserId(token)
        val adminUser = userRepository.findById(userId).orElse(null)
            ?: return ResponseEntity.status(403).build()

        if (!adminUser.isAdmin) {
            return ResponseEntity.status(403).build()
        }

        val newUser = User(
            email = request.email,
            name = request.name,
            passwordHash = passwordEncoder.encode(request.password),
            userType = request.userType,
            company = adminUser.company,
            isAdmin = false
        )

        return ResponseEntity.ok(userRepository.save(newUser))
    }

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody request: UpdateUserRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<UserResponse> {
        val token = httpRequest.getHeader("Authorization")?.removePrefix("Bearer ")
            ?: return ResponseEntity.status(401).build()

        val adminUserId = jwtUtil.getUserId(token)
        val adminUser = userRepository.findById(adminUserId).orElse(null)
            ?: return ResponseEntity.status(403).build()

        if (!adminUser.isAdmin) return ResponseEntity.status(403).build()

        val userToUpdate = userRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        userToUpdate.name = request.name
        userToUpdate.email = request.email
        userToUpdate.userType = request.userType
        userRepository.save(userToUpdate)

        return ResponseEntity.ok(
            UserResponse(
                id = userToUpdate.id,
                name = userToUpdate.name,
                email = userToUpdate.email,
                companyId = userToUpdate.company.id,
                userType = userToUpdate.userType,
                isAdmin = userToUpdate.isAdmin
            )
        )
    }
}
