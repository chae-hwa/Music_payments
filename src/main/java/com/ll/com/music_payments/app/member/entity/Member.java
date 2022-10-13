package com.ll.com.music_payments.app.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.com.music_payments.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;

    @JsonIgnore
    private String password;
    private String email;
    private boolean emailVerified;

    private long restCash; // 보유한 캐시 금액

    public String getName() {
        return username;
    }

    public Member(long id) {
        super(id);
    }
}
