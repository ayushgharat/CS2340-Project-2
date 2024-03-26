package com.example.spotifywrapper.model;

public class User {

    private String display_name;
    private String email;
    private String access_code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getAccess_code() {
        return access_code;
    }
    public void setAccess_code(String access_code) {
        this.access_code = access_code;
    }
}
