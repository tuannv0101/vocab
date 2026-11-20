package com.example.demo.repository;

import com.example.demo.entity.VocabSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabSetRepository extends JpaRepository<VocabSet, Long> {
    
    @Query("SELECT s FROM VocabSet s LEFT JOIN FETCH s.words ORDER BY s.id DESC")
    List<VocabSet> findAllWithWords();
}
