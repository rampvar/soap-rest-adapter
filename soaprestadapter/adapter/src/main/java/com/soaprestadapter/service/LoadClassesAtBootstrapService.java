package com.soaprestadapter.service;

/**
 *  Load .class files into DB
 */
public interface LoadClassesAtBootstrapService {
    /**
     * service implementation method to load .class files into db
     * @throws Exception
     */
    void loadClassAtBootstrap() throws Exception;
}
