package dustit.clientapp.mvp.ui.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import rx.exceptions.OnErrorNotImplementedException;

/**
 * Created by User on 06.03.2018.
 */

public abstract class BaseFeedFragment extends Fragment {

    public interface IBaseFragmentInteraction {
        void notifyFavoriteAdded(FavoriteEntity favoriteEntity);

        void notifyOnScrollChanged(int distance);

        void launchMemView(View holder, MemEntity memEntity);

        void launchMemView(MemEntity memEntity);

        void notifyFeedScrollIdle(boolean b);

        void notifyFeedOnTop();

    }
    private int lastPos = -1;
    private IBaseFragmentInteraction fragmentInteraction;

    public void bindWithBase(Context context) {
        if (context instanceof IBaseFragmentInteraction) {
            fragmentInteraction = (IBaseFragmentInteraction) context;
        } else {
            throw new OnErrorNotImplementedException(new Throwable("Must implement FragmentInteraction"));
        }
    }

    public void launchMemView(View view, MemEntity memEntity) {
        fragmentInteraction.launchMemView(view, memEntity);
    }

    public void launchMemView(MemEntity memEntity) {
        fragmentInteraction.launchMemView(memEntity);
    }

    public void notifyFeedScrollChanged(int scrollY) {
        fragmentInteraction.notifyOnScrollChanged(scrollY);
    }

    public void notifyBase(String id) {
        fragmentInteraction.notifyFavoriteAdded(new FavoriteEntity(id));
    }

    public void notifyFeedScrollIdle(boolean b) {
        fragmentInteraction.notifyFeedScrollIdle(b);
    }

    public void notifyFeedOnTop() {
        fragmentInteraction.notifyFeedOnTop();
    }

    public int getLastPos() {
        return lastPos;
    }

    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }
}
