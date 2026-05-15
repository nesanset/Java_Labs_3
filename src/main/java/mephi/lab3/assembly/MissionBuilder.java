package mephi.lab3.assembly;

import java.util.*;
import mephi.lab3.domain.*;

public class MissionBuilder{
    private String missionId;
    private String date;
    private String location;
    private String outcome;
    private String damageCost;
    private String note;
    private Curse curse;
    private final List<Sorcerer> participants = new ArrayList<>();
    private final List<Technique> techniques = new ArrayList<>();
    private final Map<String, MissionExtensionBlock> extensionBlocks = new LinkedHashMap<>();

    public MissionBuilder setMissionId(String missionId){
        this.missionId = missionId;
        return this;
    }

    public MissionBuilder setDate(String date){
        this.date = date;
        return this;
    }

    public MissionBuilder setLocation(String location){
        this.location = location;
        return this;
    }

    public MissionBuilder setOutcome(String outcome){
        this.outcome = outcome;
        return this;
    }

    public MissionBuilder setDamageCost(String damageCost){
        this.damageCost = damageCost;
        return this;
    }

    public MissionBuilder setCurse(String name, String threatLevel){
        if ((name == null || name.isBlank()) && (threatLevel == null || threatLevel.isBlank())) {
            return this;
        }
        if (curse == null){
            curse = new Curse(null, null);
        }
        if (name != null && !name.isBlank()) {
            curse.setName(name);
        }
        if (threatLevel != null && !threatLevel.isBlank()) {
            curse.setThreatLevel(threatLevel);
        }
        return this;
    }

    public MissionBuilder setNote(String note){
        this.note = note;
        return this;
    }

    public MissionBuilder addParticipant(Sorcerer participant){
        if (participant != null) {
            participants.add(participant);
        }
        return this;
    }

    public MissionBuilder addTechnique(Technique technique){
        if (technique != null) {
            techniques.add(technique);
        }
        return this;
    }

    public MissionBuilder addExtensionField(String blockName, String fieldName, String value){
        getOrCreateExtensionBlock(blockName).addField(fieldName, value);
        return this;
    }

    public MissionBuilder addExtensionEntry(String blockName, Map<String, String> entry){
        getOrCreateExtensionBlock(blockName).addEntry(entry);
        return this;
    }

    public Mission build(){
        return new Mission(missionId, date, location, outcome, damageCost, participants, curse, techniques, note, extensionBlocks);
    }

    private MissionExtensionBlock getOrCreateExtensionBlock(String blockName){
        MissionExtensionBlock block = extensionBlocks.get(blockName);
        if (block == null){
            block = new MissionExtensionBlock(blockName);
            extensionBlocks.put(blockName, block);
        }
        return block;
    }
}