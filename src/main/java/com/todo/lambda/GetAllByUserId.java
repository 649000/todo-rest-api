package com.todo.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
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
import java.util.List;
import java.util.Map;

public class GetAllByUserId implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private Regions REGION = Regions.AP_SOUTHEAST_1;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Gson gson = new Gson();
        LambdaLogger logger = context.getLogger();
        logger.log("APIGatewayProxyRequestEvent::" + request.toString());

        DynamoDBMapper mapper = getDynamoDB();


        Map<String, Object> claims = (Map<String, Object>) request.getRequestContext().getAuthorizer().get("claims");

        DynamoDBScanExpression scanExp = new DynamoDBScanExpression();
        scanExp.addFilterCondition("cognito_sub",
                new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(claims.get("sub").toString())));



        List<ToDo> results = mapper.scan(ToDo.class, scanExp);

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(HttpStatus.SC_OK);
        responseEvent.setBody(gson.toJson(results));
        HashMap<String, String> header = new HashMap<>();
        header.put(HttpHeaders.CONTENT_TYPE,"application/json");
        responseEvent.setHeaders(header);
        return responseEvent;
    }

    private DynamoDBMapper getDynamoDB() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        return new DynamoDBMapper(client);
    }
}
