package fraud.detection.app.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    //General info
    @NotEmpty(message = "This field 'model' should not be empty")
    private String firstName;
    @NotNull(message = "This field 'model' should not be empty")
    private String middleName;
    @NotNull(message = "This field 'model' should not be empty")
    private String lastName;
    @NotNull(message = "This field 'model' should not be empty")
    private String dateOfBirth;
    @NotNull(message = "This field 'model' should not be empty")
    private String gender;
    @NotNull(message = "This field 'model' should not be empty")
    private String occupation;
    @NotNull(message = "This field 'model' should not be empty")
    private String password;

    //Contact Information
    @NotNull(message = "This field 'model' should not be empty")
    @Column(unique = true)
    private String mobileNumber;
    @NotNull(message = "This field 'model' should not be empty")
    private String email;
    @NotNull(message = "This field 'model' should not be empty")
    private String permanentAddress;
    @NotNull(message = "This field 'model' should not be empty")
    private String currentAddress;
    @NotNull(message = "This field 'model' should not be empty")
    private String pinCode;
    @NotNull(message = "This field 'model' should not be empty")
    private String city;
    @NotNull(message = "This field 'model' should not be empty")
    private String state;
    @NotNull(message = "This field 'model' should not be empty")
    private String country;

//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
//    private Account account;

    @CreationTimestamp
    private LocalDateTime created_at;
    @UpdateTimestamp
    private LocalDateTime updated_at;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return mobileNumber ;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
