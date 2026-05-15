package mephi.lab3.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "techniques")
public class TechniqueEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String owner;
    private String type;
    private String damage;

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

    public String getOwner(){
        return owner;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getDamage(){
        return damage;
    }

    public void setDamage(String damage){
        this.damage = damage;
    }

    public MissionEntity getMission(){
        return mission;
    }

    public void setMission(MissionEntity mission){
        this.mission = mission;
    }
}