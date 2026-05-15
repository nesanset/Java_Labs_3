package mephi.lab3.parsing.parsers;

import java.util.*;
import mephi.lab3.assembly.MissionBuilder;
import mephi.lab3.domain.*;
import mephi.lab3.parsing.*;

public class IniMissionParser implements MissionParser{
    @Override
    public void parse(String content, MissionBuilder missionBuilder) throws MissionParseException{
        try{
            String currentSection = null;
            Sorcerer currentSorcerer = null;
            Technique currentTechnique = null;

            for (String rawLine : content.split("\\R")) {
                String trimmed = rawLine.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                    saveCurrentSorcerer(currentSection, currentSorcerer, missionBuilder);
                    saveCurrentTechnique(currentSection, currentTechnique, missionBuilder);
                    currentSection = trimmed.substring(1, trimmed.length() - 1).trim().toUpperCase(Locale.ROOT);
                    currentSorcerer = null;
                    currentTechnique = null;
                    if ("SORCERER".equals(currentSection)) {
                        currentSorcerer = new Sorcerer();
                    } else if ("TECHNIQUE".equals(currentSection)) {
                        currentTechnique = new Technique();
                    }
                    continue;
                }

                if (currentSection == null || !trimmed.contains("=")) {
                    continue;
                }
                String key = trimmed.substring(0, trimmed.indexOf('=')).trim();
                String value = trimmed.substring(trimmed.indexOf('=') + 1).trim();
                switch (currentSection) {
                    case "MISSION" -> setMissionField(missionBuilder, key, value);
                    case "CURSE" -> setCurseField(missionBuilder, key, value);
                    case "SORCERER" -> setSorcererField(currentSorcerer, key, value);
                    case "TECHNIQUE" -> setTechniqueField(currentTechnique, key, value);
                    default -> missionBuilder.addExtensionField(currentSection.toLowerCase(Locale.ROOT), key, value);
                }
            }
            saveCurrentSorcerer(currentSection, currentSorcerer, missionBuilder);
            saveCurrentTechnique(currentSection, currentTechnique, missionBuilder);
        }catch (Exception exception){
            throw new MissionParseException("Не удалось прочитать INI/TXT");
        }
    }

    private void saveCurrentSorcerer(String currentSection, Sorcerer currentSorcerer, MissionBuilder missionBuilder){
        if (!"SORCERER".equals(currentSection) || currentSorcerer == null){
            return;
        }
        missionBuilder.addParticipant(currentSorcerer);
    }

    private void saveCurrentTechnique(String currentSection, Technique currentTechnique, MissionBuilder missionBuilder) {
        if (!"TECHNIQUE".equals(currentSection) || currentTechnique == null){
            return;
        }
        missionBuilder.addTechnique(currentTechnique);
    }

    private void setCurseField(MissionBuilder missionBuilder, String key, String value){
        if ("name".equals(key)) {
            missionBuilder.setCurse(value, null);
        } else if ("threatLevel".equals(key)) {
            missionBuilder.setCurse(null, value);
        }
    }

    private void setMissionField(MissionBuilder missionBuilder, String key, String value){
        if ("missionId".equals(key)){
            missionBuilder.setMissionId(value);
        } else if ("date".equals(key)){
            missionBuilder.setDate(value);
        } else if ("location".equals(key)){
            missionBuilder.setLocation(value);
        } else if ("outcome".equals(key)){
            missionBuilder.setOutcome(value);
        } else if ("damageCost".equals(key)){
            missionBuilder.setDamageCost(value);
        } else if ("note".equals(key) || "comment".equals(key)){
            missionBuilder.setNote(value);
        }
    }

    private void setSorcererField(Sorcerer currentSorcerer, String key, String value){
        if (currentSorcerer == null){
            return;
        }
        if ("name".equals(key)){
            currentSorcerer.setName(value);
        } else if ("rank".equals(key)){
            currentSorcerer.setRank(value);
        }
    }

    private void setTechniqueField(Technique currentTechnique, String key, String value){
        if (currentTechnique == null){
            return;
        }
        if ("name".equals(key)){
            currentTechnique.setName(value);
        } else if ("owner".equals(key)){
            currentTechnique.setOwner(value);
        } else if ("type".equals(key)){
            currentTechnique.setType(value);
        } else if ("damage".equals(key)){
            currentTechnique.setDamage(value);
        }
    }
}