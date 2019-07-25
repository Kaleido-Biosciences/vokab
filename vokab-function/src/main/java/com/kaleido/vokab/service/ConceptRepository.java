package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.kaleido.vokab.domain.Concept;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class ConceptRepository {

    @NonNull
    private DynamoDBMapper mapper;

    public void write(Concept concept){
        mapper.save(concept);
    }

    public void delete(Concept concept){
        mapper.delete(concept);
    }

    public PaginatedScanList<Concept> concepts(){
        return mapper.scan(Concept.class, new DynamoDBScanExpression());
    }

    public Concept findConcept(String uuid){

        return mapper.load(Concept.of(uuid));
    }

}
