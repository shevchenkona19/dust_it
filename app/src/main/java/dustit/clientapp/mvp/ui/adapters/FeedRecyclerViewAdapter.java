package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.utils.IConstants;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MemEntity> memEntityList;
    private LayoutInflater inflater;
    private Context context;
    private boolean isLoading = false;

    private boolean sent = false;
    private int offset = 6;
    private int lastPos;

    private boolean flag = false;

    @Inject
    DataManager dataManager;

    public FeedRecyclerViewAdapter(Context context, IFeedInteractionListener listener) {
        memEntityList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.context = context;
        interactionListener = listener;
        App.get().getAppComponent().inject(this);
    }

    public void onLikePostedSuccesfully(String id) {
        for (int i = 0; i < memEntityList.size(); i++) {
            if (memEntityList.get(i).getId().equals(id)) {
                MemEntity mem = memEntityList.get(i);
                if (mem.isDisliked()) {
                    mem.setDisliked(false);
                    mem.setDislikes(String.valueOf(Integer.parseInt(mem.getDislikes()) - 1));
                }
                mem.setLikes(String.valueOf(Integer.parseInt(mem.getLikes()) + 1));
                mem.setLiked(true);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void onLikeDeletedSuccesfully(String id) {
        for (int i = 0; i < memEntityList.size(); i++) {
            if (memEntityList.get(i).getId().equals(id)) {
                MemEntity mem = memEntityList.get(i);
                mem.setLiked(false);
                mem.setLikes(String.valueOf(Integer.parseInt(mem.getLikes()) - 1));
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void onDislikePostedSuccesfully(String id) {
        for (int i = 0; i < memEntityList.size(); i++) {
            if (memEntityList.get(i).getId().equals(id)) {
                MemEntity mem = memEntityList.get(i);
                if (mem.isLiked()) {
                    mem.setLiked(false);
                    mem.setLikes(String.valueOf(Integer.parseInt(mem.getLikes()) - 1));
                }
                mem.setDislikes(String.valueOf(Integer.parseInt(mem.getDislikes()) + 1));
                mem.setDisliked(true);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void onDislikeDeletedSuccesfully(String id) {
        for (int i = 0; i < memEntityList.size(); i++) {
            if (memEntityList.get(i).getId().equals(id)) {
                MemEntity mem = memEntityList.get(i);
                mem.setDislikes(String.valueOf(Integer.parseInt(mem.getDislikes()) - 1));
                mem.setDisliked(false);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void addedToFavorites(String id) {
        for (int i = 0; i < memEntityList.size(); i++) {
            if (memEntityList.get(i).getId().equals(id)) {
                MemEntity mem = memEntityList.get(i);
                mem.setFavorite(true);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public interface IFeedInteractionListener {
        void reloadFeedPartial(int offset);

        void reloadFeedBase();

        void onMemSelected(MemEntity mem);

        void postLike(String id);

        void deleteLike(String id);

        void postDislike(String id);

        void deleteDislike(String id);

        void addToFavorites(String id);
    }

    private IFeedInteractionListener interactionListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.item_feed, parent, false);
                return new FeedMemViewHolder(v);
            case 1:
                if (isLoading) {
                    View v1 = inflater.inflate(R.layout.item_feed_loading, parent, false);
                    return new FeedLoadingViewHolder(v1);
                } else {
                    View v2 = inflater.inflate(R.layout.item_feed_failed_to_load, parent, false);
                    return new FeedFailedToLoadViewHolder(v2);
                }
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        if (pos % 5 == 0 && pos != 0) {
            if (pos > lastPos) {
                if (sent && pos == 5) {
                    sent = false;
                } else {
                    sent = true;
                    interactionListener.reloadFeedPartial(offset);
                    offset += 5;
                    lastPos = pos;
                }
            }
        }
        if (holder instanceof FeedMemViewHolder) {
            final FeedMemViewHolder memViewHolder = (FeedMemViewHolder) holder;
            final MemEntity mem = memEntityList.get(position);
            memViewHolder.sdvMemImage.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
            memViewHolder.sdvMemImage.getHierarchy().setRetryImage(context.getResources().getDrawable(R.drawable.ic_reload));
            memViewHolder.sdvMemImage.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setTapToRetryEnabled(true)
                            .setUri(Uri.parse(IConstants.BASE_URL + "/client/imgs?token=" + dataManager.getToken() + "&id=" + mem.getId()))
                            .build());
            memViewHolder.sdvMemImage.setImageURI(Uri.parse(IConstants.BASE_URL + "/client/imgs?token=" + dataManager.getToken() + "&id=" + mem.getId()));
            memViewHolder.tvLikeCount.setText(mem.getLikes());
            memViewHolder.tvDislikeCount.setText(mem.getDislikes());
            if (mem.isLiked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    memViewHolder.ivLike.setImageDrawable(context.getDrawable(R.drawable.ic_like));
                } else {
                    memViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    memViewHolder.ivLike.setImageDrawable(context.getDrawable(R.drawable.ic_like_pressed));
                } else {
                    memViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_pressed));
                }
            }
            if (mem.isDisliked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    memViewHolder.ivDisliked.setImageDrawable(context.getDrawable(R.drawable.ic_dislike));
                } else {
                    memViewHolder.ivDisliked.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dislike));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    memViewHolder.ivDisliked.setImageDrawable(context.getDrawable(R.drawable.ic_dislike_pressed));
                } else {
                    memViewHolder.ivDisliked.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dislike_pressed));
                }
            }
            memViewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mem.isLiked()) {
                        interactionListener.deleteLike(mem.getId());
                    } else {
                        interactionListener.postLike(mem.getId());
                    }
                }
            });
            memViewHolder.ivDisliked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mem.isDisliked()) {
                        interactionListener.deleteDislike(mem.getId());
                    } else {
                        interactionListener.postDislike(mem.getId());
                    }
                }
            });
            memViewHolder.sdvMemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    interactionListener.onMemSelected(mem);
                }
            });
            memViewHolder.addToFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    interactionListener.addToFavorites(mem.getId());
                }
            });
        } else if (holder instanceof FeedFailedToLoadViewHolder) {
            final FeedFailedToLoadViewHolder failedToLoadViewHolder = (FeedFailedToLoadViewHolder) holder;
            failedToLoadViewHolder.btnRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (memEntityList.size() > 1) {
                        interactionListener.reloadFeedPartial(memEntityList.size());
                    } else {
                        interactionListener.reloadFeedBase();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return memEntityList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (memEntityList.get(position) == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void onStartLoading() {
        Handler handler = new Handler();
        isLoading = true;
        if (!memEntityList.contains(null)) {
            memEntityList.add(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(memEntityList.size() - 1);
                }
            }, 100);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(memEntityList.size() - 1);
                }
            }, 100);
        }
    }

    public void onFailedToLoad() {
        isLoading = false;
        notifyItemChanged(memEntityList.size() - 1);
    }

    public void updateListWhole(List<MemEntity> list) {
        if (isLoading) {
            isLoading = false;
        }
        memEntityList.clear();
        memEntityList.addAll(list);
        lastPos = 0;
        offset = 6;
        notifyDataSetChanged();
    }

    public void updateListAtEnding(List<MemEntity> list) {
        if (isLoading) {
            isLoading = false;
            int lastPos = memEntityList.size() - 1;
            memEntityList.remove(null);
            notifyItemChanged(lastPos);
        }
        int currPos = memEntityList.size() - 1;
        memEntityList.addAll(list);
        notifyItemRangeInserted(currPos, list.size());
    }

    public List<MemEntity> getList() {
        return memEntityList;
    }

    public MemEntity getItem(int position) {
        return memEntityList.get(position);
    }

    static class FeedMemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvItemFeed)
        SimpleDraweeView sdvMemImage;
        @BindView(R.id.ivItemFeedIsLiked)
        ImageView ivLike;
        @BindView(R.id.tvItemFeedLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.ivItemFeedMore)
        ImageView ivMore;
        @BindView(R.id.ivItemFeedAddToFavorites)
        ImageView addToFavorites;
        @BindView(R.id.ivItemFeedDisliked)
        ImageView ivDisliked;
        @BindView(R.id.tvItemFeedDislikeCount)
        TextView tvDislikeCount;

        FeedMemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FeedLoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbItemFeedLoading)
        ProgressBar pbLoading;

        FeedLoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            pbLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }
    }

    static class FeedFailedToLoadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btnItemFeedRetry)
        Button btnRetry;

        FeedFailedToLoadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
