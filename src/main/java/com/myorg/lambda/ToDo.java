package com.myorg.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;

import java.util.HashMap;

public class ToDo implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent apiGatewayV2HTTPEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log(apiGatewayV2HTTPEvent.toString());
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "text/html");
        response.setHeaders(headers);
        response.setBody("<!DOCTYPE html><html><head><title>AWS Lambda sample</title></head><body>"+
                "<h1>Welcome</h1><p>Page generated by a Lambda function.</p>" +
                "</body></html>");
        // log execution details
        return response;
    }
}
