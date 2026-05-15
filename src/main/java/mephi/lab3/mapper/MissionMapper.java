package mephi.lab3.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import mephi.lab3.app.LoadedMission;
import mephi.lab3.domain.*;
import mephi.lab3.entity.*;
import mephi.lab3.parsing.FileFormat;
import mephi.lab3.validation.ValidationResult;
import org.springframework.stereotype.Component;

@Component
public class MissionMapper{
    private final ObjectMapper objectMapper;

    public MissionMapper(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public MissionEntity toEntity(LoadedMission loadedMission){
        MissionEntity entity = toEntity(loadedMission.getMission());
        entity.setSourceName(loadedMission.getWayToFile());
        entity.setSourceFormat(loadedMission.getFormat().name());
        return entity;
    }

    public void updateEntity(MissionEntity entity, LoadedMission loadedMission){
        copyMissionFields(loadedMission.getMission(), entity);
        entity.setSourceName(loadedMission.getWayToFile());
        entity.setSourceFormat(loadedMission.getFormat().name());
    }

    public MissionEntity toEntity(Mission mission){
        MissionEntity entity = new MissionEntity();
        copyMissionFields(mission, entity);
        return entity;
    }

    public LoadedMission toLoadedMission(MissionEntity entity){
        return new LoadedMission(entity.getSourceName(), readFileFormat(entity.getSourceFormat()), toDomain(entity), new ValidationResult());
    }

    public Mission toDomain(MissionEntity entity){
        return new Mission(entity.getMissionId(), entity.getDate(), entity.getLocation(), entity.getOutcome(), entity.getDamageCost(), toDomainSorcerers(entity.getParticipants()), toDomainCurse(entity.getCurse()), toDomainTechniques(entity.getTechniques()), entity.getNote(), readExtensions(entity.getExtensionData()));
    }

    private void copyMissionFields(Mission mission, MissionEntity entity){
        entity.setMissionId(mission.getMissionId());
        entity.setDate(mission.getDate());
        entity.setLocation(mission.getLocation());
        entity.setOutcome(mission.getOutcome());
        entity.setDamageCost(mission.getDamageCost());
        entity.setNote(mission.getNote());
        entity.setCurse(toCurseEntity(mission.getCurse()));
        entity.setParticipants(toSorcererEntitiesFromDomain(mission.getParticipants()));
        entity.setTechniques(toTechniqueEntitiesFromDomain(mission.getTechniques()));
        entity.setExtensionData(writeExtensions(mission.getExtensionBlocks()));
    }

    private CurseEntity toCurseEntity(Curse curse){
        if (curse == null){
            return null;
        }
        CurseEntity entity = new CurseEntity();
        entity.setName(curse.getName());
        entity.setThreatLevel(curse.getThreatLevel());
        return entity;
    }

    private Curse toDomainCurse(CurseEntity entity){
        if (entity == null){
            return null;
        }
        return new Curse(entity.getName(), entity.getThreatLevel());
    }

    private List<SorcererEntity> toSorcererEntitiesFromDomain(List<Sorcerer> sorcerers){
        List<SorcererEntity> entities = new ArrayList<>();
        if (sorcerers == null){
            return entities;
        }
        for (Sorcerer sorcerer : sorcerers){
            SorcererEntity entity = new SorcererEntity();
            entity.setName(sorcerer.getName());
            entity.setRank(sorcerer.getRank());
            entities.add(entity);
        }
        return entities;
    }

    private List<Sorcerer> toDomainSorcerers(List<SorcererEntity> entities){
        List<Sorcerer> sorcerers = new ArrayList<>();
        for (SorcererEntity entity : entities){
            sorcerers.add(new Sorcerer(entity.getName(), entity.getRank()));
        }
        return sorcerers;
    }

    private List<TechniqueEntity> toTechniqueEntitiesFromDomain(List<Technique> techniques){
        List<TechniqueEntity> entities = new ArrayList<>();
        if (techniques == null){
            return entities;
        }
        for (Technique technique : techniques){
            TechniqueEntity entity = new TechniqueEntity();
            entity.setName(technique.getName());
            entity.setOwner(technique.getOwner());
            entity.setType(technique.getType());
            entity.setDamage(technique.getDamage());
            entities.add(entity);
        }
        return entities;
    }

    private List<Technique> toDomainTechniques(List<TechniqueEntity> entities){
        List<Technique> techniques = new ArrayList<>();
        for (TechniqueEntity entity : entities){
            techniques.add(new Technique(entity.getName(), entity.getOwner(), entity.getType(), entity.getDamage()));
        }
        return techniques;
    }

    private String writeExtensions(Map<String, MissionExtensionBlock> extensions){
        if (extensions == null || extensions.isEmpty()){
            return "{}";
        }
        try{
            return objectMapper.writeValueAsString(extensions);
        }catch (JsonProcessingException exception){
            throw new IllegalArgumentException("Не удалось сохранить дополнительные поля миссии", exception);
        }
    }

    private Map<String, MissionExtensionBlock> readExtensions(String extensionData){
        Map<String, MissionExtensionBlock> extensionBlocks = new LinkedHashMap<>();
        if (extensionData == null || extensionData.isBlank()){
            return extensionBlocks;
        }
        try{
            JsonNode root = objectMapper.readTree(extensionData);
            for (Map.Entry<String, JsonNode> blockEntry : root.properties()){
                MissionExtensionBlock block = readExtensionBlock(blockEntry.getKey(), blockEntry.getValue());
                extensionBlocks.put(blockEntry.getKey(), block);
            }
        }catch (JsonProcessingException exception){
            return extensionBlocks;
        }
        return extensionBlocks;
    }

    private MissionExtensionBlock readExtensionBlock(String blockName, JsonNode rawBlock){
        MissionExtensionBlock block = new MissionExtensionBlock(blockName);
        readExtensionFields(rawBlock.path("fields"), block);
        readExtensionEntries(rawBlock.path("entries"), block);
        return block;
    }

    private void readExtensionFields(JsonNode fields, MissionExtensionBlock block){
        if (!fields.isObject()){
            return;
        }
        for (Map.Entry<String, JsonNode> field : fields.properties()){
            if (!field.getValue().isNull()){
                block.addField(field.getKey(), field.getValue().asText());
            }
        }
    }

    private void readExtensionEntries(JsonNode entries, MissionExtensionBlock block){
        if (!entries.isArray()){
            return;
        }
        for (JsonNode entry : entries){
            if (!entry.isObject()){
                continue;
            }
            Map<String, String> normalizedEntry = new LinkedHashMap<>();
            for (Map.Entry<String, JsonNode> item : entry.properties()){
                String value = "";
                if (!item.getValue().isNull()){
                    value = item.getValue().asText();
                }
                normalizedEntry.put(item.getKey(), value);
            }
            block.addEntry(normalizedEntry);
        }
    }

    private FileFormat readFileFormat(String sourceFormat){
        if (sourceFormat == null || sourceFormat.isBlank()){
            return FileFormat.JSON;
        }
        try{
            return FileFormat.valueOf(sourceFormat);
        }catch (IllegalArgumentException exception){
            return FileFormat.JSON;
        }
    }
}
