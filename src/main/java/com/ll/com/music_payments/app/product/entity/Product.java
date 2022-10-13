package com.ll.com.music_payments.app.product.entity;

import com.ll.com.music_payments.app.base.entity.BaseEntity;
import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.song.entity.Song;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = PROTECTED)
public class Product extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private Member author;

    @ManyToOne(fetch = LAZY)
    private Song song;

    private String subject;
    private int price;
}
