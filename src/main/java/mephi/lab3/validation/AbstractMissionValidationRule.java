package mephi.lab3.validation;

import mephi.lab3.domain.Mission;
import java.util.Map;

public abstract class AbstractMissionValidationRule implements MissionValidationRule{
    private MissionValidationRule nextRule;

    @Override
    public MissionValidationRule setNext(MissionValidationRule nextRule){
        this.nextRule = nextRule;
        return nextRule;
    }

    protected void validateNext(Mission mission, ValidationResult validationResult){
        if (nextRule != null){
            nextRule.validate(mission, validationResult);
        }
    }

    protected void addRequiredFieldWarning(String fieldName, ValidationResult validationResult){
        validationResult.addWarning("ВНИМАНИЕ обязательное поле " + fieldName + " не задано, проверьте корректность файлов.");
    }

    protected boolean isBlank(String value){
        return value == null || value.isBlank();
    }

    protected void validateRequiredValue(String fieldName, String value, ValidationResult validationResult){
        if (isBlank(value)){
            addRequiredFieldWarning(fieldName, validationResult);
        }
    }

    protected void validateIndexedValue(String blockName, int index, String fieldName, String value, ValidationResult validationResult){
        validateRequiredValue(buildIndexedFieldName(blockName, index, fieldName), value, validationResult);
    }

    protected void validateEntryValue(Map<String, String> entry, String blockName, int index, String fieldName, ValidationResult validationResult){
        String value = entry == null ? null : entry.get(fieldName);
        validateIndexedValue(blockName, index, fieldName, value, validationResult);
    }

    private String buildIndexedFieldName(String blockName, int index, String fieldName){
        return blockName + "[" + (index + 1) + "]." + fieldName;
    }
}