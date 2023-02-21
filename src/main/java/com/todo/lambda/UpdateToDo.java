package com.todo.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
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

public class UpdateToDo implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private Regions REGION = Regions.AP_SOUTHEAST_1;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Gson gson = new Gson();
        LambdaLogger logger = context.getLogger();
        logger.log("APIGatewayProxyRequestEvent::" + request.toString());

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        DynamoDBMapper mapper = new DynamoDBMapper(client);

        final String id = request.getPathParameters().get("id");
        ToDo requestToDo = gson.fromJson(request.getBody(), ToDo.class);

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();

        try {
            ToDo toDo = mapper.load(ToDo.class, id);
            toDo.setDescription(requestToDo.getDescription());
            toDo.setPriority(requestToDo.getPriority());

            mapper.save(toDo,
                    DynamoDBMapperConfig.builder()
                            .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES)
                            .build());
            responseEvent.setStatusCode(HttpStatus.SC_OK);

            ToDo newToDo = mapper.load(ToDo.class, id);
            responseEvent.setBody(gson.toJson(newToDo));
        } catch (Exception e) {
            logger.log(e.getMessage());
            responseEvent.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }

        //Setting headers
        HashMap<String, String> header = new HashMap<>();
        header.put(HttpHeaders.CONTENT_TYPE, "application/json");
        responseEvent.setHeaders(header);

        return responseEvent;
    }
}
