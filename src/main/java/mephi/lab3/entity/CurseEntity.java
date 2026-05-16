package mephi.lab3.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "curses")
public class CurseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "threat_level")
    private String threatLevel;

    @OneToOne
    @JoinColumn(name = "mission_id", nullable = false)
    private MissionEntity mission;

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getThreatLevel(){
        return threatLevel;
    }

    public void setThreatLevel(String threatLevel){
        this.threatLevel = threatLevel;
    }

    public MissionEntity getMission(){
        return mission;
    }

    public void setMission(MissionEntity mission){
        this.mission = mission;
    }
}