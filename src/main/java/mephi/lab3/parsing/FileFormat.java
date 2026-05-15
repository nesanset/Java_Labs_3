package mephi.lab3.parsing;

public enum FileFormat{
    TEXT("Текст"),
    JSON("JSON"),
    XML("XML"),
    YAML("YAML"),
    INI("INI"),
    EVENT_LOG("Event log");
    private final String viewName;

    FileFormat(String viewName){
        this.viewName = viewName;
    }

    public String getViewName(){
        return viewName;
    }
}
