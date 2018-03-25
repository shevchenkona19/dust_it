package dustit.clientapp.mvp.presenters.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.IFavoriteViewActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IFavoriteViewActivityView;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import rx.Subscriber;

/**
 * Created by Никита on 12.12.2017.
 */

public class FavoriteViewActivityPresenter extends BasePresenter<IFavoriteViewActivityView> implements IFavoriteViewActivityPresenter {
    @Inject
    DataManager dataManager;

    public FavoriteViewActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void removeFromFavorites(String id) {
        addSubscription(dataManager.removeFromFavorites(id)
                .subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onRemovedFromFavorites();
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print(e.getMessage());
                        getView().onErrorRemovingFromFavorites();
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        if (responseEntity.getResponse() != 200) {
                            getView().onErrorRemovingFromFavorites();
                        }
                    }
                }));
    }

    @Override
    public void downloadImage(String id) {
        Picasso.get()
                .load(Uri.parse(IConstants.BASE_URL + "/feed/imgs?id=" + id))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        String res = saveImage(bitmap);
                        if (res.equals("")) {
                            getView().onDownloadFailed();
                        } else {
                            getView().onDownloaded(res);

                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        getView().onDownloadFailed();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    private String saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/memes");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 1000000;
        n = generator.nextInt(n);
        String fname = "Image-"+ n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return file.getAbsolutePath();
    }

    public String getToken() {
        return dataManager.getToken();
    }
}
