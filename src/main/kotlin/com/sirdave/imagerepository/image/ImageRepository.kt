package com.sirdave.imagerepository.image

import com.sirdave.imagerepository.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository: PagingAndSortingRepository<Image, Long> {

    @Query("SELECT i FROM Image i WHERE i.user = ?1")
    fun findImagesByUser(user: User, pageable: Pageable): Page<Image>

    @Query("SELECT i FROM Image i WHERE i.category = ?1")
    fun findImagesByCategory(category: String, pageable: Pageable): Page<Image>

    @Query("SELECT i FROM Image i WHERE i.category = ?1 and i.user = ?2")
    fun findImagesByCategoryAndUser(category: String, user: User, pageable: Pageable): Page<Image>

}