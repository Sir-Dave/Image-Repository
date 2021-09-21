package com.sirdave.imagerepository.helper

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils

object CloudinaryConf {
    fun getConfiguration(): Cloudinary {
        return Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET"),
                "secure", "true"
            )
        )

    }
}