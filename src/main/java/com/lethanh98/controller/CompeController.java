package com.lethanh98.controller;

import com.lethanh98.annotation.ApiResponsesBase;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/compe")
@ApiResponsesBase()
@Slf4j
public class CompeController {

    @ApiOperation(value = "The search does not exist .properties")
    @PostMapping(value = "find-not-does", consumes = {"multipart/form-data"})
    public Map<String, Map<String, String>> get(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2) throws IOException {
        Map<String, Map<String, String>> mapResult = new HashMap<>();
        try {

            Properties fileProperties1 = new Properties();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(file1.getInputStream()));
            fileProperties1.load(br1);
            String nameFile1 = file1.getOriginalFilename();
////
            Properties fileProperties2 = new Properties();
            BufferedReader br2 = new BufferedReader(new InputStreamReader(file2.getInputStream()));
            fileProperties2.load(br2);
            String nameFile2 = file2.getOriginalFilename();

            Map<String, String> file1Vs2 = new HashMap<>();

            Set<Object> keysFile1 = fileProperties1.keySet();
            Set<Object> keysFile2 = fileProperties2.keySet();
            keysFile1.forEach(o -> {
                if (!keysFile2.contains(o)) {
                    StringBuilder stringBuilder = new StringBuilder("===== ở File : " + nameFile1);
                    file1Vs2.put(String.valueOf(o), stringBuilder.append("  Không trong file : " + nameFile2).toString());
                }
            });
            Map<String, String> file2Vs1 = new HashMap<>();

            keysFile2.forEach(o -> {
                if (!keysFile1.contains(o)) {
                    StringBuilder stringBuilder = new StringBuilder("===== ở File : " + nameFile2);
                    file2Vs1.put(String.valueOf(o), stringBuilder.append("  Không trong file : " + nameFile1).toString());
                }
            });
            mapResult.put("file1Vs2", file2Vs1);
            mapResult.put("file2Vs1", file2Vs1);
        } catch (Exception e) {
            log.info("error");
        }
        return mapResult;
    }
}
