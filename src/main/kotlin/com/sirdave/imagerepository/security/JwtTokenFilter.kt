package com.sirdave.imagerepository.security


import com.sirdave.imagerepository.user.UserRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtTokenFilter(private val jwtTokenUtil: JwtTokenUtil,
                     private val repository: UserRepository
) : OncePerRequestFilter() {


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain) {
        // Get authorization header and validate
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (StringUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response)
            return
        }

        // Get jwt token and validate
        val token = header.split(" ").toTypedArray()[1].trim { it <= ' ' }

        println("============TOKEN IS =============$token")
        if (!jwtTokenUtil.validate(token)) {
            chain.doFilter(request, response)
            return
        }

        // Get user identity and set it on the spring security context
        val userDetails = repository.findByUserName(jwtTokenUtil.getUsername(token))
            ?.orElse(null)

        val authentication = UsernamePasswordAuthenticationToken(
            userDetails, null,
            userDetails?.authorities ?: listOf()
        )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }
}
