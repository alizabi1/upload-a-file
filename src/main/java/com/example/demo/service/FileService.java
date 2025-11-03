package com.example.demo.service;

import com.example.demo.exception.ParseFileException;
import com.example.demo.model.OutputLine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.utils.InputFileIndices.*;

@Service
public class FileService {

    private ObjectMapper mapper;
    private FileValidationService validator;

    @Autowired
    public FileService(ObjectMapper mapper, FileValidationService validator) {
        this.mapper = mapper;
        this.validator = validator;
    }

    public String parseFile(MultipartFile file, boolean shouldValidate) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            List<OutputLine> outputLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if(shouldValidate) {
                    validator.validateLine(parts);
                }
                outputLines.add(new OutputLine(parts[NAME], parts[TRANSPORT], parts[TOP_SPEED]));
            }
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(outputLines);

        } catch (IOException e) {
            throw new ParseFileException("Unable to parse the file", e);
        }

    }

}
