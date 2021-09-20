package com.sirdave.imagerepository.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sirdave.imagerepository.image.Image
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "allUsers")
class User(
    firstName: String,
    lastName: String,
    userName: String,
    email: String,
    password: String,
    dateJoined: LocalDate) : UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence")
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    var id: Long? = null
    var firstName: String? = firstName
    var lastName: String? = lastName
    var userName: String? = userName
    var email: String? = email

    @JsonIgnore
    private var password: String? = password
    var dateJoined: LocalDate? = dateJoined

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    var images: List<Image>? = null

    fun setPassword(password: String?) {
        this.password = password
    }

    @JsonIgnore
    override fun getAuthorities(): List<SimpleGrantedAuthority?> {
        val authority = SimpleGrantedAuthority("USER")
        return listOf(authority)
    }

    @JsonIgnore
    override fun getPassword(): String {
        return password!!
    }

    @JsonIgnore
    override fun getUsername(): String {
        return userName!!
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true
    }
}
