package com.myorg.function;

import com.amazonaws.services.lambda.runtime.Context;
import software.amazon.awscdk.services.apigateway.GatewayResponse;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

public class CreateItem implements RequestHandler<Map<String,Object>, GatewayResponse>{
    @Override
    public GatewayResponse handleRequest(Map<String, Object> stringObjectMap, Context context) {
        return null;
    }
}
