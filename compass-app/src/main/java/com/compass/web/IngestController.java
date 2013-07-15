package com.compass.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.compass.domain.port.in.IngestEvent;

@Controller
public class IngestController {

    private final IngestEvent ingestEvent;

    @Autowired
    public IngestController(IngestEvent ingestEvent) {
        this.ingestEvent = ingestEvent;
    }

    @RequestMapping(value = "/events", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void ingest(@RequestBody EventRequest request) {
        ingestEvent.ingest(request.toEvent());
    }
}
