package mephi.lab3.parsing.parsers;//разобрать

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import mephi.lab3.assembly.*;
import mephi.lab3.domain.*;
import mephi.lab3.parsing.*;

public class JsonMissionParser implements MissionParser{
    private static final Set<String> KNOWN_FIELDS = Set.of("missionId", "date", "location", "outcome", "damageCost", "note", "comment", "curse", "sorcerers", "techniques");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void parse(String content, MissionBuilder missionBuilder) throws MissionParseException{
        try{
            JsonNode root = objectMapper.readTree(content);
            readBaseFields(root, missionBuilder);
            readCurse(root.path("curse"), missionBuilder);
            readSorcerers(root.path("sorcerers"), missionBuilder);
            readTechniques(root.path("techniques"), missionBuilder);
            readExtensions(root, missionBuilder);
        }catch (Exception exception){
            throw new MissionParseException("Не удалось прочитать JSON");
        }
    }

    private void readBaseFields(JsonNode root, MissionBuilder missionBuilder){
        missionBuilder.setMissionId(readText(root, "missionId")).setDate(readText(root, "date")).setLocation(readText(root, "location")).setOutcome(readText(root, "outcome")).setDamageCost(readText(root, "damageCost")).setNote(firstNonBlank(readText(root, "note"), readText(root, "comment")));
    }

    private void readCurse(JsonNode curseNode, MissionBuilder missionBuilder){
        if (curseNode == null || curseNode.isNull()){
            return;
        }
        missionBuilder.setCurse(readText(curseNode, "name"), readText(curseNode, "threatLevel"));
    }

    private void readSorcerers(JsonNode sorcerersNode, MissionBuilder missionBuilder){
        if (!sorcerersNode.isArray()){
            return;
        }
        for (JsonNode sorcererNode : sorcerersNode){
            missionBuilder.addParticipant(new Sorcerer(readText(sorcererNode, "name"), readText(sorcererNode, "rank")));
        }
    }

    private void readTechniques(JsonNode techniquesNode, MissionBuilder missionBuilder){
        if (!techniquesNode.isArray()){
            return;
        }
        for (JsonNode techniqueNode : techniquesNode){
            missionBuilder.addTechnique(new Technique(readText(techniqueNode, "name"), readText(techniqueNode, "owner"), readText(techniqueNode, "type"), readText(techniqueNode, "damage")));
        }
    }

    private void readExtensions(JsonNode root, MissionBuilder missionBuilder){
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()){
            Map.Entry<String, JsonNode> field = fields.next();
            if (!KNOWN_FIELDS.contains(field.getKey())){
                addExtensionBlock(field.getKey(), field.getValue(), missionBuilder);
            }
        }
    }

    private void addExtensionBlock(String blockName, JsonNode node, MissionBuilder missionBuilder){
        if (node == null || node.isNull()){
            return;
        }
        if (!node.isObject()){
            missionBuilder.addExtensionField(blockName, "value", node.toString());
            return;
        }

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()){
            Map.Entry<String, JsonNode> field = fields.next();
            missionBuilder.addExtensionField(blockName, field.getKey(), field.getValue().asText());
        }
    }

    private String readText(JsonNode node, String fieldName){
        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()){
            return null;
        }
        return value.asText();
    }

    private String firstNonBlank(String firstValue, String secondValue){
        if (firstValue != null && !firstValue.isBlank()){
            return firstValue;
        }
        return secondValue;
    }
}