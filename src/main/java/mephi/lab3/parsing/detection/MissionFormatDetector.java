package mephi.lab3.parsing.detection;

import java.util.Locale;
import mephi.lab3.parsing.FileFormat;

public class MissionFormatDetector{
    public FileFormat detect(String wayToFile, String content){
        String normalizedPath = wayToFile.toLowerCase(Locale.ROOT);
        String normalizedContent = content.trim();

        if (normalizedPath.endsWith(".json") || normalizedContent.startsWith("{")){
            return FileFormat.JSON;
        }
        if (normalizedPath.endsWith(".xml") || normalizedContent.startsWith("<mission")){
            return FileFormat.XML;
        }
        if (normalizedPath.endsWith(".yaml") || normalizedPath.endsWith(".yml")){
            return FileFormat.YAML;
        }
        if (normalizedContent.startsWith("[MISSION]") || normalizedContent.startsWith("[")){
            return FileFormat.INI;
        }
        if (normalizedContent.startsWith("MISSION_CREATED|")){
            return FileFormat.EVENT_LOG;
        }
        return FileFormat.TEXT;
    }
}