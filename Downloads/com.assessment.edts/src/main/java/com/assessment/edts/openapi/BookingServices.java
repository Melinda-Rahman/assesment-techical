package com.assessment.edts.openapi;

import com.assessment.edts.model.database.Concert;
import com.assessment.edts.model.database.ConcertTicket;
import com.assessment.edts.model.database.User;
import com.assessment.edts.model.database.UserTicket;
import com.assessment.edts.model.services.BookedRequest;
import com.assessment.edts.model.services.BookedResponse;
import com.assessment.edts.model.services.SearchRequest;
import com.assessment.edts.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class BookingServices implements BookingApi {

    @Autowired
    private UserTicketRepository userTicketRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertTicketRepository concertTicketRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Concert> searchConcert(SearchRequest request) {
        return concertRepository.findByConcertNameContainingAndIsFull(request.getSearchValue(), 0l);
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public List<BookedResponse> bookedConcert(List<BookedRequest> request) {
        List<BookedResponse> response = new ArrayList<>();
        for (BookedRequest order : request) {
            BookedResponse response1 = new BookedResponse();
            response1.setBookId(order.getBookId());
            if (null != concertRepository.findByIdAndIsFull(order.getConcertId(),1l)) {
                response1.setResult("full booked");
            } else {
                List<ConcertTicket> ticketList = concertTicketRepository.getTicketNumber(order.getConcertId(), order.getQuantity());
                if (null == ticketList || (null != ticketList && ticketList.size() < order.getQuantity())) {
                    response1.setResult("balance insufficient");
                } else {
                    List<Long> ticketId = ticketList.stream().map(d -> d.getId()).collect(Collectors.toList());
                    concertTicketRepository.updateBookedTicket(ticketId);
                    List<UserTicket> bookedTicket = new ArrayList<>();
                    for (Long ticketItem : ticketId) {
                        UserTicket userTicket = new UserTicket();
                        User user = userRepository.findById(order.getUserId()).orElse(new User());
                        userTicket.setUserId(user);
                        ConcertTicket ticket = concertTicketRepository.findById(ticketItem).orElse(new ConcertTicket());
                        userTicket.setConcertTicketId(ticket);
                        userTicket.setBookId(order.getBookId());
                        bookedTicket.add(userTicket);
                    }
                    concertRepository.updateFullBookedConcert();
                    userTicketRepository.saveAll(bookedTicket);
                    response1.setResult("booking successful");
                }
            }
            response.add(response1);
        }
        return response;
    }


}
