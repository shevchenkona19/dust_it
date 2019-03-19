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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.containers.Pair;
import dustit.clientapp.utils.managers.ReviewManager;

import static dustit.clientapp.utils.IConstants.BASE_URL;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    private boolean showUserIcon = true;

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

    public void hideUserIcons() {
        showUserIcon = false;
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
                        bind(memViewHolder, mem);
                        interactionListener.postLike(mem);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case NEUTRAL:
                        mem.setLikes(1);
                        mem.setOpinion(IConstants.OPINION.LIKED);
                        bind(memViewHolder, mem);
                        interactionListener.postLike(mem);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case LIKED:
                        mem.setLikes(-1);
                        mem.setOpinion(IConstants.OPINION.NEUTRAL);
                        bind(memViewHolder, mem);
                        interactionListener.deleteLike(mem);
                        break;
                }
            });
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
                        bind(memViewHolder, mem);
                        interactionListener.postDislike(mem);
                        break;
                    case NEUTRAL:
                        mem.setDislikes(1);
                        mem.setOpinion(IConstants.OPINION.DISLIKED);
                        bind(memViewHolder, mem);
                        interactionListener.postDislike(mem);
                        break;
                    case DISLIKED:
                        mem.setDislikes(-1);
                        mem.setOpinion(IConstants.OPINION.NEUTRAL);
                        bind(memViewHolder, mem);
                        interactionListener.deleteDislike(mem);
                        break;
                }
            });
            memViewHolder.ibAddToFavs.setOnClickListener(v -> {
                if (mem.isFavorite()) {
                    interactionListener.removeFromFavourites(mem);
                } else {
                    interactionListener.addToFavourites(mem);
                }
            });
            memViewHolder.ibShare.setOnClickListener(v -> interactionListener.shareMem(mem));
            memViewHolder.icComments.setOnClickListener(v -> interactionListener.onCommentsSelected(memViewHolder.itemView, mem));
            memViewHolder.itemFeed.setOnClickListener(v -> interactionListener.onMemSelected(memViewHolder.itemView, mem));
            if (showUserIcon) {
                if (mem.getUserId() != null && !mem.getUserId().equals("")) {
                    memViewHolder.sdvUserIcon.setImageURI(IConstants.USER_IMAGE_URL + mem.getUsername());
                    memViewHolder.tvUsername.setText(mem.getUsername());
                    memViewHolder.sdvUserIcon.setOnClickListener(v -> interactionListener.gotoAccount(mem));
                } else {
                    memViewHolder.sdvUserIcon.setImageResource(R.drawable.icon_memspace);
                    memViewHolder.tvUsername.setText("MemSpace");
                }
            }
            memViewHolder.tvOptions.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(context, memViewHolder.tvOptions);
                menu.inflate(R.menu.feed_item_menu);
                menu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.report_meme) {
                        interactionListener.reportMeme(mem);
                        return true;
                    }
                    return false;
                });
                menu.show();
            });
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

    private void bind(MemViewHolder memViewHolder, MemEntity mem) {
        if (memViewHolder != null) {
            if (memViewHolder.itemView != null) {
                if (memViewHolder.itemFeedLike != null) {
                    switch (mem.getOpinion()) {
                        case LIKED:
                            memViewHolder.itemFeedLike.setImageResource(R.drawable.ic_like_pressed);
                            memViewHolder.itemFeedDislike.setImageResource(R.drawable.ic_dislike);
                            break;
                        case NEUTRAL:
                            memViewHolder.itemFeedLike.setImageResource(R.drawable.ic_like);
                            memViewHolder.itemFeedDislike.setImageResource(R.drawable.ic_dislike);
                            break;
                        case DISLIKED:
                            memViewHolder.itemFeedLike.setImageResource(R.drawable.ic_like);
                            memViewHolder.itemFeedDislike.setImageResource(R.drawable.ic_dislike_pressed);
                            break;
                    }

                    if (mem.isFavorite()) {
                        memViewHolder.ibAddToFavs.setImageResource(R.drawable.ic_saved);
                    } else {
                        memViewHolder.ibAddToFavs.setImageResource(R.drawable.ic_add_to_favourites);
                    }

                    memViewHolder.tvCommentsCount.setText(String.valueOf(mem.getCommentsCount()));
                    memViewHolder.tvDislikeCount.setText(mem.getDislikes());
                    memViewHolder.tvLikeCount.setText(mem.getLikes());
                    closeSrlForPosition(memViewHolder);
                }
            }
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

    private void closeSrlForPosition(MemViewHolder holder) {
        holder.srlReveal.close(true);
    }

    public void refreshMem(RefreshedMem refreshedMem) {
        final Pair<Integer, MemEntity> pair = findMemAndPositionById(refreshedMem.getId());
        if (pair != null) {
            MemEntity mem = pair.getMem();
            if (mem == null) return;
            mem = refreshedMem.populateMemEntity(mem);
            MemViewHolder memViewHolder = (MemViewHolder) rvFeed.findViewHolderForAdapterPosition(pair.getPosition());
            bind(memViewHolder, mem);
        }
    }

    public void restoreMem(RestoreMemEntity restoreMemEntity) {
        final Pair<Integer, MemEntity> memAndPos = findMemAndPositionById(restoreMemEntity.getId());
        if (memAndPos != null) {
            MemEntity mem = memAndPos.getMem();
            if (mem == null) return;
            final int pos = memAndPos.getPosition();
            mem = restoreMemEntity.populateMemEntity(mem);
            MemViewHolder memViewHolder = (MemViewHolder) rvFeed.findViewHolderForAdapterPosition(pos);
            bind(memViewHolder, mem);
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

        void addToFavourites(MemEntity memEntity);

        void removeFromFavourites(MemEntity memEntity);

        void shareMem(MemEntity mem);

        void gotoAccount(MemEntity mem);

        void reportMeme(MemEntity mem);

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
        dustit.clientapp.utils.SwipeRevealLayout srlReveal;
        @BindView(R.id.sdvUserUploadIcon)
        SimpleDraweeView sdvUserIcon;
        @BindView(R.id.tvUsernameUpload)
        TextView tvUsername;
        @BindView(R.id.tvOptions)
        TextView tvOptions;

        MemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(MemEntity mem) {

            srlReveal.close(false);

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
