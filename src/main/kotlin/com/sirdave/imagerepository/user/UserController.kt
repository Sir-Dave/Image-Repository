package com.sirdave.imagerepository.user


import com.sirdave.imagerepository.auth.UserResponse
import com.sirdave.imagerepository.security.JwtTokenUtil
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/v1/user")
class UserController(private val jwtTokenUtil: JwtTokenUtil,
                     private val userService: UserService
) {

    @GetMapping("/profile")
    fun getMyProfile(request: HttpServletRequest): ResponseEntity<UserResponse<*>>{
        return try {
            val user = getCurrentLoggedUser(request)
            val response = UserResponse(success = true, data = user, null)
            ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
        } catch (error: NullPointerException){
            val response = UserResponse(success = false, data = "You have to log in first", null)
            ResponseEntity(response, HttpHeaders(), HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("/{username}")
    fun getUserProfile(@PathVariable username: String): ResponseEntity<UserResponse<*>>{
        val otherUser = userService.findUserByUsername(username)
        return try {
            check(otherUser != null){
                val response = UserResponse(success = false, data = "User does not exist", null)
                return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
            }
            val response = UserResponse(success = true, data = otherUser, null)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)

        } catch (error: NullPointerException){
            val response = UserResponse(success = false, data = "You have to log in first", null)
            ResponseEntity(response, HttpHeaders(), HttpStatus.UNAUTHORIZED)
        }
    }

    private fun getCurrentLoggedUser(request: HttpServletRequest): User{
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = header.split(" ").toTypedArray()[1].trim { it <= ' ' }

        val id = jwtTokenUtil.getUserId(token)
        println("Cart Controller: User id is $id")
        return userService.getOneUser(id.toLong())
    }
}