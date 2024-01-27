package com.toptalproject.quiz;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

public class PrintRequestAndResponseResultHandler implements ResultHandler {

    @Override
    public void handle(MvcResult result) throws Exception {
        System.out.println("Request Body: " + result.getRequest().getContentAsString());
        System.out.println("Response Body: " + result.getResponse().getContentAsString());
    }

    public static PrintRequestAndResponseResultHandler printRequestAndResponse() {
        return new PrintRequestAndResponseResultHandler();
    }
}