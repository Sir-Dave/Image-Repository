package com.sirdave.imagerepository.image

import com.sirdave.imagerepository.helper.getCurrentLoggedUser
import com.sirdave.imagerepository.security.JwtTokenUtil
import com.sirdave.imagerepository.user.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/v1/images")
class ImageController(private val imageService: ImageService,
                      private val userService: UserService,
                      private val jwtTokenUtil: JwtTokenUtil) {

    @PostMapping
    fun uploadImage(
        @RequestParam name: String,
        @RequestParam description: String,
        @RequestParam category: String,
        @RequestParam file: MultipartFile,
        request: HttpServletRequest): ResponseEntity<ImageResponse<*>>{

        val user = getCurrentLoggedUser(request, jwtTokenUtil, userService)

        val data = imageService.uploadToCloudinary(file.bytes, name, category)
        data?.let {
            val imageUrl = data["url"] as String?
            val cloudinaryID = data["public_id"] as String

            user?.let {
                val image = imageUrl?.let { Image(name, description, category, it, cloudinaryID, user) }
                val newImage = imageService.addImage(image!!)
                val response = ImageResponse(true, newImage)
                return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
            }
        }

        val response = ImageResponse(success = false, data = "An error occurred")
        return ResponseEntity(response, HttpHeaders(), HttpStatus.BAD_REQUEST)
    }

    @GetMapping
    fun getImages(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") pageSize: Int,
        @RequestParam(defaultValue = "") category: String,
        @RequestParam(defaultValue = "") username: String): ResponseEntity<ImageResponse<*>>{

        if (category != ""){
            val isCategoryExists = imageService.isCategoryExists(category)
            check(isCategoryExists){
                val response = ImageResponse(success = false, data = "Category not found")
                return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
            }

            if (username != ""){
                val user = userService.findUserByUsername(username)
                check(user != null){
                    val response = ImageResponse(success = false, data = "User does not exist")
                    return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
                }

                // filter by username and category
                val images = imageService.findImagesByCategoryAndUser(category, user, page,  pageSize)
                val response = ImageResponse(success = true, data = images)
                return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)

            }
            else{
                //filter by category only
                val images = imageService.findImagesByCategory(category, page, pageSize)
                val response = ImageResponse(success = true, data = images)
                return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
            }
        }
        else if (username != ""){
            val user = userService.findUserByUsername(username)
            check(user != null){
                val response = ImageResponse(success = false, data = "User does not exist")
                return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
            }
            //filter by username only
            val images = imageService.findImagesByUser(user)
            val response = ImageResponse(success = true, data = images)
            return ResponseEntity(response, HttpHeaders(), HttpStatus.OK)
        }

        else{
            val images = imageService.getAllImages(page, pageSize)
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
    fun updateImageProperties(@PathVariable id: Long){

    }

    @DeleteMapping("/{id}")
    fun deleteImage(@PathVariable id: Long): ResponseEntity<ImageResponse<*>>{
        check(imageService.isImageExists(id)){
            val response = ImageResponse(false, data = "No image with id $id found")
            return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
        }
        imageService.deleteFromCloudinary(id)
        imageService.deleteImage(id)
        val response = ImageResponse(true, data = "Image deleted successfully")
        return ResponseEntity(response, HttpHeaders(), HttpStatus.NOT_FOUND)
    }
}