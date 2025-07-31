package com.soaprestadapter.factory;

import com.soaprestadapter.DatabaseService;
import com.soaprestadapter.model.ResponseType;
import com.soaprestadapter.service.AmtResponseHandler;
import com.soaprestadapter.service.BlueageResponseHandler;
import com.soaprestadapter.service.CustomResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Factory class for handling REST responses.
 */
@RequiredArgsConstructor
@Service
public class ResponseHandlerFactory {

    /**
     * Db class for handling REST responses payload with copybook.
     */
    private final DatabaseService dbService;

    /**
     * ResponseHandler for handling REST responses.
     * @param type The type of response (AMT, BLUEAGE, or CUSTOM)
     * @return ResponseHandler for the specified type, or null if the type is invalid
     */
    public ResponseHandler getResponseHandler(final String type) {

        ResponseType responseType = ResponseType.fromString(type);
        if (responseType == null) {
            return null;
        }
        switch (responseType) {
            case AMT_RESPONSE -> {
                return new AmtResponseHandler(dbService);
            }
            case BLUEAGE_RESPONSE -> {
                return new BlueageResponseHandler(dbService);
            }
            case CUSTOM_RESPONSE -> {
                return new CustomResponseHandler(dbService);
            }
            default -> {
                return null;
            }
        }




//        if ("AMT-RESPONSE".equalsIgnoreCase(type)) {
//            return new AmtResponseHandler(fetchResponseCopybookDataStrategy);
//        } else if ("BLUEAGE-RESPONSE".equalsIgnoreCase(type)) {
//            return new BlueageResponseHandler(fetchResponseCopybookDataStrategy);
//        } else if ("CUSTOM-RESPONSE".equalsIgnoreCase(type)) {
//            return new CustomResponseHandler(fetchResponseCopybookDataStrategy);
//        }
//        return null;
    }
}
