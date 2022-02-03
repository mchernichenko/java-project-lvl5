package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;

import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@Table(name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Column(unique = true)
    private String email;

    @NotBlank
    @JsonIgnore
    private String password;

    // private ZonedDateTime createdAt;
    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
