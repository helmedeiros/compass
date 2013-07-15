package com.compass.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.compass.config.CompassConfiguration;
import com.compass.domain.model.EntityId;
import com.compass.domain.port.out.EventStore;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {CompassConfiguration.class, WebMvcConfiguration.class})
public class IngestControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EventStore eventStore;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void accepts_an_event_and_stores_it_for_the_entity() throws Exception {
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"entityId\":\"alice\",\"type\":\"search\",\"occurredAt\":\"2013-07-15T09:00:00\"}"))
                .andExpect(status().isAccepted());

        assertThat(eventStore.eventsOf(EntityId.of("alice")).size(), is(1));
    }

    @Test
    public void rejects_an_event_with_a_blank_entity() throws Exception {
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"entityId\":\"\",\"type\":\"search\",\"occurredAt\":\"2013-07-15T09:00:00\"}"))
                .andExpect(status().isBadRequest());
    }
}
