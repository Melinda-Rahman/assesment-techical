package com.assessment.edts.repository;

import com.assessment.edts.model.database.ConcertTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ConcertTicketRepository extends JpaRepository<ConcertTicket, Long> {
    @Query(value = "SELECT * " +
            "FROM concert_ticket " +
            "WHERE is_booked = 0 AND concert_id = ?1 " +
            "ORDER BY ticket_code DESC Limit 0, ?2 LOCK IN SHARE MODE",
            nativeQuery = true)
    List<ConcertTicket> getTicketNumber(Long concertId, Integer quantity);

    @Modifying
    @Query(value = "UPDATE concert_ticket u set u.is_booked = 1 where id IN ?1",
            nativeQuery = true)
    int updateBookedTicket(Collection<Long> ticketId);

    @Modifying
    @Query(value = "UPDATE concert_ticket u set u.is_booked = ?1",
            nativeQuery = true)
    int setAllIsBooked(Integer ticketId);

}