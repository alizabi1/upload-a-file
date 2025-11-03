package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import static com.example.demo.Constants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidationServiceTest {

    private final FileValidationService validator = new FileValidationService();

    @Test
    public void shouldValidateCorrectLine() {
        validator.validateLine(lineParts);
    }

    @Test
    public void shouldThrowExceptionIncorrectUuid() {
        final String[] lineParts = {"18148426-rrrr-11ee-b9d1-0242ac120002", correctID, correctName, correctLikes,
                correctTransport, correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid UUID field\"");
    }

    @Test
    public void shouldThrowException_idWithSlash() {
        final String[] lineParts = {correctUUID, "1X/D14", correctName, correctLikes,
                correctTransport, correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid ID field\"");
    }

    @Test
    public void shouldThrowException_idTooLong() {
        final String[] lineParts = {correctUUID, "1XDDDD14", correctName, correctLikes,
                correctTransport, correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid ID field\"");
    }

    @Test
    public void shouldThrowException_NameWithNumbers() {
        final String[] lineParts = {correctUUID, correctID, "Name with numbers 880", correctLikes,
                correctTransport, correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid name field\"");
    }

    @Test
    public void shouldThrowException_NameWithCharacterNotAllowed() {
        final String[] lineParts = {correctUUID, correctID, "John O'&Connor", correctLikes,
                correctTransport, correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid name field\"");
    }

    @Test
    public void shouldThrowException_NameTooLong() {
        final String[] lineParts = {correctUUID, correctID,
                "JohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnConnorJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnConnor",
                correctLikes,
                correctTransport, correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid name field\"");
    }

    @Test
    public void shouldThrowException_likesTooLong() {
        final String[] lineParts = {correctUUID, correctID,
                correctName,
                "long likes long likes long likes long likes long likes long likes long likes long likes long likes long likes long likes ",
                correctTransport, correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid likes field\"");
    }

    @Test
    public void shouldThrowException_transportTooLong() {
        final String[] lineParts = {correctUUID, correctID,
                correctName, correctLikes,
                "long transport long transport long transport long transport long transport long transport long transport long transport long ",
                correctAvgSpeed, correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Invalid transport field\"");
    }

    @Test
    public void shouldThrowException_averageSpeedIncorrect() {
        final String[] lineParts = {correctUUID, correctID, correctName, correctLikes,
                correctTransport, "No speed", correctTopSpeed};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Incorrect average speed\"");
    }

    @Test
    public void shouldThrowException_topSpeedIncorrect() {
        final String[] lineParts = {correctUUID, correctID, correctName, correctLikes,
                correctTransport, correctAvgSpeed, "&80.8"};

        assertThatThrownBy(() -> validator.validateLine(lineParts))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("400 BAD_REQUEST \"Incorrect top speed\"");
    }

}