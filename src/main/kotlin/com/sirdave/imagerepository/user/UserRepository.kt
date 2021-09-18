package com.sirdave.imagerepository.user

import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface UserRepository: PagingAndSortingRepository<User, Long>{

    fun findByUserName(username: String?): Optional<User?>?
}