package mephi.lab3;

import org.springframework.boot.SpringApplication;//класс, который запускает приложение
import org.springframework.boot.autoconfigure.SpringBootApplication;//главная нотация спринг

@SpringBootApplication//аннотация - главный класс приложения
public class MissionArchiveApplication{
    public static void main(String[] args){
        SpringApplication.run(MissionArchiveApplication.class, args);//поднимает сервер, подключает настройки, создает контроллеры, сервисы, репозитории
    }
}
