package com.telekom.csrparser.interfaces.rest;


import com.telekom.csrparser.core.csr.CSRContent;
import com.telekom.csrparser.core.csr.ParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class CSRRestController {

    private final Logger log = LoggerFactory.getLogger( getClass() );

    private final ParserService parserService;


    @Autowired
    public CSRRestController(ParserService parserService) {
        this.parserService = parserService;
    }


    @PostMapping("/upload")
    public List<CSRContent> uploadFile(@RequestParam("file") MultipartFile file){
        List<CSRContent> result = null;

        log.info("begin parsing file");
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error( e.getMessage() );
            result = new ArrayList<>();
            result.add( new CSRContent("ERROR: ", "problem while reading file") );
        }

        if ( result != null ) {
            return result;
        }
        else {
            log.info("input file successfully read");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            return parserService.getContent(inputStream);
        }

    }

}
