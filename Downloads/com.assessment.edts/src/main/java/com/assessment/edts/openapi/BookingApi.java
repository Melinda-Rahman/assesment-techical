package com.assessment.edts.openapi;

import com.assessment.edts.model.database.Concert;
import com.assessment.edts.model.services.BookedRequest;
import com.assessment.edts.model.services.BookedResponse;
import com.assessment.edts.model.services.SearchRequest;

import java.util.List;

public interface BookingApi {

    public List<Concert> searchConcert(SearchRequest request);
    public List<BookedResponse> bookedConcert(List<BookedRequest> request);
}
