package mephi.lab3.parsing.parsers;

import java.util.*;
import mephi.lab3.assembly.MissionBuilder;
import mephi.lab3.domain.*;
import mephi.lab3.parsing.*;

public class EventLogMissionParser implements MissionParser{
    @Override
    public void parse(String content, MissionBuilder missionBuilder) throws MissionParseException{
        try{
            for (String rawLine : content.split("\\R")) {
                String trimmed = rawLine.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }

                String[] parts = trimmed.split("\\|");
                switch (parts[0]) {
                    case "MISSION_CREATED" -> {
                        missionBuilder.setMissionId(readPart(parts, 1));
                        missionBuilder.setDate(readPart(parts, 2));
                        missionBuilder.setLocation(readPart(parts, 3));
                    }
                    case "CURSE_DETECTED" -> missionBuilder.setCurse(readPart(parts, 1), readPart(parts, 2));
                    case "SORCERER_ASSIGNED" -> missionBuilder.addParticipant(new Sorcerer(readPart(parts, 1), readPart(parts, 2)));
                    case "TECHNIQUE_USED" -> missionBuilder.addTechnique(new Technique(readPart(parts, 1), readPart(parts, 3), readPart(parts, 2), readPart(parts, 4)));
                    case "TIMELINE_EVENT" -> missionBuilder.addExtensionEntry("timeline", mapOf("timestamp", readPart(parts, 1), "type", readPart(parts, 2), "description", readPart(parts, 3)));
                    case "ENEMY_ACTION" -> missionBuilder.addExtensionEntry("enemyActivity", mapOf("action", readPart(parts, 1), "description", readPart(parts, 2)));
                    case "CIVILIAN_IMPACT" -> addCivilianImpactFields(parts, missionBuilder);
                    case "MISSION_RESULT" -> {
                        missionBuilder.setOutcome(readPart(parts, 1));
                        for (int index = 2; index < parts.length; index++) {
                            if (parts[index].startsWith("damageCost=")) {
                                missionBuilder.setDamageCost(parts[index].substring("damageCost=".length()));
                            }
                        }
                    }
                    default -> missionBuilder.addExtensionField("meta", "event", trimmed);
                }
            }
        }catch (Exception exception){
            throw new MissionParseException("Не удалось прочитать event log");
        }
    }

    private void addCivilianImpactFields(String[] parts, MissionBuilder missionBuilder) {
        for (int index = 1; index < parts.length; index++) {
            String token = parts[index];
            if (!token.contains("=")) {
                continue;
            }
            String key = token.substring(0, token.indexOf('='));
            String value = token.substring(token.indexOf('=') + 1);
            missionBuilder.addExtensionField("civilianImpact", key, value);
        }
    }

    private Map<String, String> mapOf(String firstKey, String firstValue, String secondKey, String secondValue) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put(firstKey, firstValue);
        values.put(secondKey, secondValue);
        return values;
    }

    private Map<String, String> mapOf(String firstKey, String firstValue, String secondKey, String secondValue, String thirdKey, String thirdValue) {
        Map<String, String> values = mapOf(firstKey, firstValue, secondKey, secondValue);
        values.put(thirdKey, thirdValue);
        return values;
    }

    private String readPart(String[] parts, int index) {
        if (index >= parts.length) {
            return null;
        }
        return parts[index];
    }
}