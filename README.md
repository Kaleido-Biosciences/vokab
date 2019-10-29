# VOKAB
Vokab is a simple vocabulary (code table) service that lets you store preferred terms and synonyms for various domains.
It is built to be serverless and highly elastic so it can be used as a robust and scalable source of controlled vocabulary
for other services and databases.

## Motivation
To ensure data consistency it is useful to be able to constrain the values that people enter to a controlled vocabulary of
standard terms and to be able to look up those standards using common synonyms. This is simple to implement in a relational
database using code tables and foreign keys but in a world where you have more than one database or in a microservice environment
keeping everything consistent requires the use of a central service. Vokab provides a REST API to a highly scalable service
that can be provide values used by data entry UIs or data processing pipelines.

## Concepts & Synonyms
Concepts and Synonyms are the two objects that Vokab uses to represent a vocabulary. A Concept represents an official
term and a Synonym is an alternative or commonly used label which, for the purposes of the vocabulary, has the same meaning.

### Concept
A Concept (represented by `com.kaleido.vokab.domain.Concept`) has, minimally, a `uuid`, a `label` which is the actual term,
and a `scheme`. A Concept may also optionally have a `definition` a boolean `retired` to indicate if the term is still in
use or has been replaced, `relationships` and a `version`.

#### Scheme

#### Relationships

#### Version

### Alias

## Tech and Framework
* Vokab follows the AWS Severless Application Model (SAM). 
* At it's core is a Lambda Function that handles REST requests
and responses. REST calls are made to an Amazon API Gateway endpoint which delegates those calls to the Lamdba function.
* The Gateway endpoint is authorized by a Cognito pool (which you will need to set up to fit your needs). The pool client
will determine if the call to the Lambda function is authorized. 
* Terms and synonyms are serialized to DynamoDB.


## Requirements

* AWS CLI already configured with Administrator permission
* [Java SE Development Kit 8 installed](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Docker installed](https://www.docker.com/community-edition)
* [Maven](https://maven.apache.org/install.html)

## Setup process

### Installing dependencies

```bash
sam build
```

You can also build on a Lambda like environment by using

```bash
sam build --use-container
```

### Local development

**Invoking function locally through local API Gateway**

```bash
sam local start-api
```

If the previous command ran successfully you should now be able to hit the following local endpoint to invoke your function `http://localhost:3000/health`.

#### Local DynamoDB 
If you want to test creating and querying terms locally you can use the [DynamoDBLocal distribution](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
The bash script `vokab-function/create-tables.sh` can be used to create tables in DynamoDBLocal that will be required.

#### Template ####
Copy the `reference-template.yaml` file to `template.yaml` and make any desired adjustments to parameters, particularly 
paying attention to those with `#Replace` comments. The `template.yaml` file is the default input file for SAM that describes
the cloud infrastructure. Git is configured through `.gitignore` to not commit this file so you can add your own values
without worrying about them being pushed to the repo when you push.

**SAM CLI** is used to emulate both Lambda and API Gateway locally and uses our `template.yaml` to understand how to 
bootstrap this environment (runtime, where the source code is, etc.) - The following excerpt is what the CLI will 
read in order to initialize an API and its routes:

```yaml
...
      Events:
        Vokab:
          Type: Api # More info about API Event Source: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#api
          Properties:
            Path: /{vokab+} # intercept all path calls to vokab
            Method: ANY # Any HTTP method
```

## Packaging and deployment

AWS Lambda Java runtime accepts either a zip file or a standalone JAR file - We use the latter in this example. 
SAM will use `CodeUri` property to know where to look up for both application and dependencies:

```yaml
...
    Type: AWS::Serverless::Function # More info about Function Resource: https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md#awsserverlessfunction
    Properties:
      CodeUri: vokab-function
      Handler: com.kaleido.vokab.VokabHandler::handleRequest
```

Firstly, we need a `S3 bucket` where we can upload our Lambda functions packaged as ZIP before we deploy 
anything - If you don't have a S3 bucket to store code artifacts then this is a good time to create one:

```bash
aws s3 mb s3://BUCKET_NAME
```

Next, run the following command to package our Lambda function to S3:

```bash
sam package \
    --output-template-file packaged.yaml \
    --s3-bucket REPLACE_THIS_WITH_YOUR_S3_BUCKET_NAME
```

Next, the following command will create a Cloudformation Stack and deploy your SAM resources.

```bash
sam deploy \
    --template-file packaged.yaml \
    --stack-name vokab \
    --capabilities CAPABILITY_IAM
```

After deployment is complete you can run the following command to retrieve the API Gateway Endpoint URL:

```bash
aws cloudformation describe-stacks \
    --stack-name vokab \
    --query 'Stacks[].Outputs'
```

## Testing

We use `JUnit` for testing our code and you can simply run the following command to run our tests:

```bash
cd vokab-function
mvn test
```

# Appendix

## AWS CLI commands

AWS CLI commands to package, deploy and describe outputs defined within the cloudformation stack:

```bash
sam package \
    --template-file template.yaml \
    --output-template-file packaged.yaml \
    --s3-bucket REPLACE_THIS_WITH_YOUR_S3_BUCKET_NAME

sam deploy \
    --template-file packaged.yaml \
    --stack-name aws \
    --capabilities CAPABILITY_IAM \
    --parameter-overrides MyParameterSample=MySampleValue

aws cloudformation describe-stacks \
    --stack-name aws --query 'Stacks[].Outputs'
```

