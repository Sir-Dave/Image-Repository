package com.sirdave.imagerepository.image

import com.cloudinary.utils.ObjectUtils
import com.sirdave.imagerepository.user.User
import com.sirdave.imagerepository.helper.CloudinaryConf
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ImageService(private val imageRepository: ImageRepository) {
    val cloudinary = CloudinaryConf.getConfiguration()

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

    fun isCategoryExists(category: String): Boolean{
        val images  = imageRepository.findAll()
        return images.any{ it.category == category}
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

    fun isImageUploadedByUser(id: Long, user: User): Boolean{
        val image = findImageById(id)
        return image.user == user
    }

    fun deleteImage(id: Long){
        val exists = imageRepository.existsById(id)
        check(exists) { "Image with id $id does not exist" }
        imageRepository.deleteById(id)

    }

    fun deleteFromCloudinary(imageId: Long){
        val image = imageRepository.findByIdOrNull(imageId)
        val publicId = image?.cloudinaryId
        val response = cloudinary.uploader().destroy(publicId,
            ObjectUtils.emptyMap()) as HashMap<Any?, Any?>

        println("delete response is ==========> $response")
    }


    fun uploadToCloudinary(file: ByteArray, name: String, category: String): HashMap<Any?, Any?>? {
        val data = cloudinary.uploader().upload(
            file,
            ObjectUtils.asMap(
                "folder", "image_repository/$category/",
                "use_filename", "true"
            )
        ) as HashMap<Any?, Any?>

        println("Cloudinary response data is =========> $data")
        return data
    }
}