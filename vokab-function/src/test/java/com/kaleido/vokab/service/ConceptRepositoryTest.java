/*
 * Copyright (c) 2019. Kaleido Biosciences. All Rights Reserved.
 */

package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.kaleido.vokab.domain.Concept;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kaleido.vokab.service.DynamoDbService.CONCEPT_TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConceptRepositoryTest {

    private static DynamoDBMapper mapper;
    private static ConceptRepository repository;
    private static AmazonDynamoDB client = null;
    private static List<Concept> concepts;


    @BeforeAll
    static void init(){
        //this allows the embedded DynamoDB to use sqlite as a backend
        System.setProperty("sqlite4java.library.path", "src/test/resources/libs/");

        // Create an in-memory and in-process instance of DynamoDB Local that skips HTTP
        client = DynamoDBEmbedded.create().amazonDynamoDB();


        mapper = new DynamoDBMapper(client);
        repository = ConceptRepository.of(mapper);

    }

    @BeforeEach
    void setUpTable(){
        try {
            DynamoDB db = new DynamoDB(client);
            final CreateTableRequest createTableRequest = mapper.generateCreateTableRequest(Concept.class)
                    .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

            createTableRequest.getGlobalSecondaryIndexes()
                    .forEach(index -> index.setProvisionedThroughput(new ProvisionedThroughput(10L, 10L)));


            Table table = db.createTable(createTableRequest);

            table.waitForActive();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }

        concepts = IntStream.range(0,10)
                            .mapToObj(i -> {
                                Concept c = Concept.builder().uuid(UUID.randomUUID().toString()).label("label"+i).scheme("TEST").build();
                                mapper.save(c);
                                return c;
                            }).collect(Collectors.toList());
    }

    @AfterEach
    void cleanUpTable(){
        client.deleteTable(CONCEPT_TABLE_NAME);
    }

    @AfterAll
    static void cleanUp(){
        if(client != null){
            client.shutdown();
        }
    }

    @org.junit.jupiter.api.Test
    void write() {
        Concept concept = Concept.of(UUID.randomUUID().toString());
        repository.write(concept);
        assertEquals(Long.valueOf(concepts.size()+1), client.describeTable(CONCEPT_TABLE_NAME).getTable().getItemCount());
    }

    @org.junit.jupiter.api.Test
    void delete() {
        repository.delete(concepts.get(0));
        assertEquals(Long.valueOf(concepts.size() - 1), client.describeTable(CONCEPT_TABLE_NAME).getTable().getItemCount());
    }

    @org.junit.jupiter.api.Test
    void findAll() {
        final PaginatedScanList<Concept> all = repository.findAll();
        assertEquals(concepts.size(), all.size());
        assertTrue(concepts.containsAll(all));
    }

    @org.junit.jupiter.api.Test
    void findOne() {
        final Concept concept = repository.findOne(concepts.get(0).getUuid());
        assertEquals(concepts.get(0).getUuid(), concept.getUuid());
    }

    @Test
    void findAllBySchemeAndLabel(){
        final PaginatedQueryList<Concept> all = repository.findAllBySchemeAndLabel("TEST", "label0");
        assertEquals(1, all.size());
        assertEquals("TEST", all.get(0).getScheme());
        assertEquals("label0", all.get(0).getLabel());
        assertEquals(concepts.get(0).getUuid(), all.get(0).getUuid());
    }
}