package com.assessment.edts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.assessment.edts.model.database.UserTicket;

@Repository
public interface UserTicketRepository extends JpaRepository<UserTicket, Long> {

}