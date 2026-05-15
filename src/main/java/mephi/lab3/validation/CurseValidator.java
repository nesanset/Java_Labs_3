package mephi.lab3.validation;

import mephi.lab3.domain.*;

public class CurseValidator extends AbstractMissionValidationRule{
    @Override
    public void validate(Mission mission, ValidationResult validationResult){
        if (mission == null){
            addRequiredFieldWarning("curse", validationResult);
            return;
        }

        Curse curse = mission.getCurse();
        if (curse == null){
            addRequiredFieldWarning("curse.name", validationResult);
            addRequiredFieldWarning("curse.threatLevel", validationResult);
            validateNext(mission, validationResult);
            return;
        }

        validateRequiredValue("curse.name", curse.getName(), validationResult);
        validateRequiredValue("curse.threatLevel", curse.getThreatLevel(), validationResult);
        validateNext(mission, validationResult);
    }
}