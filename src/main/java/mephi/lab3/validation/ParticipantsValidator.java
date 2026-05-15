package mephi.lab3.validation;

import java.util.List;
import mephi.lab3.domain.Mission;
import mephi.lab3.domain.Sorcerer;

public class ParticipantsValidator extends AbstractMissionValidationRule{
    @Override
    public void validate(Mission mission, ValidationResult validationResult){
        if (mission == null){
            return;
        }

        List<Sorcerer> participants = mission.getParticipants();
        for (int index = 0; index < participants.size(); index++) {
            Sorcerer participant = participants.get(index);
            validateIndexedValue("sorcerers", index, "name", participant == null ? null : participant.getName(), validationResult);
            validateIndexedValue("sorcerers", index, "rank", participant == null ? null : participant.getRank(), validationResult);
        }

        validateNext(mission, validationResult);
    }
}