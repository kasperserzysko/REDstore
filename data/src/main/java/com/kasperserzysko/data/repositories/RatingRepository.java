package com.kasperserzysko.data.repositories;

import com.kasperserzysko.data.models.GameRating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<GameRating, Long> {

    @Query("SELECT COALESCE(AVG(gr.rating),0) FROM GameRating gr WHERE gr.game.id = :gameId")
    float getRatingAvg(Long gameId);

    Optional<GameRating> findByUser_IdAndGame_Id(Long userId, Long gameId);

    @Query("SELECT gr FROM GameRating gr WHERE gr.comment IS NOT NULL AND gr.game.id = :gameId ORDER BY gr.date DESC")
    List<GameRating> getRatings(Long gameId, Pageable pageable);
}
