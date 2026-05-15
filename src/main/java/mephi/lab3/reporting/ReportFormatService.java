package mephi.lab3.reporting;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReportFormatService{
    private final Map<String, ReportOption> options = new LinkedHashMap<>();
    private final String defaultOptionNumber="1";
    private final String defaultReportType="detailed";

    public ReportFormatService(){
        register("1", "detailed",new DetailedReportDecorator(new BaseMissionReportFormat("-----АНАЛИЗ МИССИИ------", false)));
        register("2", "summary", new SummaryReportDecorator(new BaseMissionReportFormat("-----КРАТКИЙ ОТЧЕТ ПО МИССИИ------", true)));
    }

    public MissionReportFormat resolve(String reportType){
        ReportOption reportOption =resolveOption(reportType);
        if (reportOption == null){
            return options.get(defaultOptionNumber).reportFormat();
        }
        return reportOption.reportFormat();
    }

    public boolean supports(String reportType){
        if (reportType == null || reportType.isBlank()) {
            return true;
        }
        return resolveOption(reportType) != null;
    }

    public String getDefaultReportType(){
        return defaultReportType;
    }

    private void register(String optionNumber, String reportType, MissionReportFormat reportFormat){
        options.put(optionNumber, new ReportOption(reportType, reportFormat));
    }

    private ReportOption resolveOption(String reportType){
        if (reportType == null || reportType.isBlank()) {
            return options.get(defaultOptionNumber);
        }
        String normalizedReportType = reportType.trim();
        ReportOption reportOption = options.get(normalizedReportType);
        if (reportOption != null){
            return reportOption;
        }
        for (ReportOption option : options.values()){
            if (option.reportType().equalsIgnoreCase(normalizedReportType)){
                return option;
            }
        }
        return null;
    }

    private record ReportOption(String reportType, MissionReportFormat reportFormat){
    }
}