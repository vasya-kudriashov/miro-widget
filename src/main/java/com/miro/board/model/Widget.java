package com.miro.board.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Widget {
    private final String id;
    private final Integer x;
    private final Integer y;
    private final Integer z;
    private final Integer width;
    private final Integer height;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime lastModified;

    public Widget(String id, Integer x, Integer y, Integer z, Integer width, Integer height) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.lastModified = LocalDateTime.now();
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
    }
}
