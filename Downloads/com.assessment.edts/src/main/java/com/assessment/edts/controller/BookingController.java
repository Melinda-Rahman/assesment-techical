package com.assessment.edts.controller;

import com.assessment.edts.model.database.Concert;
import com.assessment.edts.model.services.*;
import com.assessment.edts.openapi.BookingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/services")
public class BookingController {

    @Autowired
    private BookingServices bookingServices;

    @RequestMapping(
            path = "/api/searchConcert",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiResponse<SearchResponse> search(@RequestBody SearchRequest request) {
        SearchResponse searchResponse = new SearchResponse();
        List<Concert> response = bookingServices.searchConcert(request);
        searchResponse.setConcertList(response);
        return ApiResponse.<SearchResponse>builder().data(searchResponse).build();
    }

    @RequestMapping(
            path = "/api/bookConcert",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ApiResponse<List<BookedResponse>> book(@RequestBody List<BookedRequest> request) {
        List<BookedResponse> response = bookingServices.bookedConcert(request);
        return ApiResponse.<List<BookedResponse>>builder().data(response).build();
    }
}
