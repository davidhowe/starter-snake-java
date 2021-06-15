package com.battlesnake.starter;

import com.battlesnake.starter.models.MoveData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jpackage.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

/**
 * This is a simple Battlesnake server written in Java.
 * 
 * For instructions see
 * https://github.com/BattlesnakeOfficial/starter-snake-java/README.md
 */
public class Snake {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Handler HANDLER = new Handler();
    private static final Logger LOG = LoggerFactory.getLogger(Snake.class);

    /**
     * Main entry point.
     *
     * @param args are ignored.
     */
    public static void main(String[] args) {
        String port = System.getProperty("PORT");
        if (port != null) {
            LOG.info("Found system provided port: {}", port);
        } else {
            LOG.info("Using default port: {}", port);
            port = "8080";
        }
        port(Integer.parseInt(port));
        get("/",  HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/start", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/move", HANDLER::process, JSON_MAPPER::writeValueAsString);
        post("/end", HANDLER::process, JSON_MAPPER::writeValueAsString);
    }

    /**
     * Handler class for dealing with the routes set up in the main method.
     */
    public static class Handler {

        /**
         * For the start/end request
         */
        private static final Map<String, String> EMPTY = new HashMap<>();

        /**
         * Generic processor that prints out the request and response from the methods.
         *
         * @param req
         * @param res
         * @return
         */
        public Map<String, String> process(Request req, Response res) {
            try {
                JsonNode parsedRequest = JSON_MAPPER.readTree(req.body());
                String uri = req.uri();
                LOG.info("{} called with: {}", uri, req.body());
                Map<String, String> snakeResponse;
                if (uri.equals("/")) {
                    snakeResponse = index();
                } else if (uri.equals("/start")) {
                    snakeResponse = start(parsedRequest);
                } else if (uri.equals("/move")) {
                    snakeResponse = move(parsedRequest);
                } else if (uri.equals("/end")) {
                    snakeResponse = end(parsedRequest);
                } else {
                    throw new IllegalAccessError("Strange call made to the snake: " + uri);
                }
                LOG.info("Responding with: {}", JSON_MAPPER.writeValueAsString(snakeResponse));
                return snakeResponse;
            } catch (Exception e) {
                LOG.warn("Something went wrong!", e);
                return null;
            }
        }

        public Map<String, String> index() {         
            Map<String, String> response = new HashMap<>();
            response.put("apiversion", "1");
            response.put("author", "davidhowe");
            response.put("color", "#ef476f");
            response.put("head", "caffeine");
            response.put("tail", "coffee");
            return response;
        }

        public Map<String, String> start(JsonNode startRequest) {
            LOG.info("START");
            return EMPTY;
        }

        public Map<String, String> move(JsonNode moveRequest) {
            try {
                LOG.info("Data: {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(moveRequest));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            try {
                MoveData moveData = new ObjectMapper().readValue(new ObjectMapper().writeValueAsString(moveRequest), MoveData.class);
                LOG.info("turn=", moveData.turn);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] possibleMoves = { "up", "down", "left", "right" };

            // Choose a random direction to move in
            int choice = new Random().nextInt(possibleMoves.length);
            String move = possibleMoves[choice];

            LOG.info("MOVE {}", move);

            Map<String, String> response = new HashMap<>();
            response.put("move", move);
            return response;
        }

        /**
         * This method is called when a game your Battlesnake was in ends.
         * 
         * It is purely for informational purposes, you don't have to make any decisions
         * here.
         *
         * @param endRequest a map containing the JSON sent to this snake. Use this data
         *                   to know which game has ended
         * @return responses back to the engine are ignored.
         */
        public Map<String, String> end(JsonNode endRequest) {

            LOG.info("END");
            return EMPTY;
        }
    }

}
