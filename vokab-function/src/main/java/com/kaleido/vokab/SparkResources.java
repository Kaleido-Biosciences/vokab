package com.kaleido.vokab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaleido.vokab.util.JsonTransformer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Sets up resources to deal with HTTP requests
 */
@Slf4j
public class SparkResources {


    public static void defineResources() {

        //intercept requests
        before((request, response) -> {
            log.info("Received {} request at {}", request.requestMethod(), request.pathInfo());
            response.type("application/json");
        });

        //Aliases routes
        get("/aliases", (request, response) -> {
            //todo find the aliases
            response.status(200);
            return null; //todo return something
        }, new JsonTransformer());


        //Concepts routes


        //demo stuff - can be removed

        //handle GET
        get("/ping", (request, response) -> {
            Map<String, String> pong = new HashMap<>();
            pong.put("pong", "Hello, World!");
            response.status(200);
            return pong;
        }, new JsonTransformer());

        //handle POST
        post("/ping", (request, response) -> {
            Map<String, Object> pong = new HashMap<>();
            Map body = new ObjectMapper().readValue(request.body(), Map.class);
            pong.put("objectYouPosted", body);
            return pong;
        }, new JsonTransformer());

        post("/path/to/resource", (request, response) -> {
            log.info("request body is {}", request.body());
            return "thanks for the post";
        }, new JsonTransformer());
    }
}