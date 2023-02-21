package com.todo;

import com.amazonaws.HttpMethod;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.cognito.IUserPool;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoAppStack extends Stack {
    public ToDoAppStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ToDoAppStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        //DynamoDB Partition(Primary) Key
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
                .removalPolicy(RemovalPolicy.DESTROY)
//                .removalPolicy(RemovalPolicy.RETAIN)
                .readCapacity(1)
                .writeCapacity(1)
                .billingMode(BillingMode.PROVISIONED)
                .build();

        Table dynamodbTable = new Table(this, "ToDoTable", tableProps);

        // Setting up of lambda functions
        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", dynamodbTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY","id");

        // Declaring of Lambda functions, handler name must be same as name of class
        Function createToDoFunction = new Function(this, "createToDoItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.CreateToDo"));

        Function getAllToDoFunction = new Function(this, "getAllToDoItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.GetAllToDo"));

        Function getOneToDoFunction = new Function(this, "getToDoItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.GetOneToDo"));

        Function updateToDoFunction = new Function(this, "updateToDoFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.UpdateToDo"));

        Function deleteToDoFunction = new Function(this, "deleteToDoFunction",
                getLambdaFunctionProps(lambdaEnvMap, "com.myorg.lambda.DeleteToDo"));

        dynamodbTable.grantReadWriteData(createToDoFunction);
        dynamodbTable.grantReadWriteData(getAllToDoFunction);
        dynamodbTable.grantReadWriteData(getOneToDoFunction);
        dynamodbTable.grantReadWriteData(updateToDoFunction);
        dynamodbTable.grantReadWriteData(deleteToDoFunction);


        // Defines an API Gateway REST API resource
        LambdaRestApi api = LambdaRestApi.Builder.create(this, "ToDo API Gateway")
                .deployOptions(
                        StageOptions.builder()
                                .stageName("dev")
                                .description("For Development Environment")
                                .build()
                )
                .handler(getAllToDoFunction)
                //Proxy: false, require explicit definition of API model
//                .proxy(false)
                .build();


        //Below code will create a new UserPool
        //UserPool userPool = new UserPool(this, "userPool");

        //However, we want to use an existing dev UserPool
        IUserPool userPool = UserPool.fromUserPoolId(this, "devPool","ap-southeast-1_lkgFiXAec");

        //Declare a Cognito Authorizer to secure endpoint
        CognitoUserPoolsAuthorizer authorizer = CognitoUserPoolsAuthorizer.Builder
                .create(this,"cognito-authorizer")
                .cognitoUserPools(
                        List.of(userPool)
                )
                .authorizerName("cognito-authorizer")
                .build();

        //Set resource path: https://api-gateway/todo
        Resource todo = api.getRoot().addResource("todo");

        // HTTP GET /todo
        //Endpoint is secured and requires token to call.
        todo.addMethod(
                HttpMethod.GET.name(),
                new LambdaIntegration(getAllToDoFunction),
                MethodOptions.builder()
                        .authorizer(authorizer)
                        .authorizationType(AuthorizationType.COGNITO)
                        .build()
                );

        // HTTP POST /todo
        todo.addMethod(HttpMethod.POST.name(), new LambdaIntegration(createToDoFunction));

        //Set {ID} path: https://api-gateway/todo/{ID}
        Resource todoId = todo.addResource("{id}");

        todoId.addMethod(HttpMethod.GET.name(), new LambdaIntegration(getOneToDoFunction));
        todoId.addMethod(HttpMethod.DELETE.name(), new LambdaIntegration(deleteToDoFunction));
        todoId.addMethod(HttpMethod.PATCH.name(), new LambdaIntegration(updateToDoFunction));
    }

    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler) {
        return FunctionProps.builder()
                //Note: Use of Maven Shade plugin to include dependency JARs into final jar file
                .code(Code.fromAsset("./target/aws-cdk-lambda-rest-api-0.1.jar"))
                .handler(handler)
                .runtime(Runtime.JAVA_11)
                .environment(lambdaEnvMap)
                .timeout(Duration.seconds(30))
                .memorySize(512)
                .build();
    }
}
