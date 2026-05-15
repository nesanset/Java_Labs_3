package mephi.lab3.parsing.parsers;//норм вроде

import java.util.*;
import mephi.lab3.assembly.MissionBuilder;
import mephi.lab3.domain.*;
import mephi.lab3.parsing.MissionParser;
import mephi.lab3.parsing.MissionParseException;

public class TextMissionParser implements MissionParser {
    @Override
    public void parse(String content, MissionBuilder missionBuilder) throws MissionParseException {
        try {
            Map<String, String> values = new LinkedHashMap<>();
            Map<Integer, Sorcerer> sorcerersByIndex = new TreeMap<>();
            Map<Integer, Technique> techniquesByIndex = new TreeMap<>();

            for (String rawLine : content.split("\\R")) {
                String line = rawLine.trim();
                if (line.isEmpty()) {
                    continue;
                }

                int separatorIndex = line.indexOf(':');
                if (separatorIndex < 0) {
                    throw new MissionParseException("Не удалось прочитать текстовый формат миссии");
                }

                String key = line.substring(0, separatorIndex).trim();
                String value = line.substring(separatorIndex + 1).trim();

                if (key.startsWith("sorcerer[")) {
                    readSorcererField(sorcerersByIndex, key, value);
                } else if (key.startsWith("technique[")) {
                    readTechniqueField(techniquesByIndex, key, value);
                } else {
                    values.put(key, value);
                }
            }

            missionBuilder.setMissionId(values.get("missionId")).setDate(values.get("date")).setLocation(values.get("location")).setOutcome(values.get("outcome")).setDamageCost(values.get("damageCost")).setNote(firstNonBlank(values.get("note"), values.get("comment")));
            missionBuilder.setCurse(values.get("curse.name"), values.get("curse.threatLevel"));

            List<Sorcerer> participants = new ArrayList<>(sorcerersByIndex.values());
            for (Sorcerer participant : participants) {
                missionBuilder.addParticipant(participant);
            }

            List<Technique> techniques = new ArrayList<>(techniquesByIndex.values());
            for (Technique technique : techniques) {
                missionBuilder.addTechnique(technique);
            }

            for (Map.Entry<String, String> valueEntry : values.entrySet()) {
                String key = valueEntry.getKey();
                String value = valueEntry.getValue();

                if (!isKnownMissionField(key)) {
                    addExtensionField(missionBuilder, key, value);
                }
            }
        } catch (MissionParseException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new MissionParseException("Не удалось прочитать TXT");
        }
    }

    private void readSorcererField(Map<Integer, Sorcerer> sorcerersByIndex, String key, String value) {
        int index = readIndex(key);
        String field = readFieldName(key);
        Sorcerer sorcerer = getSorcerer(sorcerersByIndex, index);

        if ("name".equals(field)) {
            sorcerer.setName(value);
        } else if ("rank".equals(field)) {
            sorcerer.setRank(value);
        }
    }

    private void readTechniqueField(Map<Integer, Technique> techniquesByIndex, String key, String value) {
        int index = readIndex(key);
        String field = readFieldName(key);
        Technique technique = getTechnique(techniquesByIndex, index);

        if ("name".equals(field)) {
            technique.setName(value);
        } else if ("owner".equals(field)) {
            technique.setOwner(value);
        } else if ("type".equals(field)) {
            technique.setType(value);
        } else if ("damage".equals(field)) {
            technique.setDamage(value);
        }
    }

    private Sorcerer getSorcerer(Map<Integer, Sorcerer> sorcerersByIndex, int index) {
        Sorcerer sorcerer = sorcerersByIndex.get(index);
        if (sorcerer == null) {
            sorcerer = new Sorcerer();
            sorcerersByIndex.put(index, sorcerer);
        }
        return sorcerer;
    }

    private Technique getTechnique(Map<Integer, Technique> techniquesByIndex, int index) {
        Technique technique = techniquesByIndex.get(index);
        if (technique == null) {
            technique = new Technique();
            techniquesByIndex.put(index, technique);
        }
        return technique;
    }

    private int readIndex(String key) {
        int openBracketIndex = key.indexOf('[');
        int closeBracketIndex = key.indexOf(']');
        if (openBracketIndex < 0 || closeBracketIndex < 0 || closeBracketIndex <= openBracketIndex + 1) {
            throw new IllegalArgumentException("Некорректный индекс поля");
        }
        return Integer.parseInt(key.substring(openBracketIndex + 1, closeBracketIndex));
    }

    private String readFieldName(String key) {
        int dotIndex = key.indexOf('.');
        if (dotIndex < 0 || dotIndex == key.length() - 1) {
            throw new IllegalArgumentException("Некорректное имя поля");
        }
        return key.substring(dotIndex + 1);
    }

    private boolean isKnownMissionField(String key) {
        return "missionId".equals(key) || "date".equals(key) || "location".equals(key) || "outcome".equals(key) || "damageCost".equals(key) || "note".equals(key) || "comment".equals(key) || "curse.name".equals(key) || "curse.threatLevel".equals(key);
    }

    private void addExtensionField(MissionBuilder missionBuilder, String key, String value) {
        if (!key.contains(".")) {
            missionBuilder.addExtensionField("meta", key, value);
            return;
        }

        String blockName = key.substring(0, key.indexOf('.'));
        String fieldName = key.substring(key.indexOf('.') + 1);
        missionBuilder.addExtensionField(blockName, fieldName, value);
    }

    private String firstNonBlank(String firstValue, String secondValue) {
        if (firstValue != null && !firstValue.isBlank()) {
            return firstValue;
        }
        return secondValue;
    }
}