package com.compass.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.port.out.ProfileHistoryStore;

@Controller
public class HistoryController {

    private final ProfileHistoryStore profileHistoryStore;

    @Autowired
    public HistoryController(ProfileHistoryStore profileHistoryStore) {
        this.profileHistoryStore = profileHistoryStore;
    }

    @RequestMapping(value = "/entities/{entityId}/history", method = RequestMethod.GET)
    @ResponseBody
    public List<ClassificationResponse> history(@PathVariable String entityId) {
        List<ClassificationResponse> history = new ArrayList<ClassificationResponse>();
        for (Classification classification : profileHistoryStore.historyOf(EntityId.of(entityId))) {
            history.add(ClassificationResponse.from(classification));
        }
        return history;
    }
}
