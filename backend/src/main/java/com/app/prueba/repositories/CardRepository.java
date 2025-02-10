package com.app.prueba.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.prueba.models.Cards;

@Repository
public interface CardRepository extends JpaRepository<Cards, Integer> {

    @Query(value = "SELECT c.* FROM cards c JOIN user_cards uc ON c.id = uc.card_id WHERE uc.user_id = :userId", nativeQuery = true)
    public List<Cards> findCardsByUserId(int userId);

}
