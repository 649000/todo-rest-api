package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class CdkWorkshopTutorialStack extends Stack {

    public CdkWorkshopTutorialStack(final Construct scope, final String id, final StackProps props) {
        //Tutorial from https://cdkworkshop.com/
        final Function hello = Function.Builder.create(this, "HelloHandler")
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("./target/aws-cdk-lambda-rest-api-0.1.jar"))
                .handler("com.myorg.function.HelloWorld")
                .build();

        // Defines an API Gateway REST API resource backed by our "hello" function
        LambdaRestApi.Builder.create(this, "Endpoint")
                .handler(hello)
                .build();
    }


}
