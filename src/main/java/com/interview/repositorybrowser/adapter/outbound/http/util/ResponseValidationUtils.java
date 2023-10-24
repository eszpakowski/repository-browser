package com.interview.repositorybrowser.adapter.outbound.http.util;

import com.interview.repositorybrowser.domain.exception.BadRequestException;
import com.interview.repositorybrowser.domain.exception.UserNotFoundException;

import java.net.http.HttpResponse;

public class ResponseValidationUtils {
    private ResponseValidationUtils() {
    }

    public static void validateResponseCode(HttpResponse<String> httpResponse) {
        if (httpResponse.statusCode() == 200) {
            return;
        }

        switch (httpResponse.statusCode()) {
            case 400 -> throw new BadRequestException(httpResponse.body());
            case 422 -> throw new UserNotFoundException("The user does not exist!");
        }
    }
}
