package mephi.lab3.reporting;

import mephi.lab3.app.LoadedMission;

public interface MissionReportFormat{
    String format(LoadedMission loadedMission);
}