package com.github.nemunaskaralius.spring.dbprop.sample;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SampleController {

    private final SampleProperties sampleProperties;

    @GetMapping("/properties")
    SampleProperties getProperties() {
        return sampleProperties;
    }
}
