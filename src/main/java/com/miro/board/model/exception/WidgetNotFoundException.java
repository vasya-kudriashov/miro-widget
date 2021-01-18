package com.miro.board.model.exception;

public class WidgetNotFoundException extends RuntimeException {
    public WidgetNotFoundException(String id) {
        super("Widget not found for id: " + id);
    }
}
