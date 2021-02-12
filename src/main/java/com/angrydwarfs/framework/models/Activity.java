package com.angrydwarfs.framework.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Модель активностей пользователей
 * @version 0.013
 * @author habatoo
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "activity")
@ToString(of = {"id", "activityTitle", "activityBody", "creationDate", "activityCode"})
@EqualsAndHashCode(of = {"id"})
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ActivityIndex cannot be empty")
    private String activityIndex = UUID.randomUUID().toString();

    @NotBlank(message = "Title cannot be empty")
    private String activityTitle;
    @NotBlank(message = "Body cannot be empty")
    private String activityBody;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userActivities;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @OneToMany(mappedBy = "userTags", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonView(Views.ShortData.class)
    private Set<Tag> tags;

}
