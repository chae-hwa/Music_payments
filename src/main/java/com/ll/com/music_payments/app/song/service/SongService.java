package com.ll.com.music_payments.app.song.service;

import com.ll.com.music_payments.app.member.entity.Member;
import com.ll.com.music_payments.app.song.entity.Song;
import com.ll.com.music_payments.app.song.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SongService {

    private final SongRepository songRepository;

    @Transactional
    public Song create(Member author, String subject, String content) {

        Song song = Song.builder()
                .author(author)
                .subject(subject)
                .content(content)
                .build();

        songRepository.save(song);

        return song;
    }

    public Optional<Song> findById(long songId) {

        return songRepository.findById(songId);
    }

    @Transactional
    public void modify(Song song, String subject, String content) {

        song.setSubject(subject);
        song.setContent(content);
    }
}
