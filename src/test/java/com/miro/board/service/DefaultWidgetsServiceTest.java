package com.miro.board.service;

import com.miro.board.model.Widget;
import com.miro.board.model.WidgetDTO;
import com.miro.board.model.exception.WidgetNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class DefaultWidgetsServiceTest {
    @Autowired
    private WidgetsService widgetsService;

    @Before
    public void flushDefaultWidgetsServiceStorage() throws NoSuchFieldException, IllegalAccessException {
        Field widgetsField = DefaultWidgetsService.class.getDeclaredField("widgets");
        widgetsField.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(widgetsField, widgetsField.getModifiers() & ~Modifier.FINAL);

        widgetsField.set(null, new AtomicReference<>(Collections.unmodifiableMap(new LinkedHashMap<>())));
    }

    //GET

    @Test
    public void testGetWidgetsInAlphabeticalOrder() {
        WidgetDTO input1 = new WidgetDTO(1, 2 ,4 , 5, 10);
        WidgetDTO input2 = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget1 = widgetsService.createWidget(input1);
        Widget widget2 = widgetsService.createWidget(input2);

        Collection<Widget> actualWidgets = widgetsService.getWidgets();

        Iterator<Widget> widgetIterator = actualWidgets.iterator();

        Widget actualWidget2 = widgetIterator.next();
        assertEquals(widget2.getId(), actualWidget2.getId());
        assertEquals(widget2.getLastModified(), actualWidget2.getLastModified());
        assertEquals(widget2.getX(), actualWidget2.getX());
        assertEquals(widget2.getY(), actualWidget2.getY());
        assertEquals(widget2.getZ(), actualWidget2.getZ());
        assertEquals(widget2.getWidth(), actualWidget2.getWidth());
        assertEquals(widget2.getHeight(), actualWidget2.getHeight());

        Widget actualWidget1 = widgetIterator.next();
        assertEquals(widget1.getId(), actualWidget1.getId());
        assertEquals(widget1.getLastModified(), actualWidget1.getLastModified());
        assertEquals(widget1.getX(), actualWidget1.getX());
        assertEquals(widget1.getY(), actualWidget1.getY());
        assertEquals(widget1.getZ(), actualWidget1.getZ());
        assertEquals(widget1.getWidth(), actualWidget1.getWidth());
        assertEquals(widget1.getHeight(), actualWidget1.getHeight());
    }

    @Test
    public void testGetExistingWidget() {
        WidgetDTO input = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        Widget actualWidget = widgetsService.getWidget(widget.getId());

        assertEquals(widget.getId(), actualWidget.getId());
        assertEquals(widget.getLastModified(), actualWidget.getLastModified());
        assertEquals(widget.getX(), actualWidget.getX());
        assertEquals(widget.getY(), actualWidget.getY());
        assertEquals(widget.getZ(), actualWidget.getZ());
        assertEquals(widget.getWidth(), actualWidget.getWidth());
        assertEquals(widget.getHeight(), actualWidget.getHeight());
    }

    @Test(expected = WidgetNotFoundException.class)
    public void testGetNonExistingWidget() {
        widgetsService.getWidget("1");
    }

    //POST

    @Test
    public void testCreateWidgetOnEmptyBoardWithoutZ() {
        WidgetDTO input = new WidgetDTO(1, 2 ,null , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(input.getX(), widget.getX());
        assertEquals(input.getY(), widget.getY());
        assertEquals(Integer.valueOf(1), widget.getZ());
        assertEquals(input.getWidth(), widget.getWidth());
        assertEquals(input.getHeight(), widget.getHeight());
    }

    @Test
    public void testCreateWidgetOnEmptyBoardWithZ() {
        WidgetDTO input = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(input.getX(), widget.getX());
        assertEquals(input.getY(), widget.getY());
        assertEquals(Integer.valueOf(3), widget.getZ());
        assertEquals(input.getWidth(), widget.getWidth());
        assertEquals(input.getHeight(), widget.getHeight());
    }

    @Test
    public void testCreateWidgetWithoutZOnBoardWithWidget() {
        WidgetDTO inputBefore = new WidgetDTO(1, 2 ,3 , 5, 10);
        widgetsService.createWidget(inputBefore);

        WidgetDTO input = new WidgetDTO(1, 2 ,null , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(input.getX(), widget.getX());
        assertEquals(input.getY(), widget.getY());
        assertEquals(Integer.valueOf(4), widget.getZ());
        assertEquals(input.getWidth(), widget.getWidth());
        assertEquals(input.getHeight(), widget.getHeight());
    }

    @Test
    public void testCreateWidgetWithSmallestZOnBoardWith1WidgetWithoutOverlay1() {
        WidgetDTO inputBefore = new WidgetDTO(1, 2 ,3 , 5, 10);
        widgetsService.createWidget(inputBefore);

        WidgetDTO input = new WidgetDTO(1, 2 ,1 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(input.getX(), widget.getX());
        assertEquals(input.getY(), widget.getY());
        assertEquals(Integer.valueOf(1), widget.getZ());
        assertEquals(input.getWidth(), widget.getWidth());
        assertEquals(input.getHeight(), widget.getHeight());
    }

    @Test
    public void testCreateWidgetWithZOnBoardWith1WidgetWithoutOverlay2() {
        WidgetDTO inputBefore = new WidgetDTO(1, 2 ,3 , 5, 10);
        widgetsService.createWidget(inputBefore);

        WidgetDTO input = new WidgetDTO(1, 2 ,6 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(input.getX(), widget.getX());
        assertEquals(input.getY(), widget.getY());
        assertEquals(Integer.valueOf(6), widget.getZ());
        assertEquals(input.getWidth(), widget.getWidth());
        assertEquals(input.getHeight(), widget.getHeight());
    }

    @Test
    public void testCreateWidgetWithZOnBoardWith1WidgetWithOverlay() {
        WidgetDTO inputBefore = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widgetBefore = widgetsService.createWidget(inputBefore);

        WidgetDTO input = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(input.getX(), widget.getX());
        assertEquals(input.getY(), widget.getY());
        assertEquals(Integer.valueOf(3), widget.getZ());
        assertEquals(input.getWidth(), widget.getWidth());
        assertEquals(input.getHeight(), widget.getHeight());

        Widget widget2 = widgetsService.getWidget(widgetBefore.getId());

        assertEquals(widgetBefore.getId(), widget2.getId());
        assertNotNull(widget2.getLastModified());
        assertEquals(inputBefore.getX(), widget2.getX());
        assertEquals(inputBefore.getY(), widget2.getY());
        assertEquals(Integer.valueOf(4), widget2.getZ());
        assertEquals(inputBefore.getWidth(), widget2.getWidth());
        assertEquals(inputBefore.getHeight(), widget2.getHeight());
    }

    @Test
    public void testCreateWidgetWithZOnBoardWith2WidgetsWithOverlay() {
        WidgetDTO input1Before = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget1Before = widgetsService.createWidget(input1Before);
        WidgetDTO input2Before = new WidgetDTO(1, 2 ,4 , 5, 10);
        Widget widget2Before = widgetsService.createWidget(input2Before);

        WidgetDTO input = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertNotNull(widget.getId());
        assertNotNull(widget.getLastModified());
        assertEquals(input.getX(), widget.getX());
        assertEquals(input.getY(), widget.getY());
        assertEquals(Integer.valueOf(3), widget.getZ());
        assertEquals(input.getWidth(), widget.getWidth());
        assertEquals(input.getHeight(), widget.getHeight());

        Widget widget2 = widgetsService.getWidget(widget1Before.getId());

        assertEquals(widget1Before.getId(), widget2.getId());
        assertNotNull(widget2.getLastModified());
        assertEquals(widget1Before.getX(), widget2.getX());
        assertEquals(widget1Before.getY(), widget2.getY());
        assertEquals(Integer.valueOf(4), widget2.getZ());
        assertEquals(widget1Before.getWidth(), widget2.getWidth());
        assertEquals(widget1Before.getHeight(), widget2.getHeight());

        Widget widget3 = widgetsService.getWidget(widget2Before.getId());

        assertEquals(widget2Before.getId(), widget3.getId());
        assertNotNull(widget3.getLastModified());
        assertEquals(widget2Before.getX(), widget3.getX());
        assertEquals(widget2Before.getY(), widget3.getY());
        assertEquals(Integer.valueOf(5), widget3.getZ());
        assertEquals(widget2Before.getWidth(), widget3.getWidth());
        assertEquals(widget2Before.getHeight(), widget3.getHeight());
    }

    //PUT

    @Test(expected = WidgetNotFoundException.class)
    public void testUpdateWidgetOnEmptyBoard() {
        WidgetDTO input = new WidgetDTO(1, 2 ,null , 5, 10);
        widgetsService.updateWidget("1", input);
    }

    @Test
    public void testUpdateWidgetWithoutZOnBoardWith1SameWidget() {
        WidgetDTO input1 = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget1 = widgetsService.createWidget(input1);

        WidgetDTO input2 = new WidgetDTO(1, 2 ,null , 5, 10);
        Widget widget2 = widgetsService.updateWidget(widget1.getId(), input2);

        assertEquals(widget1.getId(), widget2.getId());
        assertNotNull(widget2.getLastModified());
        assertEquals(widget1.getX(), widget2.getX());
        assertEquals(widget1.getY(), widget2.getY());
        assertEquals(Integer.valueOf(1), widget2.getZ());
        assertEquals(widget1.getWidth(), widget2.getWidth());
        assertEquals(widget1.getHeight(), widget2.getHeight());
    }

    @Test
    public void testUpdateWidgetWithoutZOnBoardWith2Widgets() {
        WidgetDTO input1 = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget1 = widgetsService.createWidget(input1);

        WidgetDTO input2 = new WidgetDTO(1, 2 ,4 , 5, 10);
        Widget widget2 = widgetsService.createWidget(input2);

        WidgetDTO input3 = new WidgetDTO(1, 2 ,null, 5, 10);
        Widget widget3 = widgetsService.updateWidget(widget1.getId(), input3);

        Collection<Widget> actualWidgets = widgetsService.getWidgets();

        assertEquals(2, actualWidgets.size());

        Iterator<Widget> widgetIterator = actualWidgets.iterator();

        Widget actualWidget1 = widgetIterator.next();
        assertEquals(widget2.getId(), actualWidget1.getId());
        assertNotNull(actualWidget1.getLastModified());
        assertEquals(widget2.getX(), actualWidget1.getX());
        assertEquals(widget2.getY(), actualWidget1.getY());
        assertEquals(Integer.valueOf(4), actualWidget1.getZ());
        assertEquals(widget2.getWidth(), actualWidget1.getWidth());
        assertEquals(widget2.getHeight(), actualWidget1.getHeight());

        Widget actualWidget2 = widgetIterator.next();
        assertEquals(widget3.getId(), actualWidget2.getId());
        assertNotNull(actualWidget2.getLastModified());
        assertEquals(widget3.getX(), actualWidget2.getX());
        assertEquals(widget3.getY(), actualWidget2.getY());
        assertEquals(Integer.valueOf(5), actualWidget2.getZ());
        assertEquals(widget3.getWidth(), actualWidget2.getWidth());
        assertEquals(widget3.getHeight(), actualWidget2.getHeight());
    }

    @Test
    public void testUpdateWidgetWithZOnBoardWith2WidgestWithoutOverlay() {
        WidgetDTO input1 = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget1 = widgetsService.createWidget(input1);

        WidgetDTO input2 = new WidgetDTO(1, 2 ,4 , 5, 10);
        Widget widget2 = widgetsService.createWidget(input2);

        WidgetDTO input3 = new WidgetDTO(1, 2 ,1, 5, 10);
        Widget widget3 = widgetsService.updateWidget(widget1.getId(), input3);

        Collection<Widget> actualWidgets = widgetsService.getWidgets();

        assertEquals(2, actualWidgets.size());

        Iterator<Widget> widgetIterator = actualWidgets.iterator();

        Widget actualWidget2 = widgetIterator.next();
        assertEquals(widget3.getId(), actualWidget2.getId());
        assertNotNull(actualWidget2.getLastModified());
        assertEquals(widget3.getX(), actualWidget2.getX());
        assertEquals(widget3.getY(), actualWidget2.getY());
        assertEquals(Integer.valueOf(1), actualWidget2.getZ());
        assertEquals(widget3.getWidth(), actualWidget2.getWidth());
        assertEquals(widget3.getHeight(), actualWidget2.getHeight());

        Widget actualWidget1 = widgetIterator.next();
        assertEquals(widget2.getId(), actualWidget1.getId());
        assertNotNull(actualWidget1.getLastModified());
        assertEquals(widget2.getX(), actualWidget1.getX());
        assertEquals(widget2.getY(), actualWidget1.getY());
        assertEquals(Integer.valueOf(4), actualWidget1.getZ());
        assertEquals(widget2.getWidth(), actualWidget1.getWidth());
        assertEquals(widget2.getHeight(), actualWidget1.getHeight());
    }

    @Test
    public void testUpdateWidgetWithZOnBoardWith3WidgestWithOverlay() {
        WidgetDTO input1 = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget1 = widgetsService.createWidget(input1);

        WidgetDTO input2 = new WidgetDTO(1, 2 ,4 , 5, 10);
        Widget widget2 = widgetsService.createWidget(input2);

        WidgetDTO input4 = new WidgetDTO(1, 2 ,5 , 5, 10);
        Widget widget4 = widgetsService.createWidget(input4);

        WidgetDTO input3 = new WidgetDTO(1, 2 ,4, 5, 10);
        Widget widget3 = widgetsService.updateWidget(widget1.getId(), input3);

        Collection<Widget> actualWidgets = widgetsService.getWidgets();

        assertEquals(3, actualWidgets.size());

        Iterator<Widget> widgetIterator = actualWidgets.iterator();

        Widget actualWidget2 = widgetIterator.next();
        assertEquals(widget3.getId(), actualWidget2.getId());
        assertNotNull(actualWidget2.getLastModified());
        assertEquals(widget3.getX(), actualWidget2.getX());
        assertEquals(widget3.getY(), actualWidget2.getY());
        assertEquals(Integer.valueOf(4), actualWidget2.getZ());
        assertEquals(widget3.getWidth(), actualWidget2.getWidth());
        assertEquals(widget3.getHeight(), actualWidget2.getHeight());

        Widget actualWidget1 = widgetIterator.next();
        assertEquals(widget2.getId(), actualWidget1.getId());
        assertNotNull(actualWidget1.getLastModified());
        assertEquals(widget2.getX(), actualWidget1.getX());
        assertEquals(widget2.getY(), actualWidget1.getY());
        assertEquals(Integer.valueOf(5), actualWidget1.getZ());
        assertEquals(widget2.getWidth(), actualWidget1.getWidth());
        assertEquals(widget2.getHeight(), actualWidget1.getHeight());

        Widget actualWidget3 = widgetIterator.next();
        assertEquals(widget4.getId(), actualWidget3.getId());
        assertNotNull(actualWidget3.getLastModified());
        assertEquals(widget4.getX(), actualWidget3.getX());
        assertEquals(widget4.getY(), actualWidget3.getY());
        assertEquals(Integer.valueOf(6), actualWidget3.getZ());
        assertEquals(widget4.getWidth(), actualWidget3.getWidth());
        assertEquals(widget4.getHeight(), actualWidget3.getHeight());
    }

    //DELETE

    @Test
    public void testDeleteExistingWidget() {
        WidgetDTO input = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        assertTrue(widgetsService.deleteWidget(widget.getId()));
    }

    @Test
    public void testDeleteWidgetWithZOnBoardWith2Widgets() {
        WidgetDTO input1Before = new WidgetDTO(1, 2 ,3 , 5, 10);
        Widget widget1 = widgetsService.createWidget(input1Before);
        WidgetDTO input2Before = new WidgetDTO(1, 2 ,5 , 5, 10);
        Widget widget2 = widgetsService.createWidget(input2Before);

        WidgetDTO input = new WidgetDTO(1, 2 ,4 , 5, 10);
        Widget widget = widgetsService.createWidget(input);

        widgetsService.deleteWidget(widget.getId());

        Collection<Widget> actualWidgets = widgetsService.getWidgets();

        assertEquals(2, actualWidgets.size());

        Iterator<Widget> widgetIterator = actualWidgets.iterator();

        Widget actualWidget1 = widgetIterator.next();
        assertEquals(widget1.getId(), actualWidget1.getId());
        assertEquals(widget1.getLastModified(), actualWidget1.getLastModified());
        assertEquals(widget1.getX(), actualWidget1.getX());
        assertEquals(widget1.getY(), actualWidget1.getY());
        assertEquals(widget1.getZ(), actualWidget1.getZ());
        assertEquals(widget1.getWidth(), actualWidget1.getWidth());
        assertEquals(widget1.getHeight(), actualWidget1.getHeight());

        Widget actualWidget2 = widgetIterator.next();
        assertEquals(widget2.getId(), actualWidget2.getId());
        assertEquals(widget2.getLastModified(), actualWidget2.getLastModified());
        assertEquals(widget2.getX(), actualWidget2.getX());
        assertEquals(widget2.getY(), actualWidget2.getY());
        assertEquals(widget2.getZ(), actualWidget2.getZ());
        assertEquals(widget2.getWidth(), actualWidget2.getWidth());
        assertEquals(widget2.getHeight(), actualWidget2.getHeight());
    }

    @Test(expected = WidgetNotFoundException.class)
    public void testDeleteNonExistingWidget() {
        widgetsService.deleteWidget("1");
    }
}
