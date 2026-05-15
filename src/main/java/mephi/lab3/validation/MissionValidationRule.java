package mephi.lab3.validation;

import mephi.lab3.domain.Mission;

public interface MissionValidationRule{
    MissionValidationRule setNext(MissionValidationRule nextRule);
    void validate(Mission mission, ValidationResult validationResult);
}
