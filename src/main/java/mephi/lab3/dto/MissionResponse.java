package mephi.lab3.dto;

import java.util.List;

public record MissionResponse(
        String missionId,
        String date,
        String location,
        String outcome,
        String damageCost,
        String note,
        CurseDto curse,
        List<SorcererDto> participants,
        List<TechniqueDto> techniques,
        List<ExtensionBlockDto> extensionBlocks
){
}
