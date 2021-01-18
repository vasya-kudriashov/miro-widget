package com.miro.board;

import com.miro.board.service.DefaultWidgetsService;
import com.miro.board.service.WidgetsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WidgetsService widgetsService() {
        return new DefaultWidgetsService();
    }
}
