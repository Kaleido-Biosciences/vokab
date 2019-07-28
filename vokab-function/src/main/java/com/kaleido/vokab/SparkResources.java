package com.kaleido.vokab;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleido.vokab.domain.Alias;
import com.kaleido.vokab.domain.Concept;
import com.kaleido.vokab.service.AliasRepository;
import com.kaleido.vokab.service.ConceptRepository;
import com.kaleido.vokab.service.DynamoDbService;
import com.kaleido.vokab.util.JsonTransformer;
import lombok.extern.slf4j.Slf4j;

import static spark.Spark.*;

/**
 * Sets up resources to deal with HTTP requests
 */
@Slf4j
public class SparkResources {


    private static DynamoDbService dbService = new DynamoDbService();
    private static DynamoDBMapper mapper = new DynamoDBMapper(dbService.getDynamoDB());
    
    private static AliasRepository aliasRepository = AliasRepository.of(mapper);
    private static ConceptRepository conceptRepository = ConceptRepository.of(mapper);
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void defineResources() {

        //intercept requests
        before((request, response) -> {
            log.info("Received {} request at {}", request.requestMethod(), request.pathInfo());
            response.type("application/json");
        });

        //Aliases routes
        get("/aliases", (request, response) -> aliasRepository.findAll(), new JsonTransformer());

        get("/aliases/:alias", (request, response) -> {
            String alias = request.params(":alias");
            final PaginatedScanList<Alias> allByAlias = aliasRepository.findAllByAlias(alias);

            if(allByAlias != null && allByAlias.size() > 0) {
                return allByAlias;
            }else{
                response.status(404);
                return alias+" Not Found";
            }
        }, new JsonTransformer());

        get("/aliases/:alias/:conceptId", (request, response) -> {
            String alias = request.params(":alias");
            String conceptId = request.params(":conceptId");

            final Alias one = aliasRepository.findOne(alias, conceptId);
            if(one != null) {
                return one;
            }else {
                response.status(404);
                return alias+"/"+conceptId+" Not Found";
            }
        }, new JsonTransformer());
        
        post("/aliases", (request, response) -> {
            Alias alias = objectMapper.readValue(request.body(), Alias.class);

            //todo check if the posted Alias has an alias and a conceptId

            aliasRepository.write(alias);

            Alias saved = aliasRepository.findOne(alias.getAlias(), alias.getConceptId());
            response.status(201);

            return saved;
        }, new JsonTransformer());
        
        delete("/aliases/:alias/:conceptId", (request, response) -> {
            String alias = request.params(":alias");
            String conceptId = request.params(":conceptId");
            
            Alias template = aliasRepository.findOne(alias, conceptId);
            if( template != null ) {
                aliasRepository.delete( template );
                response.status(200);
                return "Deleted";
            }
            response.status(404);
            return alias+"/"+conceptId+" Not Found";
        });

        //Concepts routes
        get("/concepts", (request, response) -> {
            response.status(200);
            return conceptRepository.findAll();
        }, new JsonTransformer());

        get("/concepts/:uuid", (request, response) -> {
            String uuid = request.params(":uuid");
            final Concept concept = conceptRepository.findOne(uuid);

            if(concept != null){
            return concept;
            } else {
                response.status(404);
                return uuid+" Not Found";
            }
        }, new JsonTransformer());

        post("/concepts", (request, response) -> {
            Concept concept = objectMapper.readValue(request.body(), Concept.class);
            //todo test if concept has a uuid
            conceptRepository.write(concept);
            Concept saved = conceptRepository.findOne(concept.getUuid());

            response.status(201);
            return saved;
        }, new JsonTransformer());

        delete("/concepts/:conceptId", (request, response) -> {
            String conceptId = request.params(":conceptId");

            Concept template = conceptRepository.findOne(conceptId);
            if( template != null ) {
                conceptRepository.delete( template );
                response.status(200);
                return "Deleted";
            }
            response.status(404);
            return conceptId+" Not Found";
        });

        //todo setup a route to search for concepts by label and schema
        

        get("/health", (request, response) -> "alive");
        get("/vokab", (request, response) -> "alive");

    }
}