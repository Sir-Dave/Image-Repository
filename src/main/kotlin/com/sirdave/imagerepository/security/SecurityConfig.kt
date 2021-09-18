package com.sirdave.imagerepository.security


import com.sirdave.imagerepository.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val repository: UserRepository,
    private val jwtTokenFilter: JwtTokenFilter
) : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Throws(Exception::class)
    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(
            UserDetailsService { username: String ->
                repository.findByUserName(username)
                    ?.orElseThrow { UsernameNotFoundException("User: $username not found") }
            }
        ).passwordEncoder(bCryptPasswordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        corsFilter()

        // Enable CORS and disable CSRF
        var http = http
        http = http.cors().and().csrf().disable()

        // Set session management to stateless
        http = http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

        // Set unauthorized requests exception handler
        http = http.exceptionHandling()
            .authenticationEntryPoint { _: HttpServletRequest?, response: HttpServletResponse, ex: AuthenticationException ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.message)
            }
            .and()

        // Set permissions on endpoints
        http.authorizeRequests()

            // Our public endpoints
            .antMatchers("/api/v*/auth/login").permitAll()
            .antMatchers("/api/v*/auth/register").permitAll()
            .antMatchers(HttpMethod.GET, "/api/v*/images/**").permitAll()
            .anyRequest().authenticated() // Our private endpoints

        // Add JWT token filter
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    // Used by spring security if CORS is enabled.
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOriginPatterns = listOf("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}