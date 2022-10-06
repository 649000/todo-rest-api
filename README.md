# Serverless REST API using AWS Cloud Development Kit (CDK) and Java


The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Useful commands

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

## AWS Services 
1. AWS API Gateway
2. AWS Lambda
3. AWS Cognito User Pool
4. AWS DynamoDB

## Project Status

|Feature|Status  |
|--|--|
|CRUD on models|Completed  |
|User authentication via Firebase Auth|Completed  |
|Display results in JSON API| Completed
| Deployed on Heroku| Completed

## Notes on the API Gateway
The API gateway defined in this CDK project is using AWS REST API (not AWS HTTP API). 
Differences on these API Gateways are available here:
https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-vs-rest.html

### Securing the API Gateway

1. Endpoint is secured using AWS Cognito User Pool
2. By default, ID token is used as part of Authentication bearer token. 
3. Access token can be used however it requires a custom client scope set in Cognito

For Testing Purpose
1. Client Credentials flow is set up to easily retrieve token
2. This requires custom client scope hence, access token is used

Reference: https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-enable-cognito-user-pool.html



