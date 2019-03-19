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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.containers.Pair;
import dustit.clientapp.utils.managers.ReviewManager;

import static dustit.clientapp.utils.IConstants.IMAGE_URL;

public class UploadsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UploadEntity> uploads;
    private LayoutInflater layoutInflater;
    private IUploadInteraction interactionListener;
    private boolean isLoading = true;
    private boolean isError = false;
    private boolean isMemesEnded = false;
    private Context context;
    private RecyclerView rvFeed;
    private IConstants.ViewMode viewMode = IConstants.ViewMode.LIST;

    public interface IUploadInteraction {
        void reloadFeedBase();

        void onUploadSelected(View animStart, UploadEntity upload);

        boolean isRegistered();

        void onNotRegistered();

        void postLike(UploadEntity upload);

        void deleteLike(UploadEntity upload);

        void postDislike(UploadEntity upload);

        void deleteDislike(UploadEntity upload);

        void onCommentsSelected(View animStart, UploadEntity upload);

        void loadMore(int offset);

        void addToFavourites(UploadEntity uploadEntity);

        void removeFromFavourites(UploadEntity uploadEntity);

        void shareMem(UploadEntity upload);
    }

    public UploadsAdapter(Context context, IUploadInteraction uploadInteraction, RecyclerView rvUploads) {
        layoutInflater = LayoutInflater.from(context);
        uploads = new ArrayList<>();
        uploads.add(null);
        this.context = context;
        interactionListener = uploadInteraction;
        this.rvFeed = rvUploads;
    }

    @Override
    public long getItemId(int position) {
        final UploadEntity upload = uploads.get(position);
        if (upload == null && isError) {
            return -2;
        } else if (isMemesEnded && upload == null) {
            return -3;
        } else if (upload == null) {
            return -1;
        } else {
            return upload.getImageId();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case IConstants.IViewTypes.LOADING:
                return new LoadingViewHolder(layoutInflater.inflate(R.layout.item_loading, parent, false));
            case IConstants.IViewTypes.FAILED_TO_LOAD:
                return new FailedViewHolder(layoutInflater.inflate(R.layout.item_feed_failed_to_load, parent, false));
            case IConstants.IViewTypes.ITEM:
            default:
                switch (viewMode) {
                    case GRID:
                        return new GridUploadViewHolder(layoutInflater.inflate(R.layout.item_each_favorite, parent, false));
                    case LIST:
                    default:
                        return new UploadViewHolder(layoutInflater.inflate(R.layout.item_feed, parent, false));
                }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();

        if (holder instanceof UploadViewHolder) {
            final UploadViewHolder uploadVh = (UploadViewHolder) holder;
            final UploadEntity upload = uploads.get(pos);
            uploadVh.bind(upload);
            GlideApp.with(context)
                    .load(Uri.parse(IMAGE_URL + upload.getImageId()))
                    .placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_color)))
                    .into(uploadVh.itemFeed);
            uploadVh.itemFeedLike.setOnClickListener(v -> {
                if (!interactionListener.isRegistered()) {
                    interactionListener.onNotRegistered();
                    return;
                }
                switch (upload.getOpinion()) {
                    case DISLIKED:
                        upload.setDislikes(-1);
                        upload.setLikes(1);
                        upload.setOpinion(IConstants.OPINION.LIKED);
                        bind(uploadVh, upload);
                        interactionListener.postLike(upload);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case NEUTRAL:
                        upload.setLikes(1);
                        upload.setOpinion(IConstants.OPINION.LIKED);
                        bind(uploadVh, upload);
                        interactionListener.postLike(upload);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case LIKED:
                        upload.setLikes(-1);
                        upload.setOpinion(IConstants.OPINION.NEUTRAL);
                        bind(uploadVh, upload);
                        interactionListener.deleteLike(upload);
                        break;
                }
            });
            uploadVh.itemFeedDislike.setOnClickListener(v -> {
                if (!interactionListener.isRegistered()) {
                    interactionListener.onNotRegistered();
                    return;
                }
                switch (upload.getOpinion()) {
                    case LIKED:
                        upload.setLikes(-1);
                        upload.setDislikes(1);
                        upload.setOpinion(IConstants.OPINION.DISLIKED);
                        bind(uploadVh, upload);
                        interactionListener.postDislike(upload);
                        break;
                    case NEUTRAL:
                        upload.setDislikes(1);
                        upload.setOpinion(IConstants.OPINION.DISLIKED);
                        bind(uploadVh, upload);
                        interactionListener.postDislike(upload);
                        break;
                    case DISLIKED:
                        upload.setDislikes(-1);
                        upload.setOpinion(IConstants.OPINION.NEUTRAL);
                        bind(uploadVh, upload);
                        interactionListener.deleteDislike(upload);
                        break;
                }
            });
            uploadVh.ibAddToFavs.setOnClickListener(v -> {
                if (upload.isFavourite()) {
                    interactionListener.removeFromFavourites(upload);
                } else {
                    interactionListener.addToFavourites(upload);
                }
            });
            uploadVh.ibShare.setOnClickListener(v -> interactionListener.shareMem(upload));
            uploadVh.icComments.setOnClickListener(v -> interactionListener.onCommentsSelected(uploadVh.itemView, upload));
            uploadVh.itemFeed.setOnClickListener(v -> interactionListener.onUploadSelected(uploadVh.itemView, upload));
            if (upload.getUserId() != null && !upload.getUserId().equals("")) {
                uploadVh.sdvUserIcon.setImageURI(IConstants.USER_IMAGE_URL + upload.getUsername());
                uploadVh.tvUsername.setText(upload.getUsername());
            } else {
                uploadVh.sdvUserIcon.setImageResource(R.drawable.icon_memspace);
                uploadVh.tvUsername.setText("MemSpace");
            }
        } else if (holder instanceof GridUploadViewHolder) {
            GridUploadViewHolder grid = (GridUploadViewHolder) holder;
            UploadEntity upload = uploads.get(pos);
            grid.itemView.setOnClickListener(v -> interactionListener.onUploadSelected(grid.itemView, upload));
            grid.bind(upload);
        } else if (holder instanceof FailedViewHolder) {
            final FailedViewHolder failedViewHolder = (FailedViewHolder) holder;
            failedViewHolder.btnRetry.setOnClickListener(v -> {
                if (uploads.size() > 1) {
                    interactionListener.loadMore(uploads.size() - 1);
                    isLoading = true;
                } else {
                    interactionListener.reloadFeedBase();
                    isLoading = true;
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            if (!isLoading && !isMemesEnded) {
                interactionListener.loadMore(uploads.size() - 1);
                isLoading = true;
            }
        }
    }

    private void bind(UploadViewHolder uploadViewHolder, UploadEntity upload) {
        if (uploadViewHolder != null) {
            if (uploadViewHolder.itemView != null) {
                if (uploadViewHolder.itemFeedLike != null) {
                    switch (upload.getOpinion()) {
                        case LIKED:
                            uploadViewHolder.itemFeedLike.setImageResource(R.drawable.ic_like_pressed);
                            uploadViewHolder.itemFeedDislike.setImageResource(R.drawable.ic_dislike);
                            break;
                        case NEUTRAL:
                            uploadViewHolder.itemFeedLike.setImageResource(R.drawable.ic_like);
                            uploadViewHolder.itemFeedDislike.setImageResource(R.drawable.ic_dislike);
                            break;
                        case DISLIKED:
                            uploadViewHolder.itemFeedLike.setImageResource(R.drawable.ic_like);
                            uploadViewHolder.itemFeedDislike.setImageResource(R.drawable.ic_dislike_pressed);
                            break;
                    }

                    if (upload.isFavourite()) {
                        uploadViewHolder.ibAddToFavs.setImageResource(R.drawable.ic_saved);
                    } else {
                        uploadViewHolder.ibAddToFavs.setImageResource(R.drawable.ic_add_to_favourites);
                    }
                    uploadViewHolder.tvCommentsCount.setText(String.valueOf(upload.getCommentsCount()));
                    uploadViewHolder.tvDislikeCount.setText(String.valueOf(upload.getDislikes()));
                    uploadViewHolder.tvLikeCount.setText(String.valueOf(upload.getLikes()));
                    closeSrlForPosition(uploadViewHolder);
                }
            }
        }
    }

    public void updateWhole(List<UploadEntity> list) {
        isLoading = false;
        isError = false;
        uploads.clear();
        uploads.addAll(list);
        uploads.add(null);
        notifyDataSetChanged();
    }

    public void updateAtEnding(List<UploadEntity> list) {
        isLoading = false;
        isError = false;
        int lastPos = uploads.size() - 1;
        uploads.remove(uploads.size() - 1);
        uploads.addAll(list);
        uploads.add(null);
        notifyItemRangeInserted(lastPos, list.size());
    }

    public void onFailedToLoad() {
        isLoading = false;
        isError = true;
        notifyItemChanged(uploads.size() - 1);
    }

    public void onMemesEnded() {
        isMemesEnded = true;
        uploads.remove(uploads.size() - 1);
        notifyItemRemoved(uploads.size() - 1);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    @Override
    public int getItemViewType(int position) {
        final UploadEntity upload = uploads.get(position);
        if (upload == null && isError) {
            return IConstants.IViewTypes.FAILED_TO_LOAD;
        } else if (upload == null) {
            return IConstants.IViewTypes.LOADING;
        } else {
            return IConstants.IViewTypes.ITEM;
        }
    }

    private void closeSrlForPosition(UploadViewHolder holder) {
        holder.srlReveal.close(true);
    }

    public void refreshMem(RefreshedMem refreshedMem) {
        final Pair<Integer, UploadEntity> pair = findMemAndPositionById(refreshedMem.getId());
        if (pair != null) {
            if (pair.getMem() == null) return;
            UploadEntity uploadEntity = pair.getMem();
            uploadEntity.setLikes(refreshedMem.getParsedLikes());
            uploadEntity.setDislikes(refreshedMem.getParsedDislikes());
            uploadEntity.setOpinion(refreshedMem.getOpinion());
            uploadEntity.setFavourite(refreshedMem.isFavourite());
            RecyclerView.ViewHolder holder = rvFeed.findViewHolderForAdapterPosition(pair.getPosition());
            if (holder instanceof UploadViewHolder) {
                UploadViewHolder uploadViewHolder = (UploadViewHolder) rvFeed.findViewHolderForAdapterPosition(pair.getPosition());
                bind(uploadViewHolder, uploadEntity);
            }
        }
    }

    public void restoreMem(RestoreMemEntity restoreMemEntity) {
        final Pair<Integer, UploadEntity> memAndPos = findMemAndPositionById(restoreMemEntity.getId());
        if (memAndPos != null) {
            final UploadEntity upload = memAndPos.getMem();
            if (upload == null) return;
            final int pos = memAndPos.getPosition();
            upload.setLikes(restoreMemEntity.getParsedLikes());
            upload.setDislikes(restoreMemEntity.getParsedDislikes());
            upload.setOpinion(restoreMemEntity.getOpinion());
            upload.setFavourite(restoreMemEntity.isFavourite());
            RecyclerView.ViewHolder holder = rvFeed.findViewHolderForAdapterPosition(pos);
            if (holder instanceof UploadViewHolder) {
                UploadViewHolder uploadViewHolder = (UploadViewHolder) rvFeed.findViewHolderForAdapterPosition(pos);
                bind(uploadViewHolder, upload);
            }
        }
    }

    private Pair<Integer, UploadEntity> findMemAndPositionById(String id) {
        Pair<Integer, UploadEntity> pair = null;
        int memId = Integer.parseInt(id);
        for (int i = 0; i < uploads.size() - 1; i++) {
            if (uploads.get(i).getImageId() == memId) {
                pair = new Pair<>(i, uploads.get(i));
                break;
            }
        }
        return pair;
    }

    public void changeViewMode(IConstants.ViewMode viewMode) {
        this.viewMode = viewMode;
        notifyDataSetChanged();
    }

    public boolean isLoading() {
        return isLoading;
    }

    static class UploadViewHolder extends RecyclerView.ViewHolder {
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

        UploadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(UploadEntity upload) {
            srlReveal.close(false);
            switch (upload.getOpinion()) {
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

            if (upload.isFavourite()) {
                ibAddToFavs.setImageResource(R.drawable.ic_saved);
            } else {
                ibAddToFavs.setImageResource(R.drawable.ic_add_to_favourites);
            }

            tvCommentsCount.setText(String.valueOf(upload.getCommentsCount()));
            tvDislikeCount.setText(String.valueOf(upload.getDislikes()));
            tvLikeCount.setText(String.valueOf(upload.getLikes()));
        }
    }

    static class GridUploadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvFavoriteImage)
        ImageView ivUpload;

        GridUploadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(UploadEntity upload) {
            Context context = itemView.getContext();
            if (context != null)
                GlideApp.with(context)
                        .load(Uri.parse(IMAGE_URL + upload.getImageId()))
                        .placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_color)))
                        .into(ivUpload);
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
}
