package com.soaprestadapter.service;

import com.soaprestadapter.request.WsdlJobRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.tools.wsdlto.WSDLToJava;
import org.springframework.stereotype.Service;

/**
 * WsdlGenerationServiceImpl
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WsdlGenerationServiceImpl implements WsdlGenerationService {

    /**
     * To call uploadToDB.
     */
    private final UploadWsdlToDatabaseService uploadWsdlToDatabaseService;

    /**
     * listFilenamesForWsdl to list generated files
     * @param dir will have directory path
     * @param extension to specify file type
     * @return list of fileNames
     */
    private List<String> listFilenamesForWsdl(final Path dir, final String extension) throws IOException {
        List<String> files = new ArrayList<>();
        log.info("Listing files of type:{}", extension);
        try {
            Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(extension))
                    .forEach(path -> files.add(path.getFileName().toString()));
        } catch (IOException e) {
            log.error("Exception while listFilenamesForWsdl:{}", String.valueOf(e));
        }
        return files;
    }

    /**
     * processWsdlUrls to process urls.
     * @param jobRequests will have request wsdl urls
     * @return result of generated fileNames
     */
    public Map<String, Map<String, List<String>>> processWsdlUrls
    (final List<WsdlJobRequest> jobRequests) throws IOException {
        Map<String, Map<String, List<String>>> result = new LinkedHashMap<>();
        try {
            for (int i = 0; i < jobRequests.size(); i++) {
                WsdlJobRequest job = jobRequests.get(i);
                Map<String, List<String>> generated = generateFromWsdlUrl(job.getWsdlUrl(), job.getXsdUrls());
                result.put("job_" + (i + 1), generated);
            }
        } catch (IOException e) {
            log.error("Exception while processWsdlUrls:{}", String.valueOf(e));
        }
        return result;
    }

    /**
     * generateFromWsdlUrl to process urls.
     * @param wsdlUrl will have wsdl urls
     * @param xsdUrls will have xsd Urls
     * @return result of generated fileNames
     */
    private Map<String, List<String>> generateFromWsdlUrl
    (final String wsdlUrl, final List<String> xsdUrls) throws IOException {
        Map<String, List<String>> result = new LinkedHashMap<>();
        try {
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

            List<Path> classFilesPaths = getClassFilePaths(outputDir);
            uploadWsdlToDatabaseService.uploadWsdlToDb(wsdlUrl, classFilesPaths);

            result.put("java", listFilenamesForWsdl(outputDir, ".java"));
            result.put("class", listFilenamesForWsdl(outputDir, ".class"));
            return result;
        } catch (IOException e) {
            log.error("Exception while generateFromWsdlUrl:{}", String.valueOf(e));
        }
        return result;

    }

    /**
     * getClassFilePaths to get class file paths.
     * @param outputDir will have wsdl urls
     * @return class file paths
     */
    private List<Path> getClassFilePaths(final Path outputDir) throws IOException {
        log.info("Getting .class files paths");
        return Files.walk(outputDir)
                .filter(path -> path.toString().endsWith(".class"))
                .collect(Collectors.toList());
    }

    /**
     * runWsdlToJava to generate java files.
     * @param cxfArgs will have wsdl urls
     */
    private void runWsdlToJava(final String[] cxfArgs) {
        log.info("Converting wsdl to java");
        WSDLToJava.main(cxfArgs);
    }

    /**
     * downloadFile from url.
     * @param urlStr will have wsdl url
     * @param targetDir will have path to download
     * @return path of downloaded file
     */
    private Path downloadFile(final String urlStr, final Path targetDir) throws IOException {
        log.info("Downloading file from url:{}", urlStr);
        URL url = new URL(urlStr);
        String filename = Paths.get(url.getPath()).getFileName().toString();
        Path targetFile = targetDir.resolve(filename);
        try (InputStream in = url.openStream()) {
            Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return targetFile;
    }
}
