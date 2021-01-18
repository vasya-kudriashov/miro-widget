package com.miro.board.service;

import com.miro.board.model.Widget;
import com.miro.board.model.WidgetDTO;
import com.miro.board.model.exception.WidgetNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DefaultWidgetsService implements WidgetsService {
    private static final AtomicReference<Map<String, Widget>> widgets
            = new AtomicReference<>(Collections.unmodifiableMap(new LinkedHashMap<>()));

    @Override
    public Collection<Widget> getWidgets() {
        return widgets.get().values();
    }

    @Override
    public Widget getWidget(String id) {
        if (!widgets.get().containsKey(id)) {
            throw new WidgetNotFoundException(id);
        }

        return widgets.get().get(id);
    }

    @Override
    public Widget createWidget(WidgetDTO widgetDTO) {
        Map<String, Widget> actualWidgets, newWidgets;
        Widget newWidget;

        do {
            actualWidgets = widgets.get();
            newWidget = createWidget(actualWidgets, widgetDTO, null);
            newWidgets = insertWidget(actualWidgets, newWidget);
        } while (!widgets.compareAndSet(actualWidgets, newWidgets));

        return newWidget;
    }

    @Override
    public Widget updateWidget(String id, WidgetDTO widgetDTO) {
        if (!widgets.get().containsKey(id)) {
            throw new WidgetNotFoundException(id);
        }

        Map<String, Widget> actualWidgets, newWidgets;
        Widget newWidget;

        do {
            actualWidgets = widgets.get();
            newWidget = createWidget(actualWidgets, widgetDTO, id);
            newWidgets = insertWidget(actualWidgets, newWidget);
        } while (!widgets.compareAndSet(actualWidgets, newWidgets));

        return newWidget;
    }

    @Override
    public boolean deleteWidget(String id) {
        if (!widgets.get().containsKey(id)) {
            throw new WidgetNotFoundException(id);
        }

        Map<String, Widget> actualWidgets, newWidgets;
        boolean isDeleted;

        do {
            actualWidgets = widgets.get();
            newWidgets = new LinkedHashMap<>(actualWidgets);
            isDeleted = (newWidgets.remove(id) != null);
        } while (!widgets.compareAndSet(actualWidgets, newWidgets));

        return isDeleted;
    }

    private Widget createWidget(Map<String, Widget> widgets,
                                WidgetDTO widgetDTO,
                                String id) {
        Integer z = widgetDTO.getZ();

        if (z == null) {
            z = 1;

            for (Map.Entry<String, Widget> widgetEntry : widgets.entrySet()) {
                if (widgetEntry.getValue().getId().equals(id)) {
                    continue;
                }

                z = widgetEntry.getValue().getZ() + 1;
            }
        }

        return new Widget(id, widgetDTO.getX(), widgetDTO.getY(), z,
                widgetDTO.getWidth(), widgetDTO.getHeight());
    }

    private Map<String, Widget> insertWidget(Map<String, Widget> widgets,
                                             Widget newWidget) {
        Map<String, Widget> newWidgetsById = new LinkedHashMap<>();

        boolean newWidgetAdded = false, shiftModeOn = false;
        Integer zInShiftMode = newWidget.getZ();

        for (Widget widget : widgets.values()) {
            if (widget.getId().equals(newWidget.getId())
                    && !widget.getZ().equals(newWidget.getZ())) {
                continue;
            }

            if (!newWidgetAdded && widget.getZ() >= newWidget.getZ()) {
                newWidgetsById.put(newWidget.getId(), newWidget);
                newWidgetAdded = true;

                if (widget.getId().equals(newWidget.getId())) {
                    continue;
                }

                shiftModeOn = widget.getZ().equals(newWidget.getZ());
            }

            Widget widgetToAdd = widget;

            if (shiftModeOn) {
                if (!widgetToAdd.getZ().equals(zInShiftMode)) {
                    shiftModeOn = false;
                } else {
                    zInShiftMode++;

                    widgetToAdd = new Widget(widgetToAdd.getId(),
                            widgetToAdd.getX(), widgetToAdd.getY(),
                            zInShiftMode, widgetToAdd.getWidth(),
                            widgetToAdd.getHeight());
                }
            }

            newWidgetsById.put(widgetToAdd.getId(), widgetToAdd);
        }

        if (!newWidgetAdded) {
            newWidgetsById.put(newWidget.getId(), newWidget);
        }

        return newWidgetsById;
    }
}
