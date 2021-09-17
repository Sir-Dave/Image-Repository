package com.sirdave.imagerepository.image

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/images")
class ImageController(private val imageService: ImageService) {

    @PostMapping
    fun uploadImage(){

    }

    @GetMapping
    fun getImages(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "50") pageSize: Int,
        @RequestParam(defaultValue = "") category: String,
        @RequestParam(defaultValue = "") username: String): ResponseEntity<List<Image>>{
        /**if (category != "" && username != ""){
            //imageService.findImagesByCategoryAndUser(category, user, page,  pageSize)
        }
        else if (username != ""){
            //val user = User()
            //imageService.findImagesByUser()

        }
        else if (category != ""){
            imageService.findImagesByCategory(category, page, pageSize)
        }

        else{

        }*/
        val images =  imageService.getAllImages(page, pageSize)
        return ResponseEntity(images, HttpHeaders(), HttpStatus.OK)

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