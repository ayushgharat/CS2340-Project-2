package com.example.spotifywrapper.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifywrapper.model.Wrapped;

import java.util.ArrayList;

/**
 * used to maintain fixed state between all fragments in the home activity
 */

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> userJson = new MutableLiveData<>();
    private MutableLiveData<String> token = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Wrapped>> saved_wrapped = new MutableLiveData<>();

    public void setSaved_wrapped(ArrayList<Wrapped> saved_wrapped) {
        this.saved_wrapped.postValue(saved_wrapped);
    }

    public LiveData<ArrayList<Wrapped>> getSaved_wrapped() {
        return saved_wrapped;
    }

    public void setUserJson(String value) {
        userJson.postValue(value);
    }

    public LiveData<String> getUserJSON() {
        return userJson;
    }

    public void setToken(String value) {
        token.postValue(value);
    }

    public LiveData<String> getToken() {
        return token;
    }
}
