/*
 * Copyright (c) 2019. Kaleido Biosciences. All Rights Reserved.
 */

package com.kaleido.vokab.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

import java.util.List;
import java.util.Map;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL;
import static com.kaleido.vokab.service.DynamoDbService.CONCEPT_TABLE_NAME;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@Builder @AllArgsConstructor(staticName = "of")
@DynamoDBTable(tableName = CONCEPT_TABLE_NAME)
public class Concept {

    @NonNull
    @DynamoDBHashKey(attributeName = "uuid")
    private String uuid;

    @DynamoDBAttribute(attributeName = "label")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "scheme-label-index")
    private String label;

    @DynamoDBAttribute(attributeName = "definition")
    private String definition;

    @DynamoDBAttribute(attributeName = "scheme")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "scheme-label-index")
    private String scheme;

    @DynamoDBAttribute(attributeName = "retired")
    @DynamoDBTyped(BOOL)
    private boolean retired;

    @DynamoDBAttribute(attributeName = "relationships")
    private Map<String, List<String>> relationships;

    @DynamoDBVersionAttribute(attributeName = "version")
    private Long version;
}