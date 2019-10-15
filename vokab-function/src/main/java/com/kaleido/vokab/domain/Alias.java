/*
 * Copyright (c) 2019. Kaleido Biosciences. All Rights Reserved.
 */

package com.kaleido.vokab.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL;
import static com.kaleido.vokab.service.DynamoDbService.ALIAS_TABLE_NAME;

@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@Builder @AllArgsConstructor(staticName = "of")
@DynamoDBTable(tableName = ALIAS_TABLE_NAME)
public class Alias {

    @NonNull
    @DynamoDBHashKey(attributeName = "alias")
    private String alias;

    @NonNull
    @DynamoDBRangeKey(attributeName = "conceptId")
    private String conceptId;

    @DynamoDBAttribute(attributeName = "retired")
    @DynamoDBTyped(BOOL)
    private boolean retired;

    @DynamoDBVersionAttribute(attributeName = "version")
    private Long version;
}
