package com.ll.com.music_payments.app.song.entity;

import com.ll.com.music_payments.app.base.entity.BaseEntity;
import com.ll.com.music_payments.app.member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
public class Song extends BaseEntity {

    private String subject;
    private String content;
    @ManyToOne(fetch = LAZY)
    private Member author;
}
