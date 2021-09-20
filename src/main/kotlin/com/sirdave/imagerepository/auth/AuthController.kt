package com.sirdave.imagerepository.auth

import com.sirdave.imagerepository.security.JwtTokenUtil
import com.sirdave.imagerepository.user.User
import com.sirdave.imagerepository.user.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("api/v1/auth")
class AuthController(private val jwtTokenUtil: JwtTokenUtil,
                     private val authenticationManager: AuthenticationManager,
                     private val userService: UserService
) {

    @PostMapping("register")
    fun registerUser(@RequestBody registerRequest: RegisterRequest)
    : ResponseEntity<UserResponse<*>>{

        // Has a user previously registered with that email address?
        check(!userService.isUserExistsByEmail(registerRequest.email)){
            val response = UserResponse(false, data = "User with email already exists", null)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.BAD_REQUEST)
        }

        // Has a user previously registered with that username?
        check(!userService.isUserExists(registerRequest.userName)){
            val response = UserResponse(false, data = "User with username already exists", null)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.BAD_REQUEST)
        }

        // Do the passwords match?
        check(userService.doPasswordsMatch(registerRequest.password,
            registerRequest.confirmPassword)){
            val response = UserResponse(false, data = "Passwords do not match", null)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.BAD_REQUEST)
        }

        val user = User(registerRequest.firstName, registerRequest.lastName, registerRequest.userName,
            registerRequest.email, registerRequest.password, LocalDate.now())

        userService.saveUser(user)
        val response = UserResponse(true, data = "User registered successfully", null)
        return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
    }

    @PostMapping("login")
    fun loginUser(@RequestBody signInRequest: SignInRequest): ResponseEntity<*>{
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(signInRequest.email, signInRequest.password)
            )
            val user = authentication.principal as User
            val token = jwtTokenUtil.generateAccessToken(user)
            val response = UserResponse(true, "User successfully authenticated", token)
            return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body<Any>(response)
        }
        catch (ex: BadCredentialsException){
            println(ex.message)
            val response = UserResponse(false, data = ex.message, null)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.UNAUTHORIZED)
        }
    }
}