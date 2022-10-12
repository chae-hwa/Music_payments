package com.ll.com.music_payments.app.song.repository;

import com.ll.com.music_payments.app.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
}
