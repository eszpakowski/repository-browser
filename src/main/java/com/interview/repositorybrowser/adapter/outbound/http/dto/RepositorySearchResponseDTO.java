package com.interview.repositorybrowser.adapter.outbound.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RepositorySearchResponseDTO {
    @JsonProperty("total_count")
    private int totalCount;

    @JsonProperty("incomplete_results")
    private boolean incomplete;

    @JsonProperty("items")
    private List<RepositoryDTO> repositories;
}
