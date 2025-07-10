package com.soaprestadapter.controller;

import com.soaprestadapter.request.WsdlJobRequest;
import com.soaprestadapter.service.WsdlGenerationServiceImpl;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller.
 */
@RestController
@RequestMapping("/generate")
@Slf4j
@RequiredArgsConstructor
public class WsdlGeneratorController {

    /**
     * WsdlGenerationService.
     */
    private final WsdlGenerationServiceImpl generationService;

    /**
     * Rest Endpoint.
     * @param jobRequests will have request payload
     * @return response from process
     */
    @PostMapping("/from-urls")
    public ResponseEntity<?> generateFromUrls(@RequestBody final List<WsdlJobRequest> jobRequests) throws IOException {
        try {
            log.info("Request Received");
            Map<String, Map<String, List<String>>> result = generationService.processWsdlUrls(jobRequests);
            log.info("Generated Files:{}", result.toString());
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Exception while processing urls:{}", String.valueOf(e));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}
