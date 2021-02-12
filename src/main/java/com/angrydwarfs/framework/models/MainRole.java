package com.angrydwarfs.framework.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;

/**
 * Модель ролей по доступу к данным пользователей и возможности редактирования и удаления данных.
 * @version 0.001
 * @author habatoo
 * @param "id" - primary key таблицы roles.
 * @param "mainRoleName" - наименовение роли.
 * @see EMainRole (перечень возможных ролей пользователя).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "main_roles")
@ToString(of = {"id", "mainRoleName"})
@EqualsAndHashCode(of = {"id"})
public class MainRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EMainRole mainRoleName;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userMainRoles;

    public MainRole(EMainRole mainRoleName) {
        this.mainRoleName = mainRoleName;
    }

}
