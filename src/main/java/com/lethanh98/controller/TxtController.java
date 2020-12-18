package com.lethanh98.controller;

import com.lethanh98.annotation.ApiResponsesBase;
import com.lethanh98.utils.NIO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping(value = "/api/txt")
@ApiResponsesBase()
@Slf4j
public class TxtController {
    @ApiOperation(value = "Tìm kiếm các dòng trong file 2 không có trong file 1, rồi trả về tập hợp của 1 và 2")
    @PostMapping(value = "add-data-file-2-not-in-file-1", consumes = {"multipart/form-data"})
    public ResponseEntity<?> get(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) throws IOException {
        try {
            AtomicReference<Set<String>> result1 = new AtomicReference<>();
            AtomicReference<Set<String>> result2 = new AtomicReference<>();

            NIO.runMultipleTaskSync(
                    () -> result1.set(getListStringLineByMultipartFile(file1)),
                    () -> result2.set(getListStringLineByMultipartFile(file2))
            );

            result2.get().forEach(s -> result1.get().add(s));
            StringJoiner result = new StringJoiner("\n");
            result1.get().forEach(result::add);

            HttpHeaders responseHeader = new HttpHeaders();

            byte[] dataByte = result.toString().getBytes();
            // Set mimeType trả về
            responseHeader.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            // Thiết lập thông tin trả về
            responseHeader.set("Content-disposition", "attachment; filename=File1.txt");
            responseHeader.setContentLength(dataByte.length);
            InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(dataByte));
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            return new ResponseEntity<>(inputStreamResource, responseHeader, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", null, HttpStatus.OK);

        }
    }

    private Set<String> getListStringLineByMultipartFile(MultipartFile file1) {
        Set<String> result = new HashSet<>();
        try {
            String line;
            InputStream is = file1.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
}
