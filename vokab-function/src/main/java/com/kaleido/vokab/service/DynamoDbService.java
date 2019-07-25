package com.kaleido.vokab.service;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.kaleido.vokab.domain.Alias;
import com.kaleido.vokab.domain.Concept;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DynamoDbService {
    private DynamoDBMapper mapper;
    private AmazonDynamoDB dynamoDB;
    private AmazonDynamoDB localDynamoDB;
    public final static String CONCEPT_TABLE_NAME = "vokab-concept";
    public final static String ALIAS_TABLE_NAME = "vokab-alias";

    public AmazonDynamoDB getDynamoDB(){
        if(dynamoDB == null){
            final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
            dynamoDB = builder.build();
        }
        return dynamoDB;
    }

    /**
     * Gets a dynamoDb client for a dynamoDB instance running at http://localhost:8000, used for testing
     * @return the client for a local dynamo instance.
     */
    public AmazonDynamoDB getLocalDynamoDB(){
        if (localDynamoDB == null) {
            final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
                        .withEndpointConfiguration(
                                new AwsClientBuilder.EndpointConfiguration(
                                        "http://localhost:8000", "us-east-1"));
            this.localDynamoDB = (builder.build());

            dynamoDB.listTables().getTableNames();
        }

        return localDynamoDB;
    }

}
