package com.assessment.edts.openapi;

import com.assessment.edts.model.database.Concert;
import com.assessment.edts.model.database.ConcertTicket;
import com.assessment.edts.model.database.User;
import com.assessment.edts.model.database.UserTicket;
import com.assessment.edts.model.services.BookedRequest;
import com.assessment.edts.model.services.BookedResponse;
import com.assessment.edts.model.services.SearchRequest;
import com.assessment.edts.repository.ConcertRepository;
import com.assessment.edts.repository.ConcertTicketRepository;
import com.assessment.edts.repository.UserRepository;
import com.assessment.edts.repository.UserTicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class BookingServicesTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertTicketRepository concertTicketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTicketRepository userTicketRepository;

    @Test
    public void contextLoads() {
    }

    @Test
    void searchTest() {
        SearchRequest request = new SearchRequest();
        request.setSearchValue("a");
        List<Concert> response = this.searchConcertTest(request);
        assertNotNull(response.size());
    }

    @Test
    void searchNotFoundTest() {
        SearchRequest request = new SearchRequest();
        request.setSearchValue("coldplay");
        List<Concert> response = this.searchConcertTest(request);
        assertEquals(0,response.size());
    }

    @Test
    @Transactional
    @Rollback
    void bookingSuccessTest() {
        BookedRequest request1 = new BookedRequest(1l,1l,1l,1);
        BookedRequest request2 = new BookedRequest(2l,1l,2l,1);
        List<BookedRequest> request = new ArrayList<>();
        Collections.addAll(request = new ArrayList<BookedRequest>(), request1, request2);
        List<BookedResponse> response = this.bookedConcert(request);
        assertEquals("booking successful",response.get(0).getResult());
        assertEquals("booking successful",response.get(1).getResult());
    }

    @Test
    @Transactional
    @Rollback
    void bookingFailedTest() {
        BookedRequest request1 = new BookedRequest(1l,1l,1l,5);
        BookedRequest request2 = new BookedRequest(3l,1l,2l,5);
        List<BookedRequest> request = new ArrayList<>();
        Collections.addAll(request = new ArrayList<BookedRequest>(), request1, request2);
        List<BookedResponse> response = this.bookedConcert(request);
        assertEquals("balance insufficient",response.get(0).getResult());
        assertEquals("balance insufficient",response.get(1).getResult());
    }

    public List<Concert> searchConcertTest(SearchRequest request) {
        return concertRepository.findByConcertNameContainingAndIsFull(request.getSearchValue(), 0l);
    }

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