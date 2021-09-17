package com.sirdave.imagerepository.image

import com.sirdave.imagerepository.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ImageService(private val imageRepository: ImageRepository) {

    fun addImage(image: Image): Image{
        return imageRepository.save(image)
    }

    fun getAllImages(pageNo: Int, pageSize: Int): List<Image>{
        val pageable = PageRequest.of(pageNo, pageSize)
        val pagedResult = imageRepository.findAll(pageable)
        return filterResult(pagedResult)
    }

    fun isImageExists(id: Long): Boolean{
        return imageRepository.existsById(id)
    }

    fun isImageExistByUser(user: User): Boolean{
        val carts = imageRepository.findAll()
        return carts.any { it.user == user }
    }

    fun filterResult(pagedResult: Page<Image>): List<Image>{
        return if (pagedResult.hasContent()){
            pagedResult.content
        } else{
            ArrayList()
        }
    }

    fun findImagesByCategoryAndUser(category: String, user: User, pageNo: Int, pageSize: Int): List<Image> {
        val pageable = PageRequest.of(pageNo, pageSize)
        val pagedResult = imageRepository.findImagesByCategoryAndUser(category, user, pageable)
        return filterResult(pagedResult)
    }


    fun findImagesByCategory(category: String, pageNo: Int, pageSize: Int): List<Image> {
        val pageable = PageRequest.of(pageNo, pageSize)
        val pagedResult = imageRepository.findImagesByCategory(category, pageable)
        return filterResult(pagedResult)
    }

    fun findImagesByUser(user: User, pageNo: Int = 0, pageSize: Int = 50): List<Image>{
        val pageable = PageRequest.of(pageNo, pageSize)
        val pagedResult = imageRepository.findImagesByUser(user, pageable)
        return filterResult(pagedResult)
    }

    fun getUserOwnedImage(user: User, imageId: Long): Image{
        val images = findImagesByUser(user)
        return images.first { it.id == imageId }
    }

    fun findImageById(id: Long): Image{
        return imageRepository.findById(id)
            .orElseThrow{IllegalStateException("Image with id $id does not exist")}
    }

    fun deleteImage(id: Long){
        val exists = imageRepository.existsById(id)
        check(exists) { "Image with id $id does not exist" }
        imageRepository.deleteById(id)

    }

    fun updateImageProperties(imageId: Long, user: User): Image?{
        val image = getUserOwnedImage(user, imageId)
        // edit the properties here
        return null
    }



}