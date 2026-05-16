package mephi.lab3.app;

import mephi.lab3.assembly.*;
import mephi.lab3.domain.Mission;
import mephi.lab3.parsing.*;
import mephi.lab3.parsing.detection.MissionFormatDetector;
import mephi.lab3.parsing.parsers.*;
import mephi.lab3.validation.*;

public class MissionLoader{
    private final MissionFormatDetector formatDetector = new MissionFormatDetector();
    private final ParserRegistry parserRegistry = new ParserRegistry();
    private final MissionValidator missionValidator = new MissionValidator();

    public MissionLoader(){
        parserRegistry.register(FileFormat.TEXT, new TextMissionParser());
        parserRegistry.register(FileFormat.JSON, new JsonMissionParser());
        parserRegistry.register(FileFormat.XML, new XmlMissionParser());
        parserRegistry.register(FileFormat.YAML, new YamlMissionParser());
        parserRegistry.register(FileFormat.INI, new IniMissionParser());
        parserRegistry.register(FileFormat.EVENT_LOG, new EventLogMissionParser());
    }

    public LoadedMission loadContent(String sourceName, String content) throws MissionParseException{
        FileFormat format = formatDetector.detect(sourceName, content);
        MissionParser parser = parserRegistry.resolve(format);
        MissionDirector missionDirector = new MissionDirector(new MissionBuilder());
        Mission mission = missionDirector.constructMission(content, parser);
        ValidationResult validationResult = missionValidator.validate(mission);
        return new LoadedMission(sourceName, format, mission, validationResult);
    }
}
