package com.soaprestadapter.controller;

import com.soaprestadapter.request.WsdlJobRequest;
import com.soaprestadapter.service.WsdlGenerationService;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/generate")
public class WsdlGeneratorController {

    /**
     * WsdlGenerationService.
     */
    private final WsdlGenerationService generationService;

    public WsdlGeneratorController(final WsdlGenerationService generationService) {
        this.generationService = generationService;
    }

    @PostMapping("/from-urls")
    public ResponseEntity<?> generateFromUrls(@RequestBody final List<WsdlJobRequest> jobRequests) throws IOException {
        try {
            Map<String, Map<String, List<String>>> result = new LinkedHashMap<>();

            result = generationService.processWsdlUrls(jobRequests);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }
}
