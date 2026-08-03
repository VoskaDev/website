package com.voska.website.controller;

import com.voska.website.dto.request.ContactRequest;
import com.voska.website.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final MailService mailService;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitContactForm(@Valid @RequestBody ContactRequest request) {
        mailService.sendContactMail(request);
    }
}
