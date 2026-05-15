package mephi.lab3.validation;

import mephi.lab3.domain.Mission;

public class RequiredFieldsValidator extends AbstractMissionValidationRule{
    @Override
    public void validate(Mission mission, ValidationResult validationResult){
        if (mission == null){
            addRequiredFieldWarning("mission", validationResult);
            return;
        }

        validateRequiredValue("missionId", mission.getMissionId(), validationResult);
        validateRequiredValue("date", mission.getDate(), validationResult);
        validateRequiredValue("location", mission.getLocation(), validationResult);
        validateRequiredValue("outcome", mission.getOutcome(), validationResult);

        validateNext(mission, validationResult);
    }
}