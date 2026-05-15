package mephi.lab3.domain;

import java.util.*;

public class MissionExtensionBlock{
    private final String name;
    private final Map<String, String> fields = new LinkedHashMap<>();
    private final List<Map<String, String>> entries = new ArrayList<>();

    public MissionExtensionBlock(String name){
        this.name = name;
    }

    public void addField(String fieldName, String value){
        if (value == null || value.isBlank()){
            return;
        }
        fields.put(fieldName, value);
    }

    public void addEntry(Map<String, String> entry){
        if (entry == null || entry.isEmpty()){
            return;
        }
        entries.add(new LinkedHashMap<>(entry));
    }

    public String getName(){
        return name;
    }

    public Map<String, String> getFields(){
        return fields;
    }

    public List<Map<String, String>> getEntries(){
        return entries;
    }
}