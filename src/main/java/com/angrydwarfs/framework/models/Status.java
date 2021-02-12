package com.angrydwarfs.framework.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Модель текущего статуса пользователя с указанием срока начала и окончания действия статуса
 * @version 0.001
 * @author habatoo
 *
 */
@Entity
@Getter
@Setter
@Table(name = "status")
@ToString(of = {"id", "statusName", "activationDate", "endDate"})
@EqualsAndHashCode(of = {"id"})
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus userStatus;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activationDate;

    @Column(updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userCurrentStatus;

    public Status() {
        this.userStatus = EStatus.COMMON;
        this.activationDate = LocalDateTime.now();
        this.endDate = null;
    }

    public Status(EStatus userStatus, LocalDateTime endDate) {
        this.userStatus = userStatus;
        this.activationDate = LocalDateTime.now();
        this.endDate = endDate;
    }

}
