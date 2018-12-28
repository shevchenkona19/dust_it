package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dustit.clientapp.customviews.SwipeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.ui.adapters.base.RecyclerSwipeAdapter;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.containers.Pair;
import dustit.clientapp.utils.managers.ReviewManager;

import static dustit.clientapp.utils.IConstants.BASE_URL;

public class FeedRecyclerViewAdapter extends RecyclerSwipeAdapter<RecyclerView.ViewHolder> {
    private List<MemEntity> mems;
    private LayoutInflater layoutInflater;
    private IFeedInteractionListener interactionListener;
    private boolean isLoading = true;
    private boolean isError = false;
    private boolean isMemesEnded = false;
    private boolean isHot = false;
    private int appBarHeight;
    private Context context;
    private RecyclerView rvFeed;

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.srlItemFeedReveal;
    }

    public interface IFeedInteractionListener {
        void reloadFeedBase();

        void onMemSelected(View animStart, MemEntity mem);

        boolean isRegistered();

        void onNotRegistered();

        void postLike(MemEntity mem);

        void deleteLike(MemEntity mem);

        void postDislike(MemEntity mem);

        void deleteDislike(MemEntity mem);

        void onCommentsSelected(View animStart, MemEntity mem);

        void showErrorToast();

        void loadMore(int offset);

        void gotoHot();

        void addToFavourites(String id, int pos);

        void removeFromFavourites(String id, int pos);

        void shareMem(MemEntity mem);
    }

    public FeedRecyclerViewAdapter(Context context, IFeedInteractionListener feedInteractionListener, int appBarHeight, RecyclerView rvFeed) {
        layoutInflater = LayoutInflater.from(context);
        mems = new ArrayList<>();
        mems.add(null);
        this.context = context;
        interactionListener = feedInteractionListener;
        this.appBarHeight = appBarHeight;
        this.rvFeed = rvFeed;
    }

    @Override
    public long getItemId(int position) {
        final MemEntity mem = mems.get(position);
        if (mem == null && isError) {
            return -2;
        } else if (isMemesEnded && mem == null) {
            return -3;
        } else if (mem == null) {
            return -1;
        } else {
            return Long.parseLong(mem.getId());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return new LoadingViewHolder(layoutInflater.inflate(R.layout.item_loading, parent, false));
            case 2:
                return new FailedViewHolder(layoutInflater.inflate(R.layout.item_feed_failed_to_load, parent, false));
            case 3:
                return new MemesEndedViewHolder(layoutInflater.inflate(R.layout.item_feed_memes_ended, parent, false));
            case 4:
                return new HotMemesEndedViewHolder(layoutInflater.inflate(R.layout.item_hot_memes_ended, parent, false));
            case 0:
            default:
                return new MemViewHolder(layoutInflater.inflate(R.layout.item_feed, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        if (pos == 0) {
            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, appBarHeight, 0, 0);
            holder.itemView.setLayoutParams(params);
        } else if (holder.itemView.getLayoutParams() instanceof RelativeLayout.LayoutParams && ((RelativeLayout.LayoutParams) holder.itemView.getLayoutParams()).topMargin != 0) {
            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            holder.itemView.setLayoutParams(params);
        }
        if (holder instanceof MemViewHolder) {
            final MemViewHolder memViewHolder = (MemViewHolder) holder;
            final MemEntity mem = mems.get(pos);
            mItemManger.bind(memViewHolder.itemView, position);
            memViewHolder.bind(mem);
            GlideApp.with(context)
                    .load(Uri.parse(BASE_URL + "/feed/imgs?id=" + mem.getId()))
                    .placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_color)))
                    .into(memViewHolder.itemFeed);
            memViewHolder.itemFeedLike.setOnClickListener(v -> {
                if (!interactionListener.isRegistered()) {
                    interactionListener.onNotRegistered();
                    return;
                }
                switch (mem.getOpinion()) {
                    case DISLIKED:
                        mem.setDislikes(-1);
                        mem.setLikes(1);
                        mem.setOpinion(IConstants.OPINION.LIKED);
                        notifyItemChanged(pos);
                        interactionListener.postLike(mem);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case NEUTRAL:
                        mem.setLikes(1);
                        mem.setOpinion(IConstants.OPINION.LIKED);
                        notifyItemChanged(pos);
                        interactionListener.postLike(mem);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case LIKED:
                        mem.setLikes(-1);
                        mem.setOpinion(IConstants.OPINION.NEUTRAL);
                        notifyItemChanged(pos);
                        interactionListener.deleteLike(mem);
                        break;
                }
            });
            memViewHolder.ibAddToFavs.setOnClickListener(v -> {
                if (mem.isFavorite()) {
                    interactionListener.removeFromFavourites(mem.getId(), pos);
                } else {
                    interactionListener.addToFavourites(mem.getId(), pos);
                }
            });
            memViewHolder.ibShare.setOnClickListener(v -> interactionListener.shareMem(mem));
            memViewHolder.itemFeedDislike.setOnClickListener(v -> {
                if (!interactionListener.isRegistered()) {
                    interactionListener.onNotRegistered();
                    return;
                }
                switch (mem.getOpinion()) {
                    case LIKED:
                        mem.setLikes(-1);
                        mem.setDislikes(1);
                        mem.setOpinion(IConstants.OPINION.DISLIKED);
                        notifyItemChanged(pos);
                        interactionListener.postDislike(mem);
                        break;
                    case NEUTRAL:
                        mem.setDislikes(1);
                        mem.setOpinion(IConstants.OPINION.DISLIKED);
                        notifyItemChanged(pos);
                        interactionListener.postDislike(mem);
                        break;
                    case DISLIKED:
                        mem.setDislikes(-1);
                        mem.setOpinion(IConstants.OPINION.NEUTRAL);
                        notifyItemChanged(pos);
                        interactionListener.deleteDislike(mem);
                        break;
                }
            });
            memViewHolder.icComments.setOnClickListener(v -> interactionListener.onCommentsSelected(memViewHolder.itemView, mem));
            memViewHolder.itemFeed.setOnClickListener(v -> interactionListener.onMemSelected(memViewHolder.itemView, mem));
        } else if (holder instanceof FailedViewHolder) {
            final FailedViewHolder failedViewHolder = (FailedViewHolder) holder;
            failedViewHolder.btnRetry.setOnClickListener(v -> {
                if (mems.size() > 1) {
                    interactionListener.loadMore(mems.size() - 1);
                    isLoading = true;
                } else {
                    interactionListener.reloadFeedBase();
                    isLoading = true;
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            if (!isLoading && !isMemesEnded) {
                interactionListener.loadMore(mems.size() - 1);
                isLoading = true;
            }
        } else if (holder instanceof MemesEndedViewHolder) {
            MemesEndedViewHolder memesEndedViewHolder = (MemesEndedViewHolder) holder;
            memesEndedViewHolder.btnToHot.setOnClickListener(v -> interactionListener.gotoHot());
        }
    }

    public void setIsHot() {
        isHot = true;
    }

    public void updateWhole(List<MemEntity> list) {
        isLoading = false;
        isError = false;
        mems.clear();
        mems.addAll(list);
        mems.add(null);
        notifyDataSetChanged();
    }

    public void updateAtEnding(List<MemEntity> list) {
        isLoading = false;
        isError = false;
        int lastPos = mems.size() - 1;
        mems.remove(mems.size() - 1);
        mems.addAll(list);
        mems.add(null);
        notifyItemRangeInserted(lastPos, list.size());
    }

    public void onFailedToLoad() {
        isLoading = false;
        isError = true;
        notifyItemChanged(mems.size() - 1);
    }

    public void onMemesEnded() {
        isMemesEnded = true;
        notifyItemChanged(mems.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mems.size();
    }

    @Override
    public int getItemViewType(int position) {
        final MemEntity mem = mems.get(position);
        if (mem == null && isError) {
            return 2;
        } else if (isMemesEnded && mem == null) {
            if (isHot) return 4;
            else return 3;
        } else if (mem == null) {
            return 1;
        } else {
            return 0;
        }
    }

    public void onAddedToFavourites(int position) {
        mems.get(position).setFavorite(true);
        closeSrlForPosition(position);
    }

    public void onRemovedFromFavourites(int position) {
        mems.get(position).setFavorite(false);
        closeSrlForPosition(position);
    }

    private void closeSrlForPosition(int position) {
        RecyclerView.ViewHolder vh = rvFeed.findViewHolderForAdapterPosition(position);
        if (vh != null) {
            if (vh instanceof MemViewHolder) {
                MemViewHolder mem = (MemViewHolder) vh;
                if (mems.get(position).isFavorite()) {
                    mem.ibAddToFavs.setImageResource(R.drawable.ic_saved);
                } else {
                    mem.ibAddToFavs.setImageResource(R.drawable.ic_add_to_favourites);
                }
                mem.srlReveal.close(true);
            }
        }
    }

    public void refreshMem(RefreshedMem refreshedMem) {
        final Pair<Integer, MemEntity> pair = findMemAndPositionById(refreshedMem.getId());
        if (pair != null) {
            if (pair.getMem() == null) return;
            pair.getMem().setLikes(refreshedMem.getLikes());
            pair.getMem().setDislikes(refreshedMem.getDislikes());
            pair.getMem().setOpinion(refreshedMem.getOpinion());
            notifyItemChanged(pair.getPosition());
        }
    }

    public void restoreMem(RestoreMemEntity restoreMemEntity) {
        final Pair<Integer, MemEntity> memAndPos = findMemAndPositionById(restoreMemEntity.getId());
        if (memAndPos != null) {
            final MemEntity mem = memAndPos.getMem();
            if (mem == null) return;
            final int pos = memAndPos.getPosition();
            mem.setLikes(restoreMemEntity.getLikes());
            mem.setDislikes(restoreMemEntity.getDislikes());
            mem.setOpinion(restoreMemEntity.getOpinion());
            notifyItemChanged(pos);
        }
    }

    private Pair<Integer, MemEntity> findMemAndPositionById(String id) {
        Pair<Integer, MemEntity> pair = null;
        for (int i = 0; i < mems.size() - 1; i++) {
            if (mems.get(i).getId().equals(id)) {
                pair = new Pair<>(i, mems.get(i));
                break;
            }
        }
        return pair;
    }

    static class MemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvItemFeed)
        ImageView itemFeed;
        @BindView(R.id.ivItemFeedIsLiked)
        ImageView itemFeedLike;
        @BindView(R.id.ivItemFeedDisliked)
        ImageView itemFeedDislike;
        @BindView(R.id.tvItemFeedLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.tvItemFeedDislikeCount)
        TextView tvDislikeCount;
        @BindView(R.id.tvCommentsCount)
        TextView tvCommentsCount;
        @BindView(R.id.ivItemFeedComments)
        View icComments;
        @BindView(R.id.clItemFeedLayout)
        ConstraintLayout clLayout;
        @BindView(R.id.ibAddToFavsBack)
        ImageButton ibAddToFavs;
        @BindView(R.id.ibShareBack)
        ImageButton ibShare;
        @BindView(R.id.srlItemFeedReveal)
        SwipeLayout srlReveal;

        MemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(MemEntity mem) {

            switch (mem.getOpinion()) {
                case LIKED:
                    itemFeedLike.setImageResource(R.drawable.ic_like_pressed);
                    itemFeedDislike.setImageResource(R.drawable.ic_dislike);
                    break;
                case NEUTRAL:
                    itemFeedLike.setImageResource(R.drawable.ic_like);
                    itemFeedDislike.setImageResource(R.drawable.ic_dislike);
                    break;
                case DISLIKED:
                    itemFeedLike.setImageResource(R.drawable.ic_like);
                    itemFeedDislike.setImageResource(R.drawable.ic_dislike_pressed);
                    break;
            }

            if (mem.isFavorite()) {
                ibAddToFavs.setImageResource(R.drawable.ic_saved);
            } else {
                ibAddToFavs.setImageResource(R.drawable.ic_add_to_favourites);
            }

            tvCommentsCount.setText(String.valueOf(mem.getCommentsCount()));
            tvDislikeCount.setText(mem.getDislikes());
            tvLikeCount.setText(mem.getLikes());
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class FailedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btnItemFeedRetry)
        Button btnRetry;

        FailedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class HotMemesEndedViewHolder extends RecyclerView.ViewHolder {
        HotMemesEndedViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class MemesEndedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btnFeedToHot)
        Button btnToHot;

        MemesEndedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
