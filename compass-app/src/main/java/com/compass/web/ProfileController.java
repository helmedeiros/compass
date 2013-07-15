package com.compass.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.compass.domain.model.EntityId;
import com.compass.domain.port.in.ClassifyEntity;

@Controller
public class ProfileController {

    private final ClassifyEntity classifyEntity;

    @Autowired
    public ProfileController(ClassifyEntity classifyEntity) {
        this.classifyEntity = classifyEntity;
    }

    @RequestMapping(value = "/entities/{entityId}/profile", method = RequestMethod.GET)
    @ResponseBody
    public ClassificationResponse profile(@PathVariable String entityId) {
        return ClassificationResponse.from(classifyEntity.classify(EntityId.of(entityId)));
    }
}
