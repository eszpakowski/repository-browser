package com.interview.repositorybrowser.adapter.outbound.http.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class RepositoryDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("html_url")
    private String url;

    private String ownerLogin;

    @JsonProperty("owner")
    private void ownerLogin(Map<String, String> owner) {
        ownerLogin = owner.get("login");
    }
}
