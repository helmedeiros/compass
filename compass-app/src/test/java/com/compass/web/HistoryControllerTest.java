package com.compass.web;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.compass.config.CompassConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CompassConfiguration.class, WebMvcConfiguration.class})
public class HistoryControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void keeps_one_snapshot_for_each_ingested_event() throws Exception {
        postEvent("history-mia", "search");
        postEvent("history-mia", "search");
        postEvent("history-mia", "search");

        String body = mockMvc.perform(get("/entities/history-mia/history"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(occurrencesOf("\"confidence\"", body), is(3));
        assertThat(body, containsString("\"primaryProfile\":\"Explorer\""));
    }

    @Test
    public void has_an_empty_history_for_an_unseen_entity() throws Exception {
        String body = mockMvc.perform(get("/entities/history-ghost/history"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(body, is("[]"));
    }

    private void postEvent(String entityId, String type) throws Exception {
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"entityId\":\"" + entityId + "\",\"type\":\"" + type
                        + "\",\"occurredAt\":\"2013-07-15T09:00:00\"}"))
                .andExpect(status().isAccepted());
    }

    private int occurrencesOf(String needle, String haystack) {
        int count = 0;
        int from = haystack.indexOf(needle);
        while (from >= 0) {
            count++;
            from = haystack.indexOf(needle, from + needle.length());
        }
        return count;
    }
}
