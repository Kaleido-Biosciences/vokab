package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.kaleido.vokab.domain.Alias;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.kaleido.vokab.service.DynamoDbService.ALIAS_TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AliasRepositoryTest {

    private static DynamoDBMapper mapper;
    private static AliasRepository repository;
    private static AmazonDynamoDB client = null;


    @BeforeAll
    static void init(){
        //this allows the embedded DynamoDB to use sqlite as a backend
        System.setProperty("sqlite4java.library.path", "src/test/resources/libs/");

        // Create an in-memory and in-process instance of DynamoDB Local that skips HTTP
        client = DynamoDBEmbedded.create().amazonDynamoDB();


        mapper = new DynamoDBMapper(client);
        repository = AliasRepository.of(mapper);

        try {
            System.out.println("Attempting to create alias table; please wait...");
            DynamoDB db = new DynamoDB(client);
            final CreateTableRequest createTableRequest = mapper.generateCreateTableRequest(Alias.class).withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

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

    @Test
    void write() {
        Alias alias = Alias.of("alias1", "conceptId1");
        repository.write(alias);
        assertEquals(Long.valueOf(1), client.describeTable(ALIAS_TABLE_NAME).getTable().getItemCount());
    }

    @Test
    void delete() {
        Alias alias = Alias.of("alias1", "conceptId1");
        repository.write(alias);
        assertEquals(Long.valueOf(1), client.describeTable(ALIAS_TABLE_NAME).getTable().getItemCount());
        repository.delete(alias);
        assertEquals(Long.valueOf(0), client.describeTable(ALIAS_TABLE_NAME).getTable().getItemCount());
    }

    @Test
    void findAll() {
    }

    @Test
    void findAllByAlias() {
    }

    @Test
    void findOne() {
    }
}