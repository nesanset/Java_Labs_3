package mephi.lab3.validation;

import mephi.lab3.domain.Mission;

public class MissionValidator{
    private final MissionValidationRule validationChain;

    public MissionValidator(){
        MissionValidationRule requiredFieldsRule = new RequiredFieldsValidator();
        requiredFieldsRule.setNext(new CurseValidator()).setNext(new ParticipantsValidator()).setNext(new TechniquesValidator()).setNext(new TimelineValidator());
        validationChain = requiredFieldsRule;
    }

    public ValidationResult validate(Mission mission){
        ValidationResult validationResult = new ValidationResult();
        validationChain.validate(mission, validationResult);
        return validationResult;
    }
}
