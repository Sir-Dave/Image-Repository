package com.sirdave.imagerepository.security


import com.sirdave.imagerepository.user.User
import io.jsonwebtoken.*
import org.slf4j.Logger
import org.springframework.stereotype.Component
import java.lang.String.format
import java.util.*

@Component
class JwtTokenUtil {
    private val jwtSecret = "gsjuxhw762jdhbskP"
    private val logger: Logger? = null

    fun generateAccessToken(user: User): String {
        val jwtIssuer = "shopify-image-repository"
        return Jwts.builder()
            .setSubject(format("%s,%s", user.id, user.username))
            .setIssuer(jwtIssuer)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun getUserId(token: String?): String {
        val claims: Claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .body
        return claims.subject.split(",")[0]
    }

    fun getUsername(token: String?): String {
        val claims: Claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .body
        return claims.subject.split(",")[1]
    }

    fun getExpirationDate(token: String?): Date {
        val claims: Claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .body
        return claims.expiration
    }

    fun validate(token: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            logger!!.error("Invalid JWT signature - {}", ex.message)
        } catch (ex: MalformedJwtException) {
            logger!!.error("Invalid JWT token - {}", ex.message)
        } catch (ex: ExpiredJwtException) {
            logger!!.error("Expired JWT token - {}", ex.message)
        } catch (ex: UnsupportedJwtException) {
            logger!!.error("Unsupported JWT token - {}", ex.message)
        } catch (ex: IllegalArgumentException) {
            logger!!.error("JWT claims string is empty - {}", ex.message)
        }
        return false
    }
}