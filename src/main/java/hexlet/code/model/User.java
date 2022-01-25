package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    //@NotBlank
    private String lastName;

    //@Column(unique = true)
    private String email;

    //@NotBlank
    private String password;

    // private ZonedDateTime createdAt;
    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
