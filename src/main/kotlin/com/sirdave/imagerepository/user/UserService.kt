package com.sirdave.imagerepository.user

import com.sirdave.imagerepository.image.Image
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(private val userRepository: UserRepository,
                  private val passwordEncoder: BCryptPasswordEncoder) : UserDetailsService {


   @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String?): UserDetails? {
       return userRepository.findByEmail(email)
           ?.orElseThrow{
               UsernameNotFoundException("User with email $email not found")
           }
    }

    fun getOneUser(id: Long): User? = userRepository.findById(id)
        .orElseThrow{
            IllegalStateException("User with $id does not exist")
        }

    fun findUserByUsername(username: String?): User? {
        return userRepository.findByUserName(username)
            ?.orElseThrow{
                UsernameNotFoundException("User with username $username not found")
            }
    }


    fun isUserExists(username: String?): Boolean{
        return userRepository.findByUserName(username!!)!!.isPresent
    }

    fun isUserExistsByEmail(email: String?): Boolean{
        return userRepository.findByEmail(email)!!.isPresent
    }

    fun doPasswordsMatch(p1: String, p2: String): Boolean{
       return p1 == p2
    }

    fun saveUser(user: User): User {
        val encodedPassword = passwordEncoder.encode(user.password)
        user.setPassword(encodedPassword)
        return userRepository.save(user)
    }

    fun getImagesByUser(username: String): List<Image>?{
        val user = findUserByUsername(username)
        return user?.images
    }
}