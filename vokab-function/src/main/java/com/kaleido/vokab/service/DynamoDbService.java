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

import java.util.HashMap;
import java.util.Map;

public class DynamoDbService {
    private DynamoDBMapper mapper;
    private AmazonDynamoDB dynamoDB;
    private AmazonDynamoDB localDynamoDB;
    public final static String CONCEPT_TABLE_NAME = "vokab-concept";
    public final static String ALIAS_TABLE_NAME = "vokab-alias";

    protected AmazonDynamoDB getDynamoDB(){
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
    protected AmazonDynamoDB getLocalDynamoDB(){
        if (localDynamoDB == null) {
            final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
                        .withEndpointConfiguration(
                                new AwsClientBuilder.EndpointConfiguration(
                                        "http://localhost:8000", "us-east-1"));
            this.localDynamoDB = (builder.build());
        }

        return localDynamoDB;
    }

    protected void setLocalDynamoDB(AmazonDynamoDB localDynamoDB){
        this.localDynamoDB = localDynamoDB;
    }

    protected void setDynamoDB(AmazonDynamoDB dynamoDB){
        this.dynamoDB = dynamoDB;
    }

    protected DynamoDBMapper getMapper() {
        if(mapper == null) {
            mapper = new DynamoDBMapper(getDynamoDB());
        }

        return mapper;
    }

    public void setMapper(DynamoDBMapper mapper){
        this.mapper = mapper;
    }

    public void write(Alias alias){
        getMapper().save(alias);
    }

    public void write(Concept concept){
        getMapper().save(concept);
    }

    public void delete(Alias alias){
        getMapper().delete(alias);
    }

    public void delete(Concept concept){
        getMapper().delete(concept);
    }

    public PaginatedScanList<Alias> aliases(){
        return getMapper().scan(Alias.class, new DynamoDBScanExpression());
    }

    public PaginatedScanList<Alias> findAliases(String alias){
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(alias));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("alias = :val1").withExpressionAttributeValues(eav);

        return getMapper().scan(Alias.class, scanExpression);
    }

    public Alias findAlias( String alias, String conceptId){
        return getMapper().load(Alias.of(alias, conceptId));
    }

    public PaginatedScanList<Concept> concepts(){
        return getMapper().scan(Concept.class, new DynamoDBScanExpression());
    }

    public Concept findConcept(String uuid){

        return getMapper().load(Concept.of(uuid));
    }
}
