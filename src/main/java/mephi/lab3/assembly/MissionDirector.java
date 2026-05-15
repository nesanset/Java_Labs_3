package mephi.lab3.assembly;

import mephi.lab3.domain.Mission;
import mephi.lab3.parsing.*;

public class MissionDirector{
    private final MissionBuilder missionBuilder;

    public MissionDirector(MissionBuilder missionBuilder){
        this.missionBuilder = missionBuilder;
    }

    public Mission constructMission(String content, MissionParser parser) throws MissionParseException{
        parser.parse(content, missionBuilder);
        return missionBuilder.build();
    }
}