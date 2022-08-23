package com.myorg;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

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

    }
}
