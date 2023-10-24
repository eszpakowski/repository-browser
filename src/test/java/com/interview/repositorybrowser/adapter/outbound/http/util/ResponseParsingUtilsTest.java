package com.interview.repositorybrowser.adapter.outbound.http.util;

import com.interview.repositorybrowser.domain.exception.BadRequestException;
import com.interview.repositorybrowser.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ResponseParsingUtilsTest {

    @Test
    void shouldValidateResponseCode200() {
        HttpResponse<String> response = mockResponse(200);

        ResponseValidationUtils.validateResponseCode(response);
    }

    @Test
    void shouldValidateResponseCode400() {
        HttpResponse<String> response = mockResponse(400);

        assertThrows(BadRequestException.class,
                () -> ResponseValidationUtils.validateResponseCode(response));
    }

    @Test
    void shouldValidateResponseCode422() {
        HttpResponse<String> response = mockResponse(422);

        assertThrows(UserNotFoundException.class,
                () -> ResponseValidationUtils.validateResponseCode(response));
    }

    private static HttpResponse<String> mockResponse(int responseCode) {
        HttpResponse<String> response = mock(HttpResponse.class);
        given(response.statusCode()).willReturn(responseCode);
        return response;
    }
}