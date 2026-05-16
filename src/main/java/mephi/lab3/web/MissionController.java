package mephi.lab3.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.*;
import mephi.lab3.dto.*;
import mephi.lab3.service.MissionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/missions")
@Tag(name = "Mission Archive", description = "Импорт, хранение и отчеты по миссиям")
public class MissionController{
    private final MissionService missionService;

    public MissionController(MissionService missionService){
        this.missionService = missionService;
    }

    @GetMapping
    @Operation(summary = "Получить список миссий из архива")
    public List<MissionSummaryResponse> findAll(){
        return missionService.findAll();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Импортировать миссию из файла")
    public ImportResponse importFile(@RequestPart("file") MultipartFile file){
        return missionService.importFile(file);
    }

    @GetMapping("/{missionId}")
    @Operation(summary = "Получить миссию по missionId")
    public MissionResponse findByMissionId(@PathVariable("missionId") String missionId){
        return missionService.findByMissionId(missionId);
    }

    @GetMapping("/{missionId}/textreport")
    @Operation(summary = "Сформировать текстовый отчет")
    public String textReport(@PathVariable("missionId") String missionId, @RequestParam(name = "reportType", required = false) String reportType){
        return missionService.buildTextReport(missionId, normalizeReportType(reportType));
    }

    @DeleteMapping("/{missionId}")
    @Operation(summary = "Удалить миссию из архива")
    public void deleteByMissionId(@PathVariable("missionId") String missionId){
        missionService.deleteByMissionId(missionId);
    }

    private String normalizeReportType(String reportType){
        if (reportType == null || reportType.isBlank()){
            return missionService.getDefaultReportType();
        }
        return reportType;
    }
}
