package com.sirdave.imagerepository.image

import com.sirdave.imagerepository.user.User
import javax.persistence.*

@Entity
@Table(name = "images")
class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
    @SequenceGenerator(name = "image_sequence", sequenceName = "image_sequence", allocationSize = 1)
    var id: Long,
    var name: String,
    var description: String,
    var stringUrl: String,
    var email: String,
    var category: String,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    var user: User,
)
