package mephi.lab3.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "missions")
public class MissionEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_id", nullable = false, unique = true)
    private String missionId;

    @Column(name = "mission_date")
    private String date;

    private String location;
    private String outcome;

    @Column(name = "damage_cost")
    private String damageCost;

    @Column(length = 2000)
    private String note;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "source_format")
    private String sourceFormat;

    @Column(name = "extension_data", length = 4000)
    private String extensionData;

    @OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private CurseEntity curse;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SorcererEntity> participants = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TechniqueEntity> techniques = new ArrayList<>();

    public String getMissionId(){
        return missionId;
    }

    public void setMissionId(String missionId){
        this.missionId = missionId;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getOutcome(){
        return outcome;
    }

    public void setOutcome(String outcome){
        this.outcome = outcome;
    }

    public String getDamageCost(){
        return damageCost;
    }

    public void setDamageCost(String damageCost){
        this.damageCost = damageCost;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getSourceName(){
        return sourceName;
    }

    public void setSourceName(String sourceName){
        this.sourceName = sourceName;
    }

    public String getSourceFormat(){
        return sourceFormat;
    }

    public void setSourceFormat(String sourceFormat){
        this.sourceFormat = sourceFormat;
    }

    public String getExtensionData(){
        return extensionData;
    }

    public void setExtensionData(String extensionData){
        this.extensionData = extensionData;
    }

    public CurseEntity getCurse(){
        return curse;
    }

    public void setCurse(CurseEntity curse){
        if (curse != null){
            curse.setMission(this);
        }
        this.curse = curse;
    }

    public List<SorcererEntity> getParticipants(){
        return participants;
    }

    public void setParticipants(List<SorcererEntity> participants){
        this.participants.clear();
        if (participants == null){
            return;
        }
        for (SorcererEntity participant : participants){
            participant.setMission(this);
            this.participants.add(participant);
        }
    }

    public List<TechniqueEntity> getTechniques(){
        return techniques;
    }

    public void setTechniques(List<TechniqueEntity> techniques){
        this.techniques.clear();
        if (techniques == null){
            return;
        }
        for (TechniqueEntity technique : techniques){
            technique.setMission(this);
            this.techniques.add(technique);
        }
    }
}