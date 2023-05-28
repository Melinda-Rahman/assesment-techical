package com.assessment.edts.model.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="concert_ticket")
public class ConcertTicket {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column (name = "ticket_code")
    private String ticketCode;

    @ManyToOne
    @JoinColumn(name = "concert_id", referencedColumnName = "id")
    private Concert concertId ;

    @Column (name = "is_booked")
    private Long isBooked;

    @OneToOne(mappedBy = "concertTicketId")
    private UserTicket userTickets;
}
