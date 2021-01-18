package com.miro.board.service;

import com.miro.board.model.Widget;
import com.miro.board.model.WidgetDTO;

import java.util.Collection;

public interface WidgetsService {
    Collection<Widget> getWidgets();
    Widget getWidget(String id);
    Widget createWidget(WidgetDTO widgetDTO);
    Widget updateWidget(String id, WidgetDTO widgetDTO);
    boolean deleteWidget(String id);
}
