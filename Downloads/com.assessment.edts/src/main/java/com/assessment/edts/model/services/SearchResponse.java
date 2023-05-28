package com.assessment.edts.model.services;

import com.assessment.edts.model.database.Concert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SearchResponse {
    private List<Concert> concertList;
}
