package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.kaleido.vokab.domain.Alias;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor(staticName = "of")
public class AliasRepository {
    @NonNull
    @Getter
    private DynamoDBMapper mapper;

    public void write(Alias alias){
        mapper.save(alias);
    }

    public void delete(Alias alias){
        mapper.delete(alias);
    }

    public PaginatedScanList<Alias> findAll(){
        return mapper.scan(Alias.class, new DynamoDBScanExpression());
    }

    public PaginatedScanList<Alias> findAllByAlias(String alias){
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(alias));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("alias = :val1").withExpressionAttributeValues(eav);

        return mapper.scan(Alias.class, scanExpression);
    }

    public Alias findOne( String alias, String conceptId){
        return mapper.load(Alias.of(alias, conceptId));
    }

    public int count(){
        return mapper.count(Alias.class, new DynamoDBQueryExpression<>());
    }
}
