package com.miro.board.model;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
public class WidgetDTO {
    @NotNull(message = "X is a mandatory param")
    private final Integer x;

    @NotNull(message = "Y is a mandatory param")
    private final Integer y;

    private final Integer z;

    @NotNull(message = "Width is a mandatory param")
    @Positive(message = "Width should be a positive integer")
    private final Integer width;

    @NotNull(message = "Height is a mandatory param")
    @Positive(message = "Height should be a positive integer")
    private final Integer height;

    public WidgetDTO(Integer x, Integer y, Integer z, Integer width, Integer height) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
    }
}
