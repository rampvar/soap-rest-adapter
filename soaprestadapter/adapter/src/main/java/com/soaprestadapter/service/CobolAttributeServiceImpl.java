package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.exception.DataBaseException;
import com.soaprestadapter.exception.IllegalArgumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.JDBCException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * CobolAttributeServiceImpl implementation representing the Cobol attributes operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CobolAttributeServiceImpl implements CobolAttributeService {

    /**
     * CobolAttributeRepository
     */
    //private final CobolAttributeRepository repository;
    private  final WsdlToClassStorageStrategy repository;
    /**
     * HTTP status code indicating an internal server error (500).
     * Used to signal that the server encountered an unexpected condition
     * that prevented it from fulfilling the request.
     */
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    /**
     * getPayloadOne method
     *
     * @param operationName
     * @Return string
     */
    @Override
    public String getPayloadOne(final String operationName) {
        try {
            return repository.findPayloadOneByOperationName(operationName);
        } catch (DataAccessException | IllegalArgumentException | JDBCException e) {
            throw new DataBaseException(HTTP_INTERNAL_SERVER_ERROR,
                    "Failed to get data from database payloadOne. Cause: " + e.getMessage());
        }
    }

    /**
     * getPayloadTwo method
     *
     * @param operationName
     * @Return string
     */
    @Override
    public String getPayloadTwo(final String operationName) {
        try {
            return repository.findPayloadTwoByOperationName(operationName);
        } catch (DataAccessException | IllegalArgumentException | JDBCException e) {
            throw new DataBaseException(HTTP_INTERNAL_SERVER_ERROR,
                    "Failed to get data from database payloadTwo. Cause: " + e.getMessage());
        }
    }
}
