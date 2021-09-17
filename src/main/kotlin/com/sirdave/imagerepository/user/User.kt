package com.sirdave.imagerepository.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sirdave.imagerepository.image.Image
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "allUsers")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    var id: Long,
    var firstName: String,
    var lastName: String,
    var userName: String,
    var email: String,
    @JsonIgnore
    var password: String,
    var dateJoined: LocalDate,
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var images: List<Image>
)
