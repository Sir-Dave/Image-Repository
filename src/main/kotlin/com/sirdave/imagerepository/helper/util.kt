package com.sirdave.imagerepository.helper

import com.sirdave.imagerepository.security.JwtTokenUtil
import com.sirdave.imagerepository.user.User
import com.sirdave.imagerepository.user.UserService
import org.springframework.http.HttpHeaders
import javax.servlet.http.HttpServletRequest

fun getCurrentLoggedUser(request: HttpServletRequest, jwtTokenUtil: JwtTokenUtil,
                         userService: UserService): User? {
    val header = request.getHeader(HttpHeaders.AUTHORIZATION)
    val token = header.split(" ").toTypedArray()[1].trim { it <= ' ' }

    val id = jwtTokenUtil.getUserId(token)
    println("AppDebug: User id is $id")
    return userService.getOneUser(id.toLong())
}