package com.bajajfinserv.webhook.service;

import com.bajajfinserv.webhook.dto.WebhookRequest;
import com.bajajfinserv.webhook.dto.WebhookResponse;
import com.bajajfinserv.webhook.dto.SubmissionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SqlSolutionService sqlSolutionService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Application started. Initiating webhook generation...");
        try {
            generateWebhookAndSubmitSolution();
        } catch (Exception e) {
            logger.error("Error in webhook process: ", e);
        }
    }

    public void generateWebhookAndSubmitSolution() {
        try {
            // Step 1: Generate webhook
            WebhookRequest request = new WebhookRequest();
            request.setName("Prithviraj Panth");
            request.setRegNo("22BCE3382");
            request.setEmail("prathirajpanth2003@gmail.com"); // CHANGE THIS TO YOUR ACTUAL EMAIL

            logger.info("Sending POST request to generate webhook...");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                    GENERATE_WEBHOOK_URL,
                    HttpMethod.POST,
                    entity,
                    WebhookResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                WebhookResponse webhookResponse = response.getBody();
                logger.info("Webhook generated successfully. Access token received.");

                // Step 2: Solve SQL problem (based on even regNo ending)
                String sqlSolution = sqlSolutionService.getSqlSolutionForEvenRegNo();
                logger.info("SQL solution prepared: {}", sqlSolution);

                // Step 3: Submit solution
                submitSolution(webhookResponse.getAccessToken(), sqlSolution);

            } else {
                logger.error("Failed to generate webhook. Status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error in generateWebhookAndSubmitSolution: ", e);
        }
    }

    private void submitSolution(String accessToken, String sqlQuery) {
        try {
            logger.info("Submitting solution to webhook...");

            SubmissionRequest submissionRequest = new SubmissionRequest();
            submissionRequest.setFinalQuery(sqlQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            HttpEntity<SubmissionRequest> entity = new HttpEntity<>(submissionRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    SUBMIT_WEBHOOK_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            logger.info("Solution submitted. Response status: {}", response.getStatusCode());
            logger.info("Response body: {}", response.getBody());

        } catch (Exception e) {
            logger.error("Error submitting solution: ", e);
        }
    }
}