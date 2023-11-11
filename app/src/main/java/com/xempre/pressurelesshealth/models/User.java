package com.xempre.pressurelesshealth.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("id")
    Integer idUser;
    String username;

    public Integer getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
