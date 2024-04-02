package vn.edu.tdtu.vocawander.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> id = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();

    public void setUsername(String value) {
        username.setValue(value);
    }

    public void setId(String value) { id.setValue(value); }

    public LiveData<String> getId() { return id; }

    public LiveData<String> getUsername() {
        return username;
    }

    public void setEmail(String value) {
        email.setValue(value);
    }

    public LiveData<String> getEmail() {
        return email;
    }
}

