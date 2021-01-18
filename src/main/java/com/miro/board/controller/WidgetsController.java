package com.miro.board.controller;

import com.miro.board.service.WidgetsService;
import com.miro.board.model.Widget;
import com.miro.board.model.WidgetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/widgets")
public class WidgetsController {
    private final WidgetsService widgetsService;

    @Autowired
    public WidgetsController(WidgetsService widgetsService) {
        this.widgetsService = widgetsService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Widget> getAll() {
        return widgetsService.getWidgets();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Widget get(@PathVariable("id") String id) {
        return widgetsService.getWidget(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Widget create(@Valid @RequestBody WidgetDTO widgetDTO) {
        return widgetsService.createWidget(widgetDTO);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Widget update(@PathVariable("id") String id, @Valid @RequestBody WidgetDTO widgetDTO) {
        return widgetsService.updateWidget(id, widgetDTO);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public boolean delete(@Valid @PathVariable("id") String id) {
        return widgetsService.deleteWidget(id);
    }
}
