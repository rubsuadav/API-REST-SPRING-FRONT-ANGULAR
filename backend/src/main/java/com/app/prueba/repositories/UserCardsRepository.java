package com.app.prueba.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.prueba.models.UserCards;

@Repository
public interface UserCardsRepository extends JpaRepository<UserCards, Integer> {

}
