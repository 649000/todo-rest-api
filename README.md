# Serverless To Do REST API

This project is a serverless REST API for a To Do App. It is built using AWS Cloud Development Kit (CDK) with the following AWS services:

1. AWS REST API Gateway (not the HTTP API Gateway)
2. AWS Lambda
3. AWS Cognito User Pool
4. AWS DynamoDB

## Project Status

|Feature|Status  |
|--|--|
|CRUD on models|Completed  |
|Endpoint secured via Cognito|Completed  |
|Display results in JSON API| Completed
| Deployed on AWS| Completed


## Useful commands
This project does not run locally, it requires CDK CLI to deploy it onto AWS.

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation


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

## Reflection
Cloud computing is ubiquitous, and AWS is one of the leaders in this space. Some firms are using serverless approach to cut out complexity in handling their technology pipeline. As a developer, it is important to keep myself updated with what's happening in the space. 

Developing a serverless application is a completely different paradigm. One has to think in terms of services when it comes to application design. Building it out on the cloud also requires a certain level of knowledge of the services these cloud computing providers have.

These lambda functions were written in vanilla Java. Some of these functions are tedious and verbose. As a Spring framework developer, I appreciate what the Spring framework does by creating common classes or simplifying common patterns through the use of annotations.

The other challenge faced was that Java is not a popular language when it comes to lambda and AWS CDK. Finding resources or guides was challenging but the AWS guides were well-written and sufficient. 

Reason for choosing AWS amongst other cloud providers was to aid my learning for my AWS certifications. The use of CDK is pretty awesome for lack of a better word. It is similar to Terraform or other Code as a Infrastructure product. I created an entirely serverless REST API with authentication using lines of code. CDK is based on AWS Cloudformation. 

#### Tools used 
1. Postman  - to test the endpoints
2. Intellij IDE
