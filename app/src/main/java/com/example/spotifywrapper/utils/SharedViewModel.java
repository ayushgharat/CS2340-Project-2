package com.example.spotifywrapper.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * used to maintain fixed state between all fragments in the home activity
 */

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> userJson = new MutableLiveData<>();

    public void setUserJson(String value) {
        userJson.postValue(value);
    }

    public LiveData<String> getUserJSON() {
        return userJson;
    }
}
