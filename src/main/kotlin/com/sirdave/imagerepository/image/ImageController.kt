package com.sirdave.imagerepository.image

import com.sirdave.imagerepository.auth.UserResponse
import com.sirdave.imagerepository.user.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("api/v1/images")
class ImageController(private val imageService: ImageService,
                      private val userService: UserService) {

    @PostMapping
    fun uploadImage(
        @RequestParam name: String,
        @RequestParam description: String,
        @RequestParam category: String,
        @RequestParam file: MultipartFile): ResponseEntity<ImageResponse<*>>{

        val data = imageService.uploadToCloudinary(file.bytes, name, category)
        val imageUrl = data["url"] as String?
        val cloudinaryID = data["public_id"] as String
        val image = imageUrl?.let { Image(name, description, category, it, cloudinaryID) }
        val newImage = imageService.addImage(image!!)
        val response = ImageResponse(true, newImage)
        return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
    }

    @GetMapping
    fun getImages(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") pageSize: Int,
        @RequestParam(defaultValue = "") category: String,
        @RequestParam(defaultValue = "") username: String): ResponseEntity<ImageResponse<*>>{

        val user = userService.findUserByUsername(username)
        val isCategoryExists = imageService.isCategoryExists(category)

        if (category != "" && username != ""){
            check(user != null){
                val response = ImageResponse(success = false, data = "User does not exist")
                return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
            }

            check(isCategoryExists){
                val response = ImageResponse(success = false, data = "Category not found")
                return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
            }

            val images = imageService.findImagesByCategoryAndUser(category, user, page,  pageSize)
            val response = ImageResponse(success = true, data = images)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
        }
        else if (username != ""){
            check(user != null){
                val response = ImageResponse(success = false, data = "User does not exist")
                return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
            }
            val images = imageService.findImagesByUser(user)
            val response = ImageResponse(success = true, data = images)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
        }
        else if (category != ""){
            check(isCategoryExists){
                val response = ImageResponse(success = false, data = "Category not found")
                return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
            }
            val images = imageService.findImagesByCategory(category, page, pageSize)
            val response = ImageResponse(success = true, data = images)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
        }

        else{
            val images =  imageService.getAllImages(page, pageSize)
            val response = ImageResponse(success = true, data = images)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
        }
    }


    @GetMapping( "/{id}")
    fun findImageById(@PathVariable id: Long):
            ResponseEntity<ImageResponse<*>>{
        check(imageService.isImageExists(id)){
            val response = ImageResponse(false, data = "No image with id $id found")
            return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
        }
        val image = imageService.findImageById(id)
        val response = ImageResponse(true, data = image)
        return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateImageProperties(id: Long){

    }

    @DeleteMapping("/{id}")
    fun deleteImage(id: Long): ResponseEntity<ImageResponse<*>>{
        check(imageService.isImageExists(id)){
            val response = ImageResponse(false, data = "No image with id $id found")
            return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
        }
        imageService.deleteImage(id)
        val response = ImageResponse(true, data = "Image deleted successfully")
        return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
    }
}