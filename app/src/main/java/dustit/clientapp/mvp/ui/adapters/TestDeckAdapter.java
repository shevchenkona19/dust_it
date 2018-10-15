package dustit.clientapp.mvp.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.utils.IConstants;

/**
 * Created by shevc on 30.09.2017.
 * Let's GO!
 */

public class TestDeckAdapter extends ArrayAdapter<TestMemEntity> {

    public TestDeckAdapter(Context context) {
        super(context, 0);
        if (context instanceof ITestDeckListener) {
            deckListener = (ITestDeckListener) context;
        }
    }

    private boolean isFinished = false;

    public interface ITestDeckListener {
        void onLike();

        void onDislike();

        void finish();
    }

    private ITestDeckListener deckListener;

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CardViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_test_card, parent, false);
            holder = new CardViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CardViewHolder) convertView.getTag();
        }

        final TestMemEntity memEntity = getItem(position);
        if (memEntity != null) {
            holder.ivLike.setOnClickListener((v -> deckListener.onLike()));
            holder.ivDislike.setOnClickListener((v -> deckListener.onDislike()));
            Glide.with(getContext()).load(IConstants.BASE_URL + "/feed/imgs?id=" + memEntity.getMemId())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (!isFinished) {
                                deckListener.finish();
                                isFinished = true;
                            }
                            return isFirstResource;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.hideLoading();
                            holder.ivMem.setImageDrawable(resource);
                            return true;
                        }
                    })
                    .into(holder.ivMem);
        }
        return convertView;
    }

    static class CardViewHolder {
        @BindView(R.id.ivTestCardMem)
        ImageView ivMem;
        @BindView(R.id.btnLikeTest)
        ImageView ivLike;
        @BindView(R.id.btnDislikeTest)
        ImageView ivDislike;
        @BindView(R.id.pbTestCardLoading)
        ProgressBar pbLoading;
        @BindView(R.id.clTestCard)
        ViewGroup layout;

        private CardViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void hideLoading() {
            pbLoading.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        }
    }
}
