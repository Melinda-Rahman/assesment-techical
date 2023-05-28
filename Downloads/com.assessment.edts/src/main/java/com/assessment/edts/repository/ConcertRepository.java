package com.assessment.edts.repository;

import com.assessment.edts.model.database.Concert;
import com.assessment.edts.model.database.UserTicket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, Long> {

    List<Concert> findByConcertNameContainingAndIsFull(String concertName, Long isFull);

    Concert findByIdAndIsFull(Long id, Long isFull);

    @Modifying
    @Query(value = "UPDATE concert u SET u.is_full = 1 WHERE quantity = (SELECT SUM(is_booked) FROM concert_ticket WHERE concert_id = u.id)",
            nativeQuery = true)
    int updateFullBookedConcert();

    @Modifying
    @Query("update Concert u set u.isFull = ?1")
    void setAllIsFullBooked(Long concert);
}