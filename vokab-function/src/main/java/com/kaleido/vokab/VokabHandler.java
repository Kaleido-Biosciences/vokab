package com.kaleido.vokab;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spark.SparkLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.extern.slf4j.Slf4j;
import spark.Spark;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class VokabHandler implements RequestStreamHandler {

    private static SparkLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    //setup the proxy and the resources
    static {
        try {
            handler = SparkLambdaContainerHandler.getAwsProxyHandler();
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
        handler.proxyStream(inputStream, outputStream, context);
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
