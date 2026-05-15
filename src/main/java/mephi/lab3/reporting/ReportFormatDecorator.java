package mephi.lab3.reporting;

import mephi.lab3.app.LoadedMission;

public abstract class ReportFormatDecorator implements MissionReportFormat{
    private final MissionReportFormat wrappedReportFormat;

    protected ReportFormatDecorator(MissionReportFormat wrappedReportFormat){
        this.wrappedReportFormat = wrappedReportFormat;
    }

    protected String formatWrapped(LoadedMission loadedMission){
        return wrappedReportFormat.format(loadedMission);
    }
}