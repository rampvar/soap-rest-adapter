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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadWsdlToDatabaseService {

    /**
     * Injected SqliteService for Using jdbc template for insert
     * generated WSDL class entries from the database.
     */
    @Autowired
    private WsdlToClassStorageStrategy storageStrategy;

    /**
     * Injected class loader service
     */
    @Autowired
    private BlobClassLoaderService classLoaderService;

    /**
     *
     * @param wsdlUrl
     * @param filesPath
     * @throws IOException
     */
    public void uploadWsdlToDb(final String wsdlUrl , final List<Path> filesPath) throws IOException {

        System.out.println("Files Path" + filesPath);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
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
                    e.printStackTrace();
                }
            }
        }
        dataOutputStream.flush();
        byte[] blobData = byteArrayOutputStream.toByteArray();

        GeneratedWsdlClassEntity wsdlClassEntity = new GeneratedWsdlClassEntity();
        wsdlClassEntity.setWsdlUrl(wsdlUrl);
        //wsdlClassEntity.setClassName(classFile.getFileName().toString());
        wsdlClassEntity.setClassData(blobData);
        wsdlClassEntity.setGeneratedAt(LocalDateTime.now());
        storageStrategy.save(wsdlClassEntity);
    }
}
