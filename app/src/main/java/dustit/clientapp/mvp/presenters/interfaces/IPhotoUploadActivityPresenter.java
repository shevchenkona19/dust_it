package dustit.clientapp.mvp.presenters.interfaces;

import android.net.Uri;

public interface IPhotoUploadActivityPresenter {
    void uploadPhoto(Uri upload, String categories);
}
