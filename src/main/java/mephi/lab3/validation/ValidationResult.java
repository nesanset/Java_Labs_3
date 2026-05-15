package mephi.lab3.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult{
    private final List<String> warnings = new ArrayList<>();

    public void addWarning(String warning){
        if (warning == null || warning.isBlank()) {
            return;
        }
        warnings.add(warning);
    }

    public boolean hasWarnings(){
        return !warnings.isEmpty();
    }

    public List<String> getWarnings(){
        return Collections.unmodifiableList(warnings);
    }
}