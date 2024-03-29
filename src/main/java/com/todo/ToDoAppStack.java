package com.todo;

import com.amazonaws.HttpMethod;
import com.todo.lambda.*;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigateway.Resource;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.cognito.IUserPool;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.dynamodb.*;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.constructs.IConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoAppStack extends Stack {

    private List<IConstruct> constructList = new ArrayList<>();

    public ToDoAppStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ToDoAppStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Table dynamodbTable = createDynamoDB();

        // Setting up of lambda functions
        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", dynamodbTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY", "id");

        // Declaring of Lambda functions, handler name must be same as name of class
        Function createToDoFunction = new Function(this, CreateToDo.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, CreateToDo.class.getName(), "Create To Dos"));

        Function getAllToDoFunction = new Function(this, GetAllToDo.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, GetAllToDo.class.getName(), "Get All To Dos"));

        Function getAllByIdToDoFunction = new Function(this, GetAllByUserId.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, GetAllByUserId.class.getName(), "Get To Dos By UserId"));

        Function getOneToDoFunction = new Function(this, GetOneToDo.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, GetOneToDo.class.getName(), "Get One To Dos"));

        Function updateToDoFunction = new Function(this, UpdateToDo.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, UpdateToDo.class.getName(), "Update To Dos"));

        Function deleteToDoFunction = new Function(this, DeleteToDo.class.getSimpleName(),
                getLambdaFunctionProps(lambdaEnvMap, DeleteToDo.class.getName(), "Delete To Dos"));

        dynamodbTable.grantReadWriteData(createToDoFunction);
        dynamodbTable.grantReadWriteData(getAllToDoFunction);
        dynamodbTable.grantReadWriteData(getOneToDoFunction);
        dynamodbTable.grantReadWriteData(updateToDoFunction);
        dynamodbTable.grantReadWriteData(deleteToDoFunction);
        dynamodbTable.grantReadWriteData(getAllByIdToDoFunction);

        CognitoUserPoolsAuthorizer authorizer = createCognitoAuthorizer();

        LambdaRestApi api = createApiGateway(authorizer, getAllToDoFunction);

        //Set resource path: https://api-gateway/todo
        Resource todo = api.getRoot().addResource("todo");

        // HTTP GET https://api-gateway/todo
        //Endpoint is secured and requires token to call.
        todo.addMethod(HttpMethod.GET.name(), new LambdaIntegration(getAllByIdToDoFunction));

//        todo.addMethod(HttpMethod.GET.name(), new LambdaIntegration(getAllByIdToDoFunction), MethodOptions.builder()
//                .authorizationScopes())

        // HTTP POST https://api-gateway/todo
        todo.addMethod(HttpMethod.POST.name(), new LambdaIntegration(createToDoFunction));

        //Set {ID} path: https://api-gateway/todo/{ID}
        Resource todoId = todo.addResource("{id}");

        todoId.addMethod(HttpMethod.GET.name(), new LambdaIntegration(getOneToDoFunction));
        todoId.addMethod(HttpMethod.DELETE.name(), new LambdaIntegration(deleteToDoFunction));
        todoId.addMethod(HttpMethod.PATCH.name(), new LambdaIntegration(updateToDoFunction));

        constructList.add(dynamodbTable);
        constructList.add(api);
        constructList.add(authorizer);
        constructList.add(createToDoFunction);
        constructList.add(getAllToDoFunction);
        constructList.add(getOneToDoFunction);
        constructList.add(updateToDoFunction);
        constructList.add(deleteToDoFunction);
        addTags(constructList);
    }

    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler, String description) {
        return FunctionProps.builder()
                //Note: Use of Maven Shade plugin to include dependency JARs into final jar file
                .code(Code.fromAsset("./target/todo-api-0.1.jar"))
                .handler(handler)
                .runtime(Runtime.JAVA_11)
                .environment(lambdaEnvMap)
                .timeout(Duration.seconds(30))
                .memorySize(512)
                .description(description)
                .build();
    }

    /**
     * @return
     */
    private Table createDynamoDB() {
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

        return new Table(this, "ToDoTable", tableProps);
    }

    /**
     * @return
     */
    private CognitoUserPoolsAuthorizer createCognitoAuthorizer() {
        //Below code will create a new UserPool
        //UserPool userPool = new UserPool(this, "userPool");
        //However, we want to use an existing UserPool in our dev env
        IUserPool userPool = UserPool.fromUserPoolId(this, "dev", "ap-southeast-1_lkgFiXAec");

        //Declare a Cognito Authorizer to secure endpoint
        return CognitoUserPoolsAuthorizer.Builder
                .create(this, "ToDoCognitoAuthorizer")
                .cognitoUserPools(
                        List.of(userPool)
                )
                .authorizerName("todo-cognito-authorizer")
                .build();
    }

    /**
     * Requires ID Token to get access.
     * Access Token with Custom Scope can be used. Refer to:
     * https://stackoverflow.com/questions/50404761/aws-api-gateway-using-access-token-with-cognito-user-pool-authorizer
     * This custom-scope added is only available for token retrieved via
     *
     * @param authorizer
     * @param getAllToDoFunction
     * @return
     */
    private LambdaRestApi createApiGateway(CognitoUserPoolsAuthorizer authorizer, Function getAllToDoFunction) {
        return LambdaRestApi.Builder.create(this, "ToDoApiGateway")
                .restApiName("ToDo REST API Gateway")
                .description("REST API Gateway for To Do App")
                .deployOptions(
                        StageOptions.builder()
                                .stageName("dev")
                                .description("For Development Environment")
                                .build()
                )
                .defaultMethodOptions(
                        MethodOptions.builder()
                                .authorizer(authorizer)
                                .authorizationType(AuthorizationType.COGNITO)
//                                .authorizationScopes(List.of("https://google.com/custom-scope"))
                                .build()
                )
                .handler(getAllToDoFunction)
                .build();
    }

    private void addTags(List<IConstruct> constructList) {
        constructList.forEach(construct -> {
                    Tags.of(construct).add("project", "ToDoApp");
                    Tags.of(construct).add("environment", "dev");
                }
        );
    }


}
