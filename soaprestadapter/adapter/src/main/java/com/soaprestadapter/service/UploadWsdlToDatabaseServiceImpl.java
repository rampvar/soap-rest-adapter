package com.soaprestadapter.service;

import com.soaprestadapter.WsdlToClassStorageStrategy;
import com.soaprestadapter.entity.GeneratedWsdlClassEntity;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
@AllArgsConstructor
public class UploadWsdlToDatabaseServiceImpl implements UploadWsdlToDatabaseService {

    /**
     * Injected SqliteService for Using jdbc template for insert
     * generated WSDL class entries from the database.
     */
    private final WsdlToClassStorageStrategy storageStrategy;

    /**
     * Injected class loader service
     */
    private final BlobClassLoaderServiceImpl classLoaderService;

    /**
     *
     * @param wsdlUrl
     * @param filesPath
     * @throws IOException
     */
    public void uploadWsdlToDb(final String wsdlUrl, final List<Path> filesPath) {

        if (filesPath.isEmpty() || wsdlUrl.isEmpty()) {
            throw new IllegalArgumentException("WSDL URL and filepath  must not be null");
        }

        log.info("Uploading .class files to database: {}", filesPath);
        byte[] blobData;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            for (Path classFile : filesPath) {
                if (Files.isRegularFile(classFile) && classFile.toString().endsWith(".class")) {
                    try {
                        byte[] classData = Files.readAllBytes(classFile);
                        byte[] classNameBytes = classFile.getFileName().toString().getBytes(StandardCharsets.UTF_8);

                        dataOutputStream.writeInt(classNameBytes.length);      // write filename length
                        dataOutputStream.write(classNameBytes);                // write filename
                        dataOutputStream.writeInt(classData.length);     // write class byte array length
                        dataOutputStream.write(classData);
                    } catch (IOException e) {
                        log.error("Error reading class file Name: {}, Error Message:  {}", classFile, e.getMessage());
                    }
                }
            }
            dataOutputStream.flush();
            blobData = byteArrayOutputStream.toByteArray();

            if (blobData.length > 0) {
                GeneratedWsdlClassEntity wsdlClassEntity = new GeneratedWsdlClassEntity();
                wsdlClassEntity.setWsdlUrl(wsdlUrl);
                wsdlClassEntity.setClassData(blobData);
                wsdlClassEntity.setGeneratedAt(LocalDateTime.now());
                storageStrategy.save(wsdlClassEntity);
            } else {
                log.warn("No valid .class files to upload for WSDL: {}", wsdlUrl);
            }

            classLoaderService.loadClassesFromDb();
        } catch (IOException e) {
            log.error("Error writing class data blob to output stream: {}", e.getMessage());
        }


    }
}
