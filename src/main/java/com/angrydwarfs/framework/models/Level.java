package com.angrydwarfs.framework.models;

import lombok.*;

import javax.persistence.*;

/**
 * Уровни тэгов пользователя. Записывается в БД в таблицу с имененм level.
 * @version 0.001
 * @author habatoo
 *
 * @param "id" - primary key таблицы users.
 * @param "levelName" - наименование уровня - связь с значениями в
 * @see ELevel (значения уровня).
 * @param "tagLevel" - связь с таблицей тэгов.
 * @see Tag (пользователи).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "levels")
@ToString(of = {"id", "levelName"})
@EqualsAndHashCode(of = {"id"})
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ELevel levelName;

    @OneToOne(mappedBy = "tagLevel")
    private Tag tagLevel;

    public Level(ELevel levelName) {
        this.levelName = levelName;
    }
}
