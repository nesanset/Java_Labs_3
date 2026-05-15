package mephi.lab3.domain;

public class Technique{
    private String name;
    private String owner;
    private String type;
    private String damage;

    public Technique(){
    }

    public Technique(String name, String owner, String type,String damage){
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.damage = damage;
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
}