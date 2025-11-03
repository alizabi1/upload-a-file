package com.example.demo.service;

import com.example.demo.exception.ParseFileException;
import com.example.demo.model.OutputLine;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.example.demo.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class FileServiceTest {

    private FileService fileService;

    @Mock
    private FileValidationService validator;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ObjectWriter writer;

    @Mock
    private MultipartFile file;

    @BeforeEach
    public void init() {
        fileService = new FileService(mapper, validator);
    }

    @Test
    public void shouldExecuteParseFileAndProduceJsonFile() throws IOException {
        List<OutputLine> lines = List.of(new OutputLine(correctName, correctTransport, correctTopSpeed));

        when(file.getInputStream()).thenReturn(getCorrectFileContentStream());
        when(mapper.writerWithDefaultPrettyPrinter()).thenReturn(writer);

        ArgumentCaptor<String[]> linePartsCaptor = ArgumentCaptor.forClass(String[].class);
        ArgumentCaptor<?> outputLinesCaptor = ArgumentCaptor.forClass(List.class);

        fileService.parseFile(file, true);

        verify(validator).validateLine(linePartsCaptor.capture());
        verify(mapper).writerWithDefaultPrettyPrinter();
        verify(writer).writeValueAsString(outputLinesCaptor.capture());

        assertThat(lineParts).isEqualTo(linePartsCaptor.getValue());
        assertThat(lines).isEqualTo(outputLinesCaptor.getValue());
    }

    @Test
    public void shouldNotValidateFileIfFlagEnabled() throws IOException {
        List<OutputLine> lines = List.of(new OutputLine(correctName, correctTransport, correctTopSpeed));
        when(file.getInputStream()).thenReturn(getCorrectFileContentStream());
        when(mapper.writerWithDefaultPrettyPrinter()).thenReturn(writer);

        ArgumentCaptor<?> outputLinesCaptor = ArgumentCaptor.forClass(List.class);

        fileService.parseFile(file, false);

        verifyNoInteractions(validator);
        verify(mapper).writerWithDefaultPrettyPrinter();
        verify(writer).writeValueAsString(outputLinesCaptor.capture());

        assertThat(lines).isEqualTo(outputLinesCaptor.getValue());
    }

    @Test
    public void shouldThrowAn5xxErrorIfUnableToReadStream() throws IOException {
        when(file.getInputStream()).thenThrow(IOException.class);
        assertThatThrownBy(() -> fileService.parseFile(file, true))
                .isInstanceOf(ParseFileException.class);
    }

    @Test
    public void shouldThrowAn5xxErrorIfUnableToProduceAFile() throws IOException {
        when(file.getInputStream()).thenReturn(getCorrectFileContentStream());
        when(mapper.writerWithDefaultPrettyPrinter()).thenReturn(writer);

        doThrow(JsonMappingException.fromUnexpectedIOE(new IOException()))
                .when(writer)
                .writeValueAsString(any());

        assertThatThrownBy(() -> fileService.parseFile(file, true))
                .isInstanceOf(ParseFileException.class);
    }

    private InputStream getCorrectFileContentStream() {
        return new ByteArrayInputStream(String.join("|", lineParts).getBytes());
    }

}