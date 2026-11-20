package com.example.demo.repository;

import com.example.demo.entity.VocabWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabWordRepository extends JpaRepository<VocabWord, Long> {

    List<VocabWord> findByVocabSetId(Long vocabSetId);

    List<VocabWord> findByVocabSetIdOrderByIdAsc(Long vocabSetId);

    @Query("SELECT w FROM VocabWord w WHERE w.vocabSet.id = :setId AND w.isStarred = true")
    List<VocabWord> findStarredBySetId(@Param("setId") Long setId);

    @Query("SELECT w FROM VocabWord w WHERE " +
           "LOWER(w.word) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(w.meaning) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<VocabWord> searchWords(@Param("query") String query);

    @Query("SELECT COUNT(w) FROM VocabWord w WHERE w.vocabSet.id = :setId")
    long countByVocabSetId(@Param("setId") Long setId);

    @Query("SELECT COUNT(w) FROM VocabWord w WHERE w.vocabSet.id = :setId AND w.masteryLevel = 2")
    long countMasteredByVocabSetId(@Param("setId") Long setId);
}
