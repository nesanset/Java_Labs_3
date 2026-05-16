package mephi.lab3.dto;

import java.util.List;
import java.util.Map;

public record ExtensionBlockDto(String name, Map<String, String> fields, List<Map<String, String>> entries){
}
