package mephi.lab3.validation;

import java.util.Map;
import mephi.lab3.domain.Mission;
import mephi.lab3.domain.MissionExtensionBlock;

public class TimelineValidator extends AbstractMissionValidationRule{
    @Override
    public void validate(Mission mission, ValidationResult validationResult){
        if (mission == null){
            return;
        }

        MissionExtensionBlock timelineBlock = findTimelineBlock(mission);
        if (timelineBlock == null){
            validateNext(mission, validationResult);
            return;
        }

        for (int index = 0; index < timelineBlock.getEntries().size(); index++) {
            Map<String, String> entry = timelineBlock.getEntries().get(index);
            validateEntryValue(entry, "operationTimeline", index, "timestamp", validationResult);
            validateEntryValue(entry, "operationTimeline", index, "type", validationResult);
            validateEntryValue(entry, "operationTimeline", index, "description", validationResult);
        }

        validateNext(mission, validationResult);
    }

    private MissionExtensionBlock findTimelineBlock(Mission mission){
        MissionExtensionBlock operationTimeline = mission.getExtensionBlocks().get("operationTimeline");
        if (operationTimeline != null){
            return operationTimeline;
        }
        return mission.getExtensionBlocks().get("timeline");
    }
}