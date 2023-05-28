package com.assessment.edts.model.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user_ticket")
public class UserTicket {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId ;

    @OneToOne
    @JoinColumn(name = "concert_ticket_id", referencedColumnName = "id")
    private ConcertTicket concertTicketId ;

    @Column (name = "book_id")
    private Long bookId;
}
