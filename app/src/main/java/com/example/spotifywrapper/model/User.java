package com.example.spotifywrapper.model;

public class User {

    private String display_name;
    private String email;
    private String access_code;
    private Wrapped wrapped_info;

    public Wrapped getWrapped_info() {
        return wrapped_info;
    }

    public void setWrapped_info(Wrapped wrapped_info) {
        this.wrapped_info = wrapped_info;
    }

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
