package com.telekom.csrparser.core.csr;


import com.fasterxml.jackson.annotation.JsonProperty;


public class CSRContent {

    @JsonProperty( "key" )
    public String key;
    @JsonProperty( "value" )
    public String value;


    public CSRContent(String key, String value) {
        this.key = key;
        this.value = value;
    }

}
