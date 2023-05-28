package com.assessment.edts.controller;

import com.assessment.edts.model.services.*;
import com.assessment.edts.repository.ConcertRepository;
import com.assessment.edts.repository.ConcertTicketRepository;
import com.assessment.edts.repository.UserTicketRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest (classes = BookingController.class)
@AutoConfigureMockMvc
@EnableJpaRepositories
@ComponentScan(basePackages = { "com.assessment.edts.*" })
@EntityScan("com.assessment.edts.*")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTicketRepository userTicketRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertTicketRepository concertTicketRepository;

    @Test
    @Order(1)
    void testSearchFound() throws Exception {
        SearchRequest request = new SearchRequest();
        request.setSearchValue("a");

        mockMvc.perform(
                post("/services/api/searchConcert")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andDo(result -> {
            ApiResponse<SearchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ApiResponse<SearchResponse>>() {
            });
            assertNotNull(response.getData().getConcertList().get(0));
        });
    }

    @Test
    @Order(2)
    void testSearchNotFound() throws Exception {
        SearchRequest request = new SearchRequest();
        request.setSearchValue("Coldplay");

        mockMvc.perform(
                post("/services/api/searchConcert")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andDo(result -> {
            ApiResponse<SearchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ApiResponse<SearchResponse>>() {
            });
            assertEquals(0, response.getData().getConcertList().size());
        });
    }

    @Test
    @Order(3)
    void testBookSuccess() throws Exception {
        BookedRequest request1 = new BookedRequest(1l,1l,1l,1);
        BookedRequest request2 = new BookedRequest(2l,1l,2l,1);
        List<BookedRequest> request = new ArrayList<>();
        Collections.addAll(request = new ArrayList<BookedRequest>(), request1, request2);

        mockMvc.perform(
                post("/services/api/bookConcert")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andDo(result -> {
            ApiResponse<List<BookedResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ApiResponse<List<BookedResponse>>>() {
            });
            assertEquals(1l, response.getData().get(0).getBookId());
            assertEquals("booking successful", response.getData().get(0).getResult());
            assertEquals(2l, response.getData().get(1).getBookId());
            assertEquals("booking successful", response.getData().get(1).getResult());
        });
    }

    @Test
    @Order(4)
    void testBookFailed() throws Exception {
        BookedRequest request1 = new BookedRequest(2l,1l,1l,1);
        BookedRequest request2 = new BookedRequest(3l,1l,2l,3);
        List<BookedRequest> request = new ArrayList<>();
        Collections.addAll(request = new ArrayList<BookedRequest>(), request1, request2);

        mockMvc.perform(
                post("/services/api/bookConcert")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andDo(result -> {
            ApiResponse<List<BookedResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ApiResponse<List<BookedResponse>>>() {
            });
            assertEquals(2l, response.getData().get(0).getBookId());
            assertEquals("full booked", response.getData().get(0).getResult());
            assertEquals(3l, response.getData().get(1).getBookId());
            assertEquals("balance insufficient", response.getData().get(1).getResult());
        });
    }

}