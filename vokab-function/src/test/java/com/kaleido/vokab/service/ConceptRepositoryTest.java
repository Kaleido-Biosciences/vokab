package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.kaleido.vokab.domain.Concept;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.UUID;

import static com.kaleido.vokab.service.DynamoDbService.*;
import static org.junit.jupiter.api.Assertions.*;

class ConceptRepositoryTest {

    private static DynamoDBMapper mapper;
    private static ConceptRepository repository;
    private static AmazonDynamoDB client = null;


    @BeforeAll
    static void init(){
        //this allows the embedded DynamoDB to use sqlite as a backend
        System.setProperty("sqlite4java.library.path", "src/test/resources/libs/");

        // Create an in-memory and in-process instance of DynamoDB Local that skips HTTP
        client = DynamoDBEmbedded.create().amazonDynamoDB();


        mapper = new DynamoDBMapper(client);
        repository = ConceptRepository.of(mapper);

        try {
            System.out.println("Attempting to create Concept table; please wait...");
            DynamoDB db = new DynamoDB(client);
            final CreateTableRequest createTableRequest = mapper.generateCreateTableRequest(Concept.class)
                    .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

            createTableRequest.getGlobalSecondaryIndexes().forEach(index -> index.setProvisionedThroughput(new ProvisionedThroughput(10L, 10L)));

            Table table = db.createTable(createTableRequest);

            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    @AfterAll
    static void cleanUp(){
        if(client != null){
            client.shutdown();
        }
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void write() {
        Concept concept = Concept.of(UUID.randomUUID().toString());
        repository.write(concept);
        assertEquals(Long.valueOf(1L), client.describeTable(CONCEPT_TABLE_NAME).getTable().getItemCount());
    }

    @org.junit.jupiter.api.Test
    void delete() {
    }

    @org.junit.jupiter.api.Test
    void concepts() {
    }

    @org.junit.jupiter.api.Test
    void findConcept() {
    }
}