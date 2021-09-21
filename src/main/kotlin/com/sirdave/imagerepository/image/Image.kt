package com.sirdave.imagerepository.image

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sirdave.imagerepository.user.User
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "images")
class Image(
    var name: String,
    var description: String,
    var category: String,
    var imageUrl: String,
    @JsonIgnore
    var cloudinaryId: String,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    var user: User) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
    @SequenceGenerator(name = "image_sequence", sequenceName = "image_sequence", allocationSize = 1)
    var id: Long? = null

    var dateUploaded: LocalDate = LocalDate.now()

}
