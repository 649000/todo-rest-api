package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
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

        // example resource
        // final Queue queue = Queue.Builder.create(this, "AwsCdkLambdaRestApiQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();


        TableProps tableProps;

        Attribute partitionKey = Attribute.builder()
                .name("itemId")
                .type(AttributeType.STRING)
                .build();

        tableProps = TableProps.builder()
                .tableName("items")
                .partitionKey(partitionKey)
                // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
                // the new table, and it will remain in your account until manually deleted. By setting the policy to
                // DESTROY, cdk destroy will delete the table (even if it has data in it)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        Table dynamodbTable = new Table(this, "items", tableProps);

        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", dynamodbTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY","itemId");

        Function getOneItemFunction = new Function(this, "getOneItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GetOneItem"));

        Function getAllItemsFunction = new Function(this, "getAllItemsFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GetAllItems"));

        Function createItemFunction = new Function(this, "createItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.CreateItem"));

        Function updateItemFunction = new Function(this, "updateItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.UpdateItem"));

        Function deleteItemFunction = new Function(this, "deleteItemFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.DeleteItem"));
    }
    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler) {
        return FunctionProps.builder()
                .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                .handler(handler)
                .runtime(Runtime.JAVA_11)
                .environment(lambdaEnvMap)
                .timeout(Duration.seconds(30))
                .memorySize(512)
                .build();
    }
}
