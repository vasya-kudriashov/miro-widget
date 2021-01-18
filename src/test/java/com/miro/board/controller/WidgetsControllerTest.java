package com.miro.board.controller;

import com.miro.board.service.DefaultWidgetsService;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class WidgetsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void flushDefaultWidgetsServiceStorage() throws NoSuchFieldException, IllegalAccessException {
        Field widgetsField = DefaultWidgetsService.class.getDeclaredField("widgets");
        widgetsField.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(widgetsField, widgetsField.getModifiers() & ~Modifier.FINAL);

        widgetsField.set(null, new AtomicReference<>(Collections.unmodifiableMap(new LinkedHashMap<>())));
    }

    @Test
    public void whenGetRequestToWidgetsByIdAndWidgetNotFound_thenCorrectResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/widgets/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Widget not found for id: 1")));
    }

    @Test
    public void whenGetRequestToWidgetsByIdAndValidData_thenCorrectResponse() throws Exception {
        String widget = "{\"x\": \"1\", \"y\": \"2\", \"z\": \"3\", \"width\": \"10\", \"height\": \"5\"}";

        String responseBody = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultWidgetId = new JSONObject(responseBody).getString("id");


        mockMvc.perform(MockMvcRequestBuilders.get("/widgets/" + resultWidgetId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(String.format("\"id\":\"%s\"", resultWidgetId))));
    }

    @Test
    public void whenGetRequestToWidgets_thenCorrectResponse() throws Exception {
        String widget = "{\"x\": \"1\", \"y\": \"2\", \"z\": \"3\", \"width\": \"10\", \"height\": \"5\"}";

        String responseBody = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultWidgetId = new JSONObject(responseBody).getString("id");

        mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(String.format("\"id\":\"%s\"", resultWidgetId))));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void whenPostRequestToWidgetsAndValidDataWithZ_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"y\":\"2\",\"z\":\"3\",\"width\":\"10\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"z\":3")));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void whenPostRequestToWidgetsAndValidDataWithoutZ_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"y\":\"2\",\"width\":\"10\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"z\":1")));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void whenPostRequestToWidgetsAndMissedX_thenCorrectResponse() throws Exception {
        String widget = "{\"y\":\"2\",\"z\":\"3\",\"width\":\"4\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("X is a mandatory param")));
    }

    @Test
    public void whenPostRequestToWidgetsAndMissedY_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"z\":\"3\",\"width\":\"10\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Y is a mandatory param")));
    }

    @Test
    public void whenPostRequestToWidgetsAndMissedWidth_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"z\":\"3\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Width is a mandatory param")));
    }

    @Test
    public void whenPostRequestToWidgetsAndMissedHeight_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"z\":\"3\",\"width\":\"10\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Height is a mandatory param")));
    }

    @Test
    public void whenPostRequestToWidgetsAndInvalidWidth_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"y\":\"2\",\"z\":\"3\",\"width\":\"-1\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Width should be a positive integer")));
    }

    @Test
    public void whenPostRequestToWidgetsAndInvalidHeight_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"y\":\"2\",\"z\":\"3\",\"width\":\"5\",\"height\":\"-1\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Height should be a positive integer")));
    }

    @Test
    public void whenPutRequestToWidgetsAndValidDataWithZ_thenCorrectResponse() throws Exception {
        String widget = "{\"x\": \"1\", \"y\": \"2\", \"z\": \"3\", \"width\": \"10\", \"height\": \"5\"}";

        String responseBody = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultWidget1Id = new JSONObject(responseBody).getString("id");

        widget = "{\"x\": \"1\", \"y\": \"2\", \"z\": \"2\", \"width\": \"10\", \"height\": \"5\"}";

        responseBody = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultWidgetId = new JSONObject(responseBody).getString("id");

        String updateWidget = "{\"x\":\"-1\",\"y\":\"-2\",\"z\":\"3\",\"width\":\"1\",\"height\":\"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/" + resultWidgetId)
                .content(updateWidget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "\"id\":\"" + resultWidgetId + "\",\"x\":-1,\"y\":-2,\"z\":3,\"width\":1,\"height\":2")));

        mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .content(updateWidget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "\"id\":\"" + resultWidgetId + "\",\"x\":-1,\"y\":-2,\"z\":3,\"width\":1,\"height\":2")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "\"id\":\"" + resultWidget1Id + "\",\"x\":1,\"y\":2,\"z\":4,\"width\":10,\"height\":5")));
    }

    @Test
    public void whenPutRequestToWidgetsAndValidDataWithoutZ_thenCorrectResponse() throws Exception {
        String widget = "{\"x\": \"1\", \"y\": \"2\", \"z\": \"3\", \"width\": \"10\", \"height\": \"5\"}";

        String responseBody = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultWidget1Id = new JSONObject(responseBody).getString("id");

        widget = "{\"x\": \"1\", \"y\": \"2\", \"z\": \"2\", \"width\": \"10\", \"height\": \"5\"}";

        responseBody = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultWidgetId = new JSONObject(responseBody).getString("id");

        String updateWidget = "{\"x\":\"-1\",\"y\":\"-2\",\"width\":\"1\",\"height\":\"2\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/" + resultWidgetId)
                .content(updateWidget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "\"id\":\"" + resultWidgetId + "\",\"x\":-1,\"y\":-2,\"z\":4,\"width\":1,\"height\":2")));

        mockMvc.perform(MockMvcRequestBuilders.get("/widgets")
                .content(updateWidget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "\"id\":\"" + resultWidgetId + "\",\"x\":-1,\"y\":-2,\"z\":4,\"width\":1,\"height\":2")))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(
                        "\"id\":\"" + resultWidget1Id + "\",\"x\":1,\"y\":2,\"z\":3,\"width\":10,\"height\":5")));
    }

    @Test
    public void whenPutRequestToWidgetsAndMissedX_thenCorrectResponse() throws Exception {
        String widget = "{\"y\":\"2\",\"z\":\"3\",\"width\":\"4\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/1")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("X is a mandatory param")));
    }

    @Test
    public void whenPutRequestToWidgetsAndMissedY_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"z\":\"3\",\"width\":\"10\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/1")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Y is a mandatory param")));
    }

    @Test
    public void whenPutRequestToWidgetsAndMissedWidth_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"z\":\"3\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/1")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Width is a mandatory param")));
    }

    @Test
    public void whenPutRequestToWidgetsAndMissedHeight_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"z\":\"3\",\"width\":\"10\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/1")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Height is a mandatory param")));
    }

    @Test
    public void whenPutRequestToWidgetsAndInvalidWidth_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"y\":\"2\",\"z\":\"3\",\"width\":\"-1\",\"height\":\"5\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/1")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Width should be a positive integer")));
    }

    @Test
    public void whenPutRequestToWidgetsAndInvalidHeight_thenCorrectResponse() throws Exception {
        String widget = "{\"x\":\"1\",\"y\":\"2\",\"z\":\"3\",\"width\":\"5\",\"height\":\"-1\"}";

        mockMvc.perform(MockMvcRequestBuilders.put("/widgets/1")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Height should be a positive integer")));
    }

    @Test
    public void whenDeleteRequestToWidgetsAndWidgetNotFound_thenCorrectResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/widgets/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Widget not found for id: 1")));
    }

    @Test
    public void whenDeleteRequestToWidgetsAndValidData_thenCorrectResponse() throws Exception {
        String widget = "{\"x\": \"1\", \"y\": \"2\", \"z\": \"3\", \"width\": \"10\", \"height\": \"5\"}";

        String responseBody = mockMvc.perform(MockMvcRequestBuilders.post("/widgets")
                .content(widget)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String resultWidgetId = new JSONObject(responseBody).getString("id");

        mockMvc.perform(MockMvcRequestBuilders.delete("/widgets/" + resultWidgetId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("true"));
    }
}

