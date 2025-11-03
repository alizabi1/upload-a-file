package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import org.springframework.stereotype.Service;

import static com.example.demo.utils.InputFileIndices.*;

@Service
public class FileValidationService {

    public void validateLine(String[] parts){
        validateUUID(parts[UUID]);
        validateID(parts[ID]);
        validateName(parts[NAME]);
        validateLikes(parts[LIKES]);
        validateTransport(parts[TRANSPORT]);
        validateSpeed(parts[AVG_SPEED], "Incorrect average speed");
        validateSpeed(parts[TOP_SPEED], "Incorrect top speed");
    }

    private void validateUUID(String uuid){
        try{
            java.util.UUID.fromString(uuid);
        } catch (RuntimeException e){
            throw new BadRequestException("Invalid UUID field", e);
        }
    }

    private void validateID(String id){
        if (! id.matches("^[A-Z0-9]{6}$")){
            throw new BadRequestException("Invalid ID field");
        }
    }

    private void validateName(String name){
        if (! name.matches("^[A-Za-z '-]{1,100}$")){
            throw new BadRequestException("Invalid name field");
        }
    }

    private void validateLikes(String likes){
        if (! likes.matches("(?U)^[\\p{Print}]{1,100}$")){
            throw new BadRequestException("Invalid likes field");
        }
    }

    private void validateTransport(String transport){
        if (! transport.matches("^[\\p{Print}]{1,100}$")){
            throw new BadRequestException("Invalid transport field");
        }
    }

    private void validateSpeed(String speed, String message){
        try {
            Float.valueOf(speed);
        } catch (NumberFormatException e) {
            throw new BadRequestException(message, e);
        }
    }

}
