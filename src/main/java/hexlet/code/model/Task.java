package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Date;
import java.util.Set;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.TemporalType.TIMESTAMP;
import static org.hibernate.annotations.FetchMode.JOIN;

@Entity
@Getter
@Setter
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @ManyToOne
    private User author;

    @ManyToOne
    private User executor;

    @ManyToOne
    private TaskStatus taskStatus;

    @ManyToMany
    @Fetch(JOIN)
    private Set<Label> labels;

    @NotBlank
    @Size(min = 3, max = 1000)
    private String name;

    private String description;

    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;
}
