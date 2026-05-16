package mephi.lab3.dto;

import java.util.List;

public record ImportResponse(String missionId, String sourceFormat, String message, List<String> validationWarnings){
}
