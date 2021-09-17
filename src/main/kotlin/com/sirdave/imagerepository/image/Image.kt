package com.sirdave.imagerepository.image

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sirdave.imagerepository.user.User
import javax.persistence.*

@Entity
@Table(name = "images")
class Image(name: String, description: String, category: String, imageUrl: String, cloudinaryId: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
    @SequenceGenerator(name = "image_sequence", sequenceName = "image_sequence", allocationSize = 1)
    var id: Long? = null
    var name: String? = name
    var description: String? = description
    var imageUrl: String? = imageUrl
    var category: String? = category
    @JsonIgnore
    var cloudinaryId: String? = cloudinaryId
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    var user: User? = null

}
