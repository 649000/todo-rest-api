# Serverless To-Do App API

## Overview

This project encapsulates a serverless REST API catering to a To-Do application. Constructed using the AWS Cloud Development Kit (CDK), it seamlessly integrates an array of AWS services to power its functionality.

## AWS CDK

AWS CDK stands as an open-source software development framework, empowering the definition of cloud infrastructure as code using modern programming languages. It facilitates the deployment of infrastructure through AWS CloudFormation, enhancing the efficiency and reproducibility of deployment processes.

This project harnesses the power of AWS CDK to orchestrate a scalable serverless infrastructure, enabling seamless management and deployment of the To-Do App's backend components.

## AWS Services Utilized
* AWS REST API Gateway: Facilitating a robust interface for API interactions.
* AWS Lambda: Enabling serverless function execution for scalable and on-demand computing.
* AWS Cognito User Pool: Offering secure user authentication and authorization capabilities.
* AWS DynamoDB: Providing a fully managed NoSQL database solution for efficient data storage.

## Project Status

|Feature|Status  |
|--|--|
|CRUD on models|Completed  |
|Endpoint secured via Cognito|Completed  |
|Display results in JSON | Completed
| Deployed on AWS| Completed


## Installation and Setup Instructions
This project does not run locally, it requires CDK CLI to deploy it onto AWS.
Guide on installing CDK CLI is [available here.](https://docs.aws.amazon.com/cdk/v2/guide/cli.html)

 * `mvn package`     compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation


## Notes on the API Gateway

### Securing the API Gateway

1. Endpoint is secured using AWS Cognito User Pool
2. By default, ID token is used as part of Authentication bearer token. 
3. Access token can be used however it requires a custom client scope set in Cognito

For Testing Purpose
1. Client Credentials flow is set up to easily retrieve token
2. This requires custom client scope hence, access token is used

Reference: https://docs.aws.amazon.com/apigateway/latest/developerguide/apigateway-enable-cognito-user-pool.html

## Reflection
In today's technology landscape, cloud computing has become pervasive, and AWS stands prominently as a frontrunner in this domain. Many organizations are embracing serverless approaches to streamline their tech infrastructure. As a developer, staying abreast of these advancements is crucial.

Venturing into the realm of serverless applications marks a paradigm shift in development. It demands a service-oriented mindset, wherein application design revolves around leveraging distinct cloud services. Crafting applications in the cloud necessitates a profound understanding of the diverse offerings provided by cloud computing providers.

While implementing lambda functions in vanilla Java, I found some functions to be verbose and laborious. As a developer well-versed in the Spring framework, I value its ability to simplify complex patterns and streamline repetitive tasks through intuitive annotations.

Navigating the landscape of lambda and AWS CDK with Java posed its challenges. Despite Java not being as prevalent in this domain, I found the AWS guides comprehensive, offering sufficient insights and guidance.

My decision to opt for AWS among various cloud providers was primarily driven by my pursuit of AWS certifications. The utilization of CDK was an exhilarating experience, simplifying infrastructure provisioning akin to products like Terraform. With just a few lines of code, I constructed a fully serverless REST API fortified with authentication capabilities. CDK, rooted in AWS CloudFormation, proved remarkably efficient and empowering for infrastructure-as-code development.
