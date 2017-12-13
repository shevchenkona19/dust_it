package dustit.clientapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shevc on 15.10.2017.
 * Let's GO!
 */

public class ResponseEntity {

    @SerializedName("ResponseCode")
    @Expose
    int response;

    public ResponseEntity(int response) {
        this.response = response;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }
}
