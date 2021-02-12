package com.angrydwarfs.framework.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Перечень возможных ролей по пользователя по доступам к сервисам и необходимым полям
 * @version 0.001
 * @author habatoo
 * @param "id" - primary key таблицы roles.
 * @param "subRoleName" - наименовение роли.
 * @see ESubRole (перечень возможных ролей пользователя).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sub_roles")
@ToString(of = {"id", "subRoleName"})
@EqualsAndHashCode(of = {"id"})
public class SubRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ESubRole subRoleName;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userSubRoles;

    public SubRole(ESubRole subRoleName) {
        this.subRoleName = subRoleName;
    }

}

