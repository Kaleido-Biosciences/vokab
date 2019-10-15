/*
 * Copyright (c) 2019. Kaleido Biosciences. All Rights Reserved.
 */

package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamoDbService {
    private AmazonDynamoDB dynamoDB;
    public final static String CONCEPT_TABLE_NAME = "vokab-concept";
    public final static String ALIAS_TABLE_NAME = "vokab-alias";

    public AmazonDynamoDB getDynamoDB(){
        if(dynamoDB == null){
            final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();
            dynamoDB = builder.build();
        }
        return dynamoDB;
    }

}
