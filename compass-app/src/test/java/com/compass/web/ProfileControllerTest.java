package com.compass.web;

import static org.hamcrest.CoreMatchers.containsString;
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
public class ProfileControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void reports_the_primary_profile_and_evidence_from_posted_events() throws Exception {
        for (int i = 0; i < 5; i++) {
            postTopicEvent("sports-eve", "article_view", "sports");
        }
        for (int i = 0; i < 3; i++) {
            postTopicEvent("sports-eve", "video_watch", "sports");
        }
        for (int i = 0; i < 2; i++) {
            postTopicEvent("sports-eve", "article_view", "markets");
        }
        postPlainEvent("sports-eve", "subscribe");

        String body = mockMvc.perform(get("/entities/sports-eve/profile"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(body, containsString("\"primaryProfile\":\"Sports Follower\""));
        assertThat(body, containsString("\"follows_sports\""));
        assertThat(body, containsString("\"Subscriber\""));
    }

    @Test
    public void reports_no_opinion_for_an_unseen_entity() throws Exception {
        String body = mockMvc.perform(get("/entities/profile-ghost/profile"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(body, containsString("\"primaryProfile\":null"));
        assertThat(body, containsString("\"confidence\":0.0"));
    }

    private void postTopicEvent(String entityId, String type, String topic) throws Exception {
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"entityId\":\"" + entityId + "\",\"type\":\"" + type
                        + "\",\"occurredAt\":\"2014-03-01T09:00:00Z\",\"attributes\":{\"topic\":\"" + topic + "\"}}"))
                .andExpect(status().isAccepted());
    }

    private void postPlainEvent(String entityId, String type) throws Exception {
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"entityId\":\"" + entityId + "\",\"type\":\"" + type
                        + "\",\"occurredAt\":\"2014-03-01T09:00:00Z\"}"))
                .andExpect(status().isAccepted());
    }
}
