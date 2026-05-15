package mephi.lab3.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
    private static final TypeReference<Map<String, Map<String, Object>>> EXTENSION_TYPE = new TypeReference<>(){};
    private static final TypeReference<Map<String, String>> EXTENSION_FIELDS_TYPE = new TypeReference<>(){};
    private static final TypeReference<List<Map<String, String>>> EXTENSION_ENTRIES_TYPE = new TypeReference<>(){};

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
        return new Mission(entity.getMissionId(), entity.getDate(), entity.getLocation(), entity.getOutcome(), entity.getDamageCost(), toDomainSorcerers(entity.getParticipants()), toDomainCurse(entity.getCurse()), toDomainTechniques(entity.getTechniques()), entity.getNote(), toDomainExtensions(readExtensions(entity.getExtensionData())));
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

    private Map<String, MissionExtensionBlock> toDomainExtensions(Map<String, Map<String, Object>> extensionData){
        Map<String, MissionExtensionBlock> extensionBlocks = new LinkedHashMap<>();
        if (extensionData == null){
            return extensionBlocks;
        }
        for (String blockName : extensionData.keySet()){
            MissionExtensionBlock block = new MissionExtensionBlock(blockName);
            Map<String, Object> rawBlock = extensionData.get(blockName);
            Map<String, String> fields = readExtensionFields(rawBlock);
            for (Map.Entry<String, String> field : fields.entrySet()){
                block.addField(field.getKey(), field.getValue());
            }

            List<Map<String, String>> entries = readExtensionEntries(rawBlock);
            for (Map<String, String> extensionEntry : entries){
                block.addEntry(extensionEntry);
            }
            extensionBlocks.put(blockName, block);
        }
        return extensionBlocks;
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

    private Map<String, Map<String, Object>> readExtensions(String extensionData){
        if (extensionData == null || extensionData.isBlank()){
            return Map.of();
        }
        try{
            return objectMapper.readValue(extensionData, EXTENSION_TYPE);
        }catch (JsonProcessingException exception){
            return Map.of();
        }
    }

    private Map<String, String> readExtensionFields(Map<String, Object> rawBlock){
        Object fields = rawBlock.get("fields");
        if (fields == null){
            return Map.of();
        }
        return objectMapper.convertValue(fields, EXTENSION_FIELDS_TYPE);
    }

    private List<Map<String, String>> readExtensionEntries(Map<String, Object> rawBlock){
        Object entries = rawBlock.get("entries");
        if (entries == null){
            return List.of();
        }
        return objectMapper.convertValue(entries, EXTENSION_ENTRIES_TYPE);
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