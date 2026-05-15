package mephi.lab3.app;

import mephi.lab3.domain.Mission;
import mephi.lab3.parsing.FileFormat;
import mephi.lab3.validation.ValidationResult;

public class LoadedMission{
    private final String wayToFile;
    private final FileFormat format;
    private final Mission mission;
    private final ValidationResult validationResult;

    public LoadedMission(String wayToFile, FileFormat format, Mission mission, ValidationResult validationResult){
        this.wayToFile = wayToFile;
        this.format = format;
        this.mission = mission;
        this.validationResult = validationResult;
    }

    public FileFormat getFormat(){
        return format;
    }

    public String getWayToFile(){
        return wayToFile;
    }

    public Mission getMission(){
        return this.mission;
    }

    public ValidationResult getValidationResult(){
        return validationResult;
    }
}