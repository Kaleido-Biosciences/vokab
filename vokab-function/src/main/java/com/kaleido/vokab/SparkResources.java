package com.kaleido.vokab;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleido.vokab.domain.Alias;
import com.kaleido.vokab.service.AliasRepository;
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
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void defineResources() {

        //intercept requests
        before((request, response) -> {
            log.info("Received {} request at {}", request.requestMethod(), request.pathInfo());
            response.type("application/json");
        });

        //Aliases routes
        get("/aliases", (request, response) -> {
            //todo think more about status code
            response.status(200);
            return aliasRepository.findAll();
        }, new JsonTransformer());

        post("/aliases", (request, response) -> {
            Alias alias = objectMapper.readValue(request.body(), Alias.class);
            aliasRepository.write(alias);
            response.status(201);
            return "";
        });
        //Concepts routes
        //todo setup more routes including post and delete

        get("/health", (request, response) -> {
            response.status(200);
            return "Alive";
        }, new JsonTransformer());

        //demo stuff - can be removed
//
//        //handle POST
//        post("/ping", (request, response) -> {
//            Map<String, Object> pong = new HashMap<>();
//            Map body = new ObjectMapper().readValue(request.body(), Map.class);
//            pong.put("objectYouPosted", body);
//            return pong;
//        }, new JsonTransformer());
//
//        post("/path/to/resource", (request, response) -> {
//            log.info("request body is {}", request.body());
//            return "thanks for the post";
//        }, new JsonTransformer());
    }
}