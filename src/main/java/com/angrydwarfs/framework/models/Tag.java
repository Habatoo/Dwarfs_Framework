package com.angrydwarfs.framework.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

/**
 * Тэги пользователя. Записывается в БД в таблицу с имененм tags.
 * @version 0.001
 * @author habatoo
 *
 * @param "id" - primary key таблицы users.
 * @param "tagName" - тэг - вид активности.
 * @param "userTags" - пользователь тэга.
 * @see User (пользователи).
 * @param "tagLevel" - уровень тэга.
 * @see Level (уровень).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tags")
@ToString(of = {"id", "tagName"})
@EqualsAndHashCode(of = {"id"})
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ETag tagName;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userTags;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity userActivity;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "level_id")
    private Level tagLevel;

    public Tag(ETag tagName) {
        this.tagName = tagName;
    }
}
