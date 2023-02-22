package com.todo.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.todo.models.ToDo;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class CreateToDo implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    // APIGatewayV2HTTPEvent for HTTP API Gateway (Payload 2.0)
    // APIGatewayProxyRequestEvent for HTTP API Gateway (Payload 1.0)
    // APIGatewayProxyRequestEvent for REST API Gateway
    //Source: https://georgemao.medium.com/demystifying-java-aws-lambda-handlers-for-api-gateway-c1e77b7e6a8d
    private Regions REGION = Regions.AP_SOUTHEAST_1;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {

        LambdaLogger logger = context.getLogger();
        Gson gson = new Gson();
        logger.log("APIGatewayProxyRequestEvent::" + request.toString());

        //Initializing db settings
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        ToDo toDo = gson.fromJson(request.getBody(), ToDo.class);
        Map<String, Object> claims = (Map<String, Object>) request.getRequestContext().getAuthorizer().get("claims");
        toDo.setCognito_email(claims.get("email").toString());
        toDo.setCognito_sub(claims.get("sub").toString());
        toDo.setCognito_username(claims.get("cognito:username").toString());
        logger.log(toDo.toString());

        // Set expected false for an attribute -  Save only if did not exist
        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue();
        expectedAttributeValue.setExists(Boolean.FALSE);

        // Map the id field to the ExpectedAttributeValue
        Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<>();
        expectedAttributes.put("id", expectedAttributeValue);

        // Use the attributes within a DynamoDBSaveExpression
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        saveExpression.setExpected(expectedAttributes);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            // Save to dynamoDBMapper using the saveExpression
            mapper.save(toDo, saveExpression);
            response.setBody(gson.toJson(toDo));
            response.setStatusCode(HttpStatus.SC_CREATED);
        } catch (ConditionalCheckFailedException e){
            response.setStatusCode(HttpStatus.SC_CONFLICT);
        }

        //Setting headers
        HashMap<String, String> header = new HashMap<>();
        header.put(HttpHeaders.CONTENT_TYPE, "application/json");
        response.setHeaders(header);

        return response;
    }
}
