package mephi.lab3.validation;

import java.util.List;
import mephi.lab3.domain.Mission;
import mephi.lab3.domain.Technique;

public class TechniquesValidator extends AbstractMissionValidationRule{
    @Override
    public void validate(Mission mission, ValidationResult validationResult){
        if (mission == null){
            return;
        }

        List<Technique> techniques = mission.getTechniques();
        for (int index = 0; index < techniques.size(); index++) {
            Technique technique = techniques.get(index);
            validateIndexedValue("techniques", index, "name", technique == null ? null : technique.getName(), validationResult);
            validateIndexedValue("techniques", index, "type", technique == null ? null : technique.getType(), validationResult);
            validateIndexedValue("techniques", index, "owner", technique == null ? null : technique.getOwner(), validationResult);
        }

        validateNext(mission, validationResult);
    }
}