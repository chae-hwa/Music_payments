package com.ll.com.music_payments.app.song.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SongForm {

    @NotEmpty
    private String subject;
    @NotEmpty
    private String content;
}
