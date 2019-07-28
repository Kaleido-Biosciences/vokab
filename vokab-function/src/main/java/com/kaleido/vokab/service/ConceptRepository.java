package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.kaleido.vokab.domain.Concept;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor(staticName = "of")
public class ConceptRepository {

    @NonNull
    @Getter
    private DynamoDBMapper mapper;

    public void write(Concept concept){
        mapper.save(concept);
    }

    public void delete(Concept concept){
        mapper.delete(concept);
    }

    public PaginatedScanList<Concept> findAll(){
        return mapper.scan(Concept.class, new DynamoDBScanExpression());
    }

    public Concept findOne(String uuid){
        return mapper.load(Concept.of(uuid));
    }

    public PaginatedQueryList<Concept> findAllBySchemeAndLabel(String scheme, String label){
        HashMap<String, AttributeValue> eav = new HashMap<>();
        eav.put(":scheme",  new AttributeValue().withS(scheme));
        eav.put(":label",  new AttributeValue().withS(label));

        DynamoDBQueryExpression<Concept> queryExpression = new DynamoDBQueryExpression<Concept>()
                .withIndexName("scheme-label-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("scheme = :scheme and label = :label")
                .withExpressionAttributeValues(eav);

        return mapper.query(Concept.class, queryExpression);
    }

    public int count(){
        return mapper.count(Concept.class, new DynamoDBQueryExpression<>());
    }
}
