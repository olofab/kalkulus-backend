package com.timla.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import java.nio.charset.StandardCharsets
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import io.jsonwebtoken.Claims
import com.timla.model.User

@Component
class JwtUtil(@Value("\${jwt.secret}") secret: String) {

    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    private val expirationMs = 7 * 24 * 60 * 60 * 1000 // 7 days in milliseconds

    fun generateToken(user: User): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationMs)

    return Jwts.builder()
        .setSubject(user.email)                         // Email som subject
        .claim("userId", user.id)                       // userId som claim
        .claim("companyId", user.company.id)           // companyId
        .claim("isAdmin", user.isAdmin)                 // ev. isAdmin
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key)
        .compact()
    } 


    fun getUserId(token: String): Long {
        val claims = parseToken(token)
        println("userId claim: ${claims["userId"]}") // debug
        return claims["userId"].toString().toLong()
    }

    fun getCompanyId(token: String): Long {
        val claims = parseToken(token)
        println("companyId claim: ${claims["companyId"]}") // debug
        return claims["companyId"].toString().toLong()
    }    
    
    fun getEmail(token: String): String = parseToken(token).subject
    fun isAdmin(token: String): Boolean = parseToken(token)["isAdmin"].toString().toBoolean()

    private fun parseToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
