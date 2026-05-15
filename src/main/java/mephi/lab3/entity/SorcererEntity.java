package mephi.lab3.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sorcerers")
public class SorcererEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String rank;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public String getRank(){
        return rank;
    }

    public void setRank(String rank){
        this.rank = rank;
    }

    public MissionEntity getMission(){
        return mission;
    }

    public void setMission(MissionEntity mission){
        this.mission = mission;
    }
}