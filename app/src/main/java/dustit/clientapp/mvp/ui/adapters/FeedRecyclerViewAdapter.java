package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.DoubleClickListener;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.containers.Pair;
import dustit.clientapp.utils.managers.ThemeManager;

/**
 * Created by shevc on 05.10.2017.
 * Let's GO!
 */

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<MemEntity> memEntityList = new ArrayList<>();
    private final List<FavoriteEntity> favoriteEntityList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private boolean isLoading = false;

    private boolean sent = false;
    private int offset = 6;
    private int lastPos;
    private int appBarHeight;


    public FeedRecyclerViewAdapter(Context context, IFeedInteractionListener listener, int appBarHeight) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        interactionListener = listener;
        this.appBarHeight = appBarHeight;
        App.get().getAppComponent().inject(this);
    }

    public void onLikePostedSuccesfully(String id) {
        final Pair<Integer, MemEntity> p = findMemAndPositionById(id);
        final MemEntity mem = p.getMem();
        final int pos = p.getPosition();
        final IConstants.OPINION opinion = mem.getOpinion();
        if (opinion != null) {
            if (opinion == IConstants.OPINION.DISLIKED) mem.setDislikes(-1);
            mem.setLikes(1);
            mem.setOpinion(IConstants.OPINION.LIKED);
            notifyItemChanged(pos);
        } else {
            showErrorToast();
        }
    }

    public void onLikeDeletedSuccesfully(String id) {
        final Pair<Integer, MemEntity> pair = findMemAndPositionById(id);
        final MemEntity mem = pair.getMem();
        final int pos = pair.getPosition();
        mem.setLikes(-1);
        mem.setOpinion(IConstants.OPINION.NEUTRAL);
        notifyItemChanged(pos);
    }

    public void onDislikePostedSuccesfully(String id) {
        final Pair<Integer, MemEntity> pair = findMemAndPositionById(id);
        final MemEntity mem = pair.getMem();
        final int pos = pair.getPosition();
        final IConstants.OPINION opinion = mem.getOpinion();
        if (opinion != null) {
            if (opinion == IConstants.OPINION.LIKED) mem.setLikes(-1);
            mem.setDislikes(1);
            mem.setOpinion(IConstants.OPINION.DISLIKED);
            notifyItemChanged(pos);
        } else {
            showErrorToast();
        }
    }

    public void onDislikeDeletedSuccesfully(String id) {
        final Pair<Integer, MemEntity> pair = findMemAndPositionById(id);
        final MemEntity mem = pair.getMem();
        final int pos = pair.getPosition();
        mem.setDislikes(-1);
        mem.setOpinion(IConstants.OPINION.NEUTRAL);
        notifyItemChanged(pos);
    }

    public void addedToFavorites(String id) {
        final Pair<Integer, MemEntity> pair = findMemAndPositionById(id);
        final MemEntity mem = pair.getMem();
        final int pos = pair.getPosition();
        mem.setFavorite(true);
        notifyItemChanged(pos);
    }

    public void onDeletedFromFavorites(String id) {
        final Pair<Integer, MemEntity> pair = findMemAndPositionById(id);
        final MemEntity mem = pair.getMem();
        final int pos = pair.getPosition();
        mem.setFavorite(false);
        notifyItemChanged(pos);
    }

    public void setFavoritesList(List<FavoriteEntity> list) {
        favoriteEntityList.clear();
        favoriteEntityList.addAll(list);
    }

    private void findAndReload() {
        if (memEntityList.size() == 0) return;
        for (int i = 0; i < favoriteEntityList.size(); i++) {
            final Pair<Integer, MemEntity> pair =
                    findMemAndPositionById(favoriteEntityList.get(i).getId());
            if (pair != null) {
                pair.getMem().setFavorite(true);
                notifyItemChanged(pair.getPosition());
            }
        }
    }

    public interface IFeedInteractionListener {
        void reloadFeedPartial(int offset);

        void reloadFeedBase();

        void onMemSelected(View animStart, MemEntity mem);

        void postLike(String id);

        void deleteLike(String id);

        void postDislike(String id);

        void deleteDislike(String id);

        void addToFavorites(String id);

        void deleteFromFavorites(String id);

        void showErrorToast();
    }

    private IFeedInteractionListener interactionListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View v = inflater.inflate(R.layout.item_feed, parent, false);
                final FeedMemViewHolder holder = new FeedMemViewHolder(v);
                return holder;
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
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
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
        if (position == 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, appBarHeight, 0, 0);
            holder.itemView.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            holder.itemView.setLayoutParams(params);
        }
        if (holder instanceof FeedMemViewHolder) {
            final FeedMemViewHolder memViewHolder = (FeedMemViewHolder) holder;
            if (position == 0) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, appBarHeight, 0, 0);
                memViewHolder.itemView.setLayoutParams(params);
            } else {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                memViewHolder.itemView.setLayoutParams(params);
            }
            memViewHolder.isMoreLayoutVisible = false;
            memViewHolder.vgMoreLayout.setVisibility(View.GONE);
            final MemEntity mem = memEntityList.get(position);
            memViewHolder.sdvMemImage.getHierarchy().setProgressBarImage(new ProgressBarDrawable());
            memViewHolder.sdvMemImage.getHierarchy().setRetryImage(context.getResources().getDrawable(R.drawable.ic_reload));
            memViewHolder.sdvMemImage.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setTapToRetryEnabled(true)
                            .setUri(Uri.parse(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId()))
                            .build());
            memViewHolder.tvItemSrc.setText(mem.getSource());
            memViewHolder.sdvMemImage.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/imgs?id=" + mem.getId()));
            memViewHolder.tvLikeCount.setText(mem.getLikes());
            memViewHolder.tvDislikeCount.setText(mem.getDislikes());
            if (mem.isFavorite()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    memViewHolder.addToFavorites.setImageDrawable(context.getDrawable(R.drawable.ic_added_to_favourites));
                } else {
                    memViewHolder.addToFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_added_to_favourites));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    memViewHolder.addToFavorites.setImageDrawable(context.getDrawable(R.drawable.ic_add_to_favourites));
                } else {
                    memViewHolder.addToFavorites.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_to_favourites));
                }
            }
            memViewHolder.ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionManager.beginDelayedTransition((ViewGroup) memViewHolder.itemView);
                    memViewHolder.vgMoreLayout.setVisibility(memViewHolder.isMoreLayoutVisible ? View.GONE : View.VISIBLE);
                    memViewHolder.isMoreLayoutVisible = !memViewHolder.isMoreLayoutVisible;
                }
            });
            final IConstants.OPINION opinion = mem.getOpinion();
            if (opinion != null) {
                switch (opinion) {
                    case LIKED:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            memViewHolder.ivLike.setImageDrawable(context.getDrawable(R.drawable.ic_like_pressed));
                        } else {
                            memViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_pressed));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            memViewHolder.ivDisliked.setImageDrawable(context.getDrawable(R.drawable.ic_dislike));
                        } else {
                            memViewHolder.ivDisliked.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dislike));
                        }
                        break;
                    case DISLIKED:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            memViewHolder.ivLike.setImageDrawable(context.getDrawable(R.drawable.ic_like));
                        } else {
                            memViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            memViewHolder.ivDisliked.setImageDrawable(context.getDrawable(R.drawable.ic_dislike_pressed));
                        } else {
                            memViewHolder.ivDisliked.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dislike_pressed));
                        }
                        break;
                    case NEUTRAL:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            memViewHolder.ivLike.setImageDrawable(context.getDrawable(R.drawable.ic_like));
                        } else {
                            memViewHolder.ivLike.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            memViewHolder.ivDisliked.setImageDrawable(context.getDrawable(R.drawable.ic_dislike));
                        } else {
                            memViewHolder.ivDisliked.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dislike));
                        }
                        break;
                    default:
                        break;
                }
            }
            memViewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (opinion != null) {
                        switch (opinion) {
                            case LIKED:
                                interactionListener.deleteLike(mem.getId());
                                break;
                            case DISLIKED:
                            case NEUTRAL:
                                interactionListener.postLike(mem.getId());
                                break;
                        }
                    }
                }
            });
            memViewHolder.ivDisliked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (opinion != null) {
                        switch (opinion) {
                            case DISLIKED:
                                interactionListener.deleteDislike(mem.getId());
                                break;
                            case LIKED:
                            case NEUTRAL:
                                interactionListener.postDislike(mem.getId());
                                break;
                        }
                    }
                }
            });
            memViewHolder.sdvMemImage.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View view) {
                    interactionListener.onMemSelected(memViewHolder.itemView, mem);
                }

                @Override
                public void onDoubleClick(View v) {
                    if (opinion != null) {
                        switch (opinion) {
                            case DISLIKED:
                            case NEUTRAL:
                                interactionListener.postLike(mem.getId());
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
            memViewHolder.addToFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mem.isFavorite()) {
                        interactionListener.deleteFromFavorites(mem.getId());
                    } else {
                        interactionListener.addToFavorites(mem.getId());
                    }
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
        findAndReload();
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
        findAndReload();
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
        @BindView(R.id.cvItemFeed)
        CardView cvCard;
        @BindView(R.id.clItemFeedMoreLayout)
        ViewGroup vgMoreLayout;
        @BindView(R.id.tvItemFeedSrc)
        TextView tvItemSrc;

        public boolean isMoreLayoutVisible = false;

        public String id;

        FeedMemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public String getId() {
            return id;
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

    private Pair<Integer, MemEntity> findMemAndPositionById(String id) {
        Pair<Integer, MemEntity> pair = null;
        for (int i = 0; i < memEntityList.size(); i++) {
            if (memEntityList.get(i).getId().equals(id)) {
                final MemEntity mem = memEntityList.get(i);
                pair = new Pair<>(i, mem);
                break;
            }
        }
        return pair;
    }

    private void showErrorToast() {
        interactionListener.showErrorToast();
    }
}
