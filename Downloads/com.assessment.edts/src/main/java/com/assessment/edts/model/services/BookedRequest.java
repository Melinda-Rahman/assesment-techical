package com.assessment.edts.model.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BookedRequest {
    private Long bookId;
    private Long userId;
    private Long concertId;
    private Integer quantity;
}
