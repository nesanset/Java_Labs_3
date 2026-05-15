package mephi.lab3.domain;

import java.util.*;

public class Mission{
    private final String missionId;
    private final String date;
    private final String location;
    private final String outcome;
    private final String damageCost;
    private final List<Sorcerer> participants;
    private final Curse curse;
    private final List<Technique> techniques;
    private final String note;
    private final Map<String, MissionExtensionBlock> extensionBlocks;

    public Mission(String missionId, String date, String location, String outcome, String damageCost, List<Sorcerer> participants, Curse curse, List<Technique> techniques, String note){
        this(missionId, date, location, outcome, damageCost, participants, curse, techniques, note, Map.of());
    }

    public Mission(String missionId, String date, String location, String outcome, String damageCost, List<Sorcerer> participants, Curse curse, List<Technique> techniques, String note, Map<String, MissionExtensionBlock> extensionBlocks){
        this.missionId = missionId;
        this.date = date;
        this.location = location;
        this.outcome = outcome;
        this.damageCost = damageCost;
        this.participants = participants;
        this.curse = curse;
        this.techniques = techniques;
        this.note = note;
        this.extensionBlocks = extensionBlocks;
    }

    public String getMissionId(){
        return missionId;
    }

    public String getDate(){
        return date;
    }

    public String getLocation(){
        return location;
    }

    public String getOutcome(){
        return outcome;
    }

    public String getDamageCost(){
        return damageCost;
    }

    public List<Sorcerer> getParticipants(){
        return participants;
    }

    public Curse getCurse(){
        return curse;
    }

    public List<Technique> getTechniques(){
        return techniques;
    }

    public String getNote(){
        return note;
    }

    public Map<String, MissionExtensionBlock> getExtensionBlocks(){
        return extensionBlocks;
    }
}