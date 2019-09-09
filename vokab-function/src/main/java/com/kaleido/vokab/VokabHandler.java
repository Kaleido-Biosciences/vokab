package com.kaleido.vokab;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spark.SparkLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import spark.Spark;

import java.io.*;

@Slf4j
public class VokabHandler implements RequestStreamHandler {

    private static SparkLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    private static ObjectMapper objectMapper;

    //setup the proxy and the resources
    static {
        try {
            handler = SparkLambdaContainerHandler.getAwsProxyHandler();
            objectMapper = new ObjectMapper();

            //setup the resource endpoints from our SparkResources class
            SparkResources.defineResources();

            Spark.awaitInitialization();
            log.info("Initialized lambda");
        } catch (ContainerInitializationException e) {
            // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spark container", e);
        }
    }

    /**
     * Entry point of the Lambda. Referenced in the template.yaml and called by the Lambda framework to process
     * the request. Proxies to a {@code SparkLambdaContainerHandler} to do the actual handling
     * @param inputStream data sent to the function
     * @param outputStream response from the function
     * @param context the context of the call
     * @throws IOException if the input stream cannot be read or the output stream written to
     */
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {

        /*
            The handler assigns requests to methods based on the Path and not the Resource. To
            make the handler resilient to things like Custom domains in API gateway with potentially multiple
            mappings I need to get the resource from the request and set that as the path
         */
        AwsProxyRequest request = objectMapper.readValue(inputStream, AwsProxyRequest.class);
        // set the path to be the resource
        request.setPath( request.getResource() );

        //write the request to an out stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        objectMapper.writeValue(out, request);

        //proxy the request (a new input stream is made from the modified output stream
        handler.proxyStream(new ByteArrayInputStream(out.toByteArray()), outputStream, context);
    }




//    /**
//     * Basically demo code
//     * @param args
//     */
//    public static void main(String[] args) {
//        VokabHandler vokabHandler = new VokabHandler();
//
//        final AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
//                    .withEndpointConfiguration(
//                            new AwsClientBuilder.EndpointConfiguration(
//                                    "http://localhost:8000", "us-east-1"));
//        vokabHandler.setDynamoDB(builder.build());
//
//        Alias alias = new Alias();
//        alias.setAlias("Foo");
//        alias.setConceptId("ghws-4f-g4-dfvggg123");
//
//        System.out.println("alias = " + alias);
//        System.out.println("writing alias to local dynamodb");
//        vokabHandler.write(alias);
//
//        System.out.println("\nquerying dynamo");
//
//        final PaginatedScanList<Alias> result = vokabHandler.findAliases("Foo");
//        System.out.println("found "+result.size()+" results");
//        result.forEach(a -> {
//            System.out.println("alias = " + a);
//            vokabHandler.getMapper().delete(a);
//        });
//    }
}
