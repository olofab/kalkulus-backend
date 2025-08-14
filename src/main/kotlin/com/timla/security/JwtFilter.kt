package com.timla.security

import com.timla.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val userRepo: UserRepository
) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        println("Request path: $path – will be filtered: ${!path.startsWith("/api/auth")}")

        return path.startsWith("/api/auth")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        var token: String? = null

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7)
        }

        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            try {
                val companyId = jwtUtil.getCompanyId(token)
                val userId = jwtUtil.getUserId(token)
                val user = userRepo.findById(userId).orElse(null)

                if (user != null && user.company.id == companyId) {
                    val userDetails = org.springframework.security.core.userdetails.User(
                        user.email, user.passwordHash, listOf()
                    )

                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authToken
                }
            } catch (e: Exception) {
                println("Token-problem: ${e.message}")
            }
        }

        chain.doFilter(request, response)
    }
}

