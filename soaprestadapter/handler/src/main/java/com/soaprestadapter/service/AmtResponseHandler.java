package com.soaprestadapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.FetchResponseCopybookDataStrategy;
import com.soaprestadapter.entity.FetchResponseCopybookDataEntity;
import com.soaprestadapter.exception.DataBaseException;
import com.soaprestadapter.factory.ResponseHandler;
import com.soaprestadapter.model.CobolField;
import com.soaprestadapter.model.CobolHeaderField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

/**
 * class for handling AMT REST responses.
 */
@Slf4j
@RequiredArgsConstructor
@Component("AMT-RESPONSE")
public class AmtResponseHandler implements ResponseHandler {
    /**
     * repository to get response copybook data
     */
    private final FetchResponseCopybookDataStrategy repository;
    /**
     * THREE - constant for 3
     */
    private static final int THREE = 3;
    /**
     * TEN - constant for 10
     */
    private static final int TEN = 10;
    /**
     * HTTP status code indicating an internal server error (500).
     * Used to signal that the server encountered an unexpected condition
     * that prevented it from fulfilling the request.
     */
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    /**
     * convert rest response to AMT format
     * @param responseBody - rest response body
     * @return - converted AMT format json string
     */
    @Override
    public String convertRestResponse
    (final String responseBody, final String operationName) throws JsonProcessingException {
        log.info("Converting AMT response to JSON: {}", responseBody);
        return processResponseString(responseBody, operationName);
    }

    /**
     * Fetch response copybook data from database based on operation name
     * @param operationName - operation name for which copy book data is required
     * @return - db entry
     */
    private FetchResponseCopybookDataEntity getCopyBookDataFromDb(final String operationName) {
        log.info("Fetching copy book data from database for data: {}", operationName);
        try {
            return repository.getByOperationName(operationName);
        } catch (DataAccessException dae) {
            log .error("Database fetch failed: {}", dae.getMessage());
            throw new DataBaseException(HTTP_INTERNAL_SERVER_ERROR, "Database fetch failed");
        }
    }

    /**
     * Process AMT response and convert it to JSON
     * @param data - AMT response data
     * @param operationName - operation name for which copy book data is required
     * @return - converted AMT format json string
     */
    private String processResponseString(final String data, final String operationName) throws JsonProcessingException {
        log.info("Processing AMT Raw response: {}", data);
        if (data == null || data.isEmpty()) {
            log.error("Invalid AMT response data: {}", data);
            return null;
        }
        FetchResponseCopybookDataEntity copyBookData = getCopyBookDataFromDb(operationName);
        if (copyBookData == null) {
            log.error("No copy book data found for operation: {}", operationName);
            return null;
        }
        return getJsonObject(data, copyBookData);
    }

    /**
     *
     * @param data - AMT response data
     * @param copyBookData - copy book data
     * @return - converted AMT format json string
     */
    private static String getJsonObject
    (final String data, final FetchResponseCopybookDataEntity copyBookData) throws JsonProcessingException {
        log.info("Getting AMT responseJSON: {}", data);
        try {
            String responseAttributes = copyBookData.getResponseAttributes();
            List<CobolField> cobolFields = parseCopybook(responseAttributes.split("\\r?\\n"));
            Map<String, Object> extractedVals = extractValues(data, cobolFields);
            ObjectMapper mapper = new ObjectMapper();
            String response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(extractedVals);
            log.info("Before AMT specific conversion: {}", response);
            /**
             * Uncomment when object to be incorporated in rest response
             */
//            return appendResponse(response, responseAttributes);
            return response;
        } catch (JsonProcessingException e) {
            log.error("Error parsing AMT response: {}", e.getMessage());
        }
        return null;
    }

    /**
     * extracting values from AMT response data based on copybook data
     * @param response      - AMT response data
     * @param lines         - cobol field objects extracted from copybook lines
     * @return - map of extracted values
     **/
    private static String appendResponse(final String response, final String lines) throws JsonProcessingException {
        log.info("Appending AMT response with header");
        Map<String, List<CobolHeaderField>> structure = parseStructure(lines);
        JSONObject json = buildJson(structure);
        return computeJson(json, response);
    }

    /**
     * create final json string from extracted values
     *
     * @param structure     - cobol field objects extracted from copybook lines
     * @param response      - AMT response data
     * @return - final json string
     */
    private static String computeJson(final JSONObject structure, final String response) {
        JSONObject data = new JSONObject(response);
        JSONObject result = new JSONObject();
        for (String section : structure.keySet()) {
            JSONObject sectionObj = structure.getJSONObject(section);
            JSONArray fields = sectionObj.getJSONArray("fields");

            JSONObject sectionData = new JSONObject();
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                String fieldName = field.getString("name");
                if (data.has(fieldName)) {
                    sectionData.put(fieldName, data.get(fieldName));
                } else {
                    sectionData.put(fieldName, JSONObject.NULL);
                }
            }
            result.put(section, sectionData);
        }
        log.info("Final AMT response JSON: {}", result);
        return result.toString();
    }

    /**
     * extracting values from AMT response data based on copybook data
     * @param structure - map of extracted values
     * @return - json extracted values
     */
    private static JSONObject buildJson(final Map<String, List<CobolHeaderField>> structure) {
        JSONObject root = new JSONObject();
        for (Map.Entry<String, List<CobolHeaderField>> entry : structure.entrySet()) {
            JSONObject section = new JSONObject();
            JSONArray fieldsArray = new JSONArray();
            for (CobolHeaderField f : entry.getValue()) {
                JSONObject fieldJson = new JSONObject();
                fieldJson.put("name", f.getName());
                fieldJson.put("type", f.getType());
                fieldJson.put("length", f.getLength());
                fieldsArray.put(fieldJson);
            }
            section.put("fields", fieldsArray);
            root.put(entry.getKey(), section);
        }
        return root;
    }

    /**
     * extracting values from AMT response data based on copybook data
     *
     * @param linesCopybook - copybook lines
     * @return - map of extracted values
     **/
    private static Map<String, List<CobolHeaderField>> parseStructure(final String linesCopybook) {
        Map<String, List<CobolHeaderField>> result = new LinkedHashMap<>();

        String[] lines = linesCopybook.split("\\r?\\n");
        String currentHeader = null;

        Pattern fieldPattern = Pattern.compile
                ("\\d+\\s+(\\S+)\\s+PIC\\s+([X9])\\s*\\((\\d+)\\)\\.?$", Pattern.CASE_INSENSITIVE);


        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.endsWith("_starts")) {
                currentHeader = trimmedLine.replace("_starts", "");
                result.put(currentHeader, new ArrayList<>());
            } else if (trimmedLine.endsWith("_ends")) {
                currentHeader = null;
            } else if (currentHeader != null && !trimmedLine.isEmpty()) {
                Matcher matcher = fieldPattern.matcher(trimmedLine);
                if (matcher.find()) {
                    String name = matcher.group(1);
                    String type = matcher.group(2);
                    int length = Integer.parseInt(matcher.group(THREE));
                    result.get(currentHeader).add(new CobolHeaderField(name, type, length));
                }
            }
        }
        log.info("Parsed copybook structure: {}", result);
        return result;
    }

    /**
     * parsing copybook lines to cobol field objects
     * @param lines - copybook lines
     * @return - list of cobol fields extracted from copybook lines
     */
    private static List<CobolField> parseCopybook(final String[] lines) {
        List<CobolField> fields = new ArrayList<>();
        int position = 0;
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.contains("PIC")) {
                continue;
            }
            String[] parts = trimmedLine.split("\\s+");
            String name = parts[1];
            String pic = parts[THREE];
            int length = 0;
            String type = "";
            if (pic.startsWith("X(")) {
                length = Integer.parseInt(pic.substring(2, pic.length() - 1));
                type = "X";
            } else if (pic.matches("9+")) {
                length = pic.length();
                type = "9";
            } else if (pic.matches("9\\([0-9]+\\)V[0-9]+")) {
                String[] parts2 = pic.split("V");
                int intPart = Integer.parseInt(parts2[0].replaceAll("[^0-9]", ""));
                int decPart = Integer.parseInt(parts2[1]);
                length = intPart + decPart;
                type = "9V";
            }
            fields.add(new CobolField(name, position, length, type));
            position += length;
        }
        return fields;
    }
    /**
     * extracting values from AMT response data based on copybook fields
     * @param record - AMT response data
     * @param fields - copybook fields
     * @return - extracted values as a map
     */
    private static Map<String, Object> extractValues
    (final String record, final List<CobolField> fields) throws JsonProcessingException {
        Map<String, Object> result = new LinkedHashMap<>();
        JSONObject obj = new JSONObject(record);
        String responseString = obj.getString("data");
        for (CobolField field : fields) {
            String raw = responseString.substring(field.getStart(), field.getStart() + field.getLength()).trim();
            switch (field.getType()) {
                case "X":
                    result.put(field.getName(), raw);
                    break;
                case "9":
                    result.put(field.getName(), Integer.parseInt(raw));
                    break;
                case "9V":
                    double val = Integer.parseInt(raw) / Math.pow(TEN, field.getLength() - raw.length());
                    result.put(field.getName(), val);
                    break;
            }
        }
        return result;
    }
}