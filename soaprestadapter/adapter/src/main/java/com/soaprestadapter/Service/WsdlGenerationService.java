package com.soaprestadapter.Service;

import com.soaprestadapter.Request.WsdlJobRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.cxf.tools.wsdlto.WSDLToJava;
import org.springframework.stereotype.Service;


@Service
public class WsdlGenerationService {

    /**
     * To call uploadToDB.
     */
    private final UploadWsdlToDatabaseService uploadWsdlToDatabaseService;

    public WsdlGenerationService(final UploadWsdlToDatabaseService uploadWsdlToDatabaseService) {
        this.uploadWsdlToDatabaseService = uploadWsdlToDatabaseService;
    }

    public List<String> listFilenamesForWsdl(final Path dir, final String extension) throws IOException {
        List<String> files = new ArrayList<>();
        try {
            Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(extension))
                    .forEach(path -> files.add(path.getFileName().toString()));
        } catch (IOException e) {
            System.err.println("Exception while listFilenames: " + e.getMessage());
        }
        return files;
    }

    public Map<String, Map<String, List<String>>> processWsdlUrls
    (final List<WsdlJobRequest> jobRequests) throws IOException {
        Map<String, Map<String, List<String>>> result = new LinkedHashMap<>();
        try{
            for (int i = 0; i < jobRequests.size(); i++) {
                WsdlJobRequest job = jobRequests.get(i);
                Map<String, List<String>> generated = generateFromWsdlUrl(job.getWsdlUrl(), job.getXsdUrls());
                result.put("job_" + (i + 1), generated);
            }
        }catch (IOException e){
            System.err.println("Exception while processWsdlUrls: " + e.getMessage());
        }

        return result;
    }

    public Map<String, List<String>> generateFromWsdlUrl
    (final String wsdlUrl, final List<String> xsdUrls) throws IOException {
        Path tempDir = Files.createTempDirectory("wsdl2java");
        Path wsdlPath = downloadFile(wsdlUrl, tempDir);

        if (xsdUrls != null) {
            for (String xsdUrl : xsdUrls) {
                downloadFile(xsdUrl, tempDir);
            }
        }

        Path outputDir = Files.createTempDirectory("cxf_output");
        String[] cxfArgs = new String[]{
            "-d", outputDir.toString(),
            "-compile",
                wsdlPath.toString()
        };

        runWsdlToJava(cxfArgs);

        System.out.println("Generated files:");
        Files.walk(outputDir)
                .filter(Files::isRegularFile)
                .forEach(path -> System.out.println(" - " + path.toAbsolutePath()));

        List<Path> classFilesPaths = getClassFilePaths(outputDir);
        uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, classFilesPaths);

        Map<String, List<String>> result = new LinkedHashMap<>();
        result.put("java", listFilenamesForWsdl(outputDir, ".java"));
        result.put("class", listFilenamesForWsdl(outputDir, ".class"));
        return result;

    }

    public List<Path> getClassFilePaths(final Path outputDir) throws IOException {
        return Files.walk(outputDir)
                .filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
    }

    public void runWsdlToJava(final String[] cxfArgs) {
        WSDLToJava.main(cxfArgs);
    }

    public Path downloadFile(final String urlStr, final Path targetDir) throws IOException {
        URL url = new URL(urlStr);
        String filename = Paths.get(url.getPath()).getFileName().toString();
        Path targetFile = targetDir.resolve(filename);
        try (InputStream in = url.openStream()) {
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return targetFile;
    }
}
