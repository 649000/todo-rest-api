package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Function;

import java.util.HashMap;
import java.util.Map;

public class AwsCdkLambdaRestApiStack extends Stack {
    public AwsCdkLambdaRestApiStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsCdkLambdaRestApiStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here
        //DynamoDB
        Attribute partitionKey = Attribute.builder()
                .name("id")
                .type(AttributeType.STRING)
                .build();

        TableProps tableProps = TableProps.builder()
                .tableName("ToDo")
                .partitionKey(partitionKey)
                // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
                // the new table, and it will remain in your account until manually deleted. By setting the policy to
                // DESTROY, cdk destroy will delete the table (even if it has data in it)
//                .removalPolicy(RemovalPolicy.DESTROY)
                .readCapacity(1)
                .writeCapacity(1)
                .billingMode(BillingMode.PROVISIONED)
                .build();

        Table dynamodbTable = new Table(this, "ToDoTable", tableProps);

        //Lambda
        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", dynamodbTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY","id");

        Function createToDoFunction = new Function(this, "createToDoItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.CreateToDo"));

        Function getAllToDoFunction = new Function(this, "getAllToDoItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.GetAllToDo"));

        Function getOneToDoFunction = new Function(this, "getToDoItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.GetOneToDo"));

        Function updateToDoFunction = new Function(this, "updateToDoFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.CreateToDo"));

        Function deleteToDoFunction = new Function(this, "deleteToDoFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.DeleteToDo"));

        dynamodbTable.grantReadWriteData(createToDoFunction);
        dynamodbTable.grantReadWriteData(getAllToDoFunction);
        dynamodbTable.grantReadWriteData(getOneToDoFunction);
        dynamodbTable.grantReadWriteData(updateToDoFunction);
        dynamodbTable.grantReadWriteData(deleteToDoFunction);

//        // Defines an API Gateway REST API resource backed by our "hello" function
//        final Function hello = Function.Builder.create(this, "HelloHandler")
//                .runtime(Runtime.JAVA_11)
//                .code(Code.fromAsset("./target/aws-cdk-lambda-rest-api-0.1.jar"))
//                .handler("com.myorg.lambda.HelloWorld")
//                .build();

        // Defines an API Gateway REST API resource
        LambdaRestApi api = LambdaRestApi.Builder.create(this, "ToDo API Gateway")
                .handler(getAllToDoFunction)
                //Proxy: false, require explicit definition of API model
//                .proxy(false)
                .build();

        //Set resource path: https://api-gateway/todo
        Resource todo = api.getRoot().addResource("todo");
        // HTTP GET /todo
        todo.addMethod("GET", new LambdaIntegration(getAllToDoFunction));
        // HTTP POST /todo
        todo.addMethod("POST", new LambdaIntegration(createToDoFunction));

        //Set {ID} path: https://api-gateway/todo/{ID}
        Resource todoId = todo.addResource("{id}");
        todoId.addMethod("GET", new LambdaIntegration(getOneToDoFunction));
        todoId.addMethod("DELETE", new LambdaIntegration(deleteToDoFunction));
        todoId.addMethod("POST", new LambdaIntegration(updateToDoFunction));


    }
    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler) {
        return FunctionProps.builder()
//                .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                .code(Code.fromAsset("./target/aws-cdk-lambda-rest-api-0.1.jar"))
                .handler(handler)
                .runtime(Runtime.JAVA_11)
                .environment(lambdaEnvMap)
                .timeout(Duration.seconds(30))
                .memorySize(512)
                .build();
    }
}
