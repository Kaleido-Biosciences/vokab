package com.kaleido.vokab.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

import java.util.List;
import java.util.Map;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL;
import static com.kaleido.vokab.service.DynamoDbService.CONCEPT_TABLE_NAME;
import static lombok.AccessLevel.*;

@Data
@NoArgsConstructor(access = PROTECTED)
@RequiredArgsConstructor(staticName = "of")
@Builder @AllArgsConstructor
@DynamoDBTable(tableName = CONCEPT_TABLE_NAME)
public class Concept {

    @NonNull
    @DynamoDBHashKey(attributeName = "uuid")
    private String uuid;

    @NonNull
    @DynamoDBAttribute(attributeName = "label")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "scheme-label-index")
    private String label;

    @DynamoDBAttribute(attributeName = "definition")
    private String definition;

    @NonNull
    @DynamoDBAttribute(attributeName = "scheme")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "scheme-label-index")
    private String scheme;

    @DynamoDBAttribute(attributeName = "retired")
    @DynamoDBTyped(BOOL)
    private boolean retired;

    @DynamoDBAttribute(attributeName = "relationships")
    private Map<String, List<String>> relationships;

}