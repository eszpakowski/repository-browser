package com.interview.repositorybrowser.adapter.outbound.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class BranchDTO {
    @JsonProperty("name")
    private String name;

    private String url;

    private String commitSha;

    @JsonProperty("commit")
    private void commitSha(Map<String, String> commit) {
        commitSha = commit.get("sha");
        url = commit.get("url");
    }
}
