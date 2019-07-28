package com.kaleido.vokab.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.kaleido.vokab.domain.Alias;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kaleido.vokab.service.DynamoDbService.ALIAS_TABLE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AliasRepositoryTest {

    private static DynamoDBMapper mapper;
    private static AliasRepository repository;
    private static AmazonDynamoDB client = null;
    private static List<Alias> aliases;


    @BeforeAll
    static void init(){
        //this allows the embedded DynamoDB to use sqlite as a backend
        System.setProperty("sqlite4java.library.path", "src/test/resources/libs/");

        // Create an in-memory and in-process instance of DynamoDB Local that skips HTTP
        client = DynamoDBEmbedded.create().amazonDynamoDB();


        mapper = new DynamoDBMapper(client);
        repository = AliasRepository.of(mapper);

    }

    @AfterAll
    static void cleanUp(){
        if(client != null){
            client.shutdown();
        }
    }

    @BeforeEach
    void populateTable(){
        try {
            DynamoDB db = new DynamoDB(client);
            final CreateTableRequest createTableRequest = mapper.generateCreateTableRequest(Alias.class)
                    .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));

            Table table = db.createTable(createTableRequest);

            table.waitForActive();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: "+ALIAS_TABLE_NAME);
            System.err.println(e.getMessage());
        }

        aliases = IntStream.range(0, 10)
                           .mapToObj(i -> {
                               Alias a = Alias.of("alias"+i, UUID.randomUUID().toString());
                               repository.write(a);
                               return a;
                           })
                           .collect(Collectors.toList());
    }

    @AfterEach
    void cleanTable(){
        client.deleteTable(ALIAS_TABLE_NAME);
    }

    @Test
    void write() {
        Alias alias = Alias.of("FOO", "conceptId1");
        repository.write(alias);
        assertEquals(Long.valueOf(aliases.size()+1), client.describeTable(ALIAS_TABLE_NAME).getTable().getItemCount());
    }

    @Test
    void delete() {
        Alias alias = aliases.get(0);

        repository.delete(alias);
        assertEquals(Long.valueOf(aliases.size() - 1), client.describeTable(ALIAS_TABLE_NAME).getTable().getItemCount());
    }

    @Test
    void findAll() {
        final PaginatedScanList<Alias> all = repository.findAll();
        assertEquals(10, all.size());
        assertTrue(aliases.containsAll(all));
    }

    @Test
    void findAllByAlias() {
        final PaginatedScanList<Alias> all = repository.findAllByAlias("alias0");
        assertEquals(1, all.size());
        assertEquals("alias0", all.get(0).getAlias());
    }

    @Test
    void findOne() {
        String alias = "foo";
        String conceptId = UUID.randomUUID().toString();

        repository.write(Alias.of(alias, conceptId));
        Alias found = repository.findOne(alias ,conceptId);

        assertEquals(alias, found.getAlias());
        assertEquals(conceptId, found.getConceptId());
    }
}