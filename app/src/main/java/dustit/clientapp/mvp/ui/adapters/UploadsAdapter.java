package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import dustit.clientapp.utils.GlideRequests;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
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
    private GlideRequests glide;
    private IConstants.ViewMode viewMode = IConstants.ViewMode.LIST;

    public UploadsAdapter(Context context, IUploadInteraction uploadInteraction, RecyclerView rvUploads) {
        layoutInflater = LayoutInflater.from(context);
        uploads = new ArrayList<>();
        uploads.add(null);
        glide = GlideApp.with(context);
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
        if (viewMode == IConstants.ViewMode.LIST && isMemesEnded && pos == uploads.size() - 1) {
            final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 0, 180);
            holder.itemView.setLayoutParams(params);
        }
        if (holder instanceof UploadViewHolder) {
            final UploadViewHolder uploadVh = (UploadViewHolder) holder;
            final UploadEntity upload = uploads.get(pos);
            uploadVh.bind(upload, glide);
            glide
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
                        upload.addDislikes(-1);
                        upload.addLikes(1);
                        upload.setOpinion(IConstants.OPINION.LIKED);
                        bind(uploadVh, upload);
                        interactionListener.postLike(upload);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case NEUTRAL:
                        upload.addLikes(1);
                        upload.setOpinion(IConstants.OPINION.LIKED);
                        bind(uploadVh, upload);
                        interactionListener.postLike(upload);
                        if (context != null)
                            ReviewManager.get().positiveCount(new WeakReference<>(context));
                        break;
                    case LIKED:
                        upload.addLikes(-1);
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
                        upload.addLikes(-1);
                        upload.addDislikes(1);
                        upload.setOpinion(IConstants.OPINION.DISLIKED);
                        bind(uploadVh, upload);
                        interactionListener.postDislike(upload);
                        break;
                    case NEUTRAL:
                        upload.addDislikes(1);
                        upload.setOpinion(IConstants.OPINION.DISLIKED);
                        bind(uploadVh, upload);
                        interactionListener.postDislike(upload);
                        break;
                    case DISLIKED:
                        upload.addDislikes(-1);
                        upload.setOpinion(IConstants.OPINION.NEUTRAL);
                        bind(uploadVh, upload);
                        interactionListener.deleteDislike(upload);
                        break;
                }
            });
            uploadVh.icComments.setOnClickListener(v -> interactionListener.onCommentsSelected(uploadVh.itemView, upload));
            uploadVh.itemFeed.setOnClickListener(v -> interactionListener.onUploadSelected(uploadVh.itemView, upload));
            if (upload.getUserId() != -1) {
                uploadVh.sdvUserIcon.setImageURI(IConstants.USER_IMAGE_URL + upload.getUsername());
                uploadVh.tvUsername.setText(upload.getUsername());
            } else {
                uploadVh.sdvUserIcon.setImageResource(R.drawable.icon_memspace);
                uploadVh.tvUsername.setText("MemSpace");
            }
            uploadVh.tvOptions.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(context, uploadVh.tvOptions);
                menu.inflate(R.menu.feed_item_menu);
                if (upload.isFavourite())
                    menu.getMenu().getItem(2).setTitle(context.getString(R.string.remove_from_favorites));
                else
                    menu.getMenu().getItem(2).setTitle(context.getString(R.string.add_to_favourites_title));
                menu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.report_meme:
                            interactionListener.reportMeme(upload);
                            return true;
                        case R.id.addToFavorites:
                            if (upload.isFavourite())
                                interactionListener.removeFromFavourites(upload);
                            else
                                interactionListener.addToFavourites(upload);
                            return true;
                        case R.id.shareMeme:
                            interactionListener.shareMem(upload);
                            return true;
                        default:
                            return false;
                    }
                });
                menu.show();
            });
        } else if (holder instanceof GridUploadViewHolder) {
            GridUploadViewHolder grid = (GridUploadViewHolder) holder;
            UploadEntity upload = uploads.get(pos);
            grid.itemView.setOnClickListener(v -> interactionListener.onUploadSelected(grid.itemView, upload));
            grid.bind(upload, glide);
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
            if (uploadViewHolder.itemFeedLike != null) {
                switch (upload.getOpinion()) {
                    case LIKED:
                        glide.load(R.drawable.ic_like_pressed).into(uploadViewHolder.itemFeedLike);
                        glide.load(R.drawable.ic_dislike).into(uploadViewHolder.itemFeedDislike);
                        break;
                    case NEUTRAL:
                        glide.load(R.drawable.ic_like).into(uploadViewHolder.itemFeedLike);
                        glide.load(R.drawable.ic_dislike).into(uploadViewHolder.itemFeedDislike);
                        break;
                    case DISLIKED:
                        glide.load(R.drawable.ic_like).into(uploadViewHolder.itemFeedLike);
                        glide.load(R.drawable.ic_dislike_pressed).into(uploadViewHolder.itemFeedDislike);
                        break;
                }
                uploadViewHolder.tvCommentsCount.setText(String.valueOf(upload.getCommentsCount()));
                uploadViewHolder.tvDislikeCount.setText(String.valueOf(upload.getDislikes()));
                uploadViewHolder.tvLikeCount.setText(String.valueOf(upload.getLikes()));
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

    public void refreshMem(RefreshedMem refreshedMem) {
        final Pair<Integer, UploadEntity> pair = findMemAndPositionById(refreshedMem.getId());
        L.print("refresh start");
        if (pair != null) {
            L.print("pair not null");
            if (pair.getMem() == null) return;
            L.print("Not null");
            UploadEntity uploadEntity = pair.getMem();
            uploadEntity.setLikes(refreshedMem.getLikes());
            uploadEntity.setDislikes(refreshedMem.getDislikes());
            uploadEntity.setOpinion(refreshedMem.getOpinion());
            uploadEntity.setFavourite(refreshedMem.isFavourite());
            notifyItemChanged(pair.getPosition());
        }
    }

    public void restoreMem(RestoreMemEntity restoreMemEntity) {
        final Pair<Integer, UploadEntity> memAndPos = findMemAndPositionById(restoreMemEntity.getId());
        if (memAndPos != null) {
            final UploadEntity upload = memAndPos.getMem();
            if (upload == null) return;
            final int pos = memAndPos.getPosition();
            upload.setLikes(restoreMemEntity.getLikes());
            upload.setDislikes(restoreMemEntity.getDislikes());
            upload.setOpinion(restoreMemEntity.getOpinion());
            upload.setFavourite(restoreMemEntity.isFavourite());
            notifyItemChanged(pos);
        }
    }

    private Pair<Integer, UploadEntity> findMemAndPositionById(int id) {
        for (int i = 0; i < uploads.size(); i++) {
            UploadEntity upload = uploads.get(i);
            if (upload != null && upload.getImageId() == id) {
                return new Pair<>(i, uploads.get(i));
            }
        }
        return null;
    }

    public void changeViewMode(IConstants.ViewMode viewMode) {
        this.viewMode = viewMode;
        notifyDataSetChanged();
    }

    public boolean isLoading() {
        return isLoading;
    }

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

        void reportMeme(UploadEntity upload);
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
        @BindView(R.id.sdvUserUploadIcon)
        SimpleDraweeView sdvUserIcon;
        @BindView(R.id.tvUsernameUpload)
        TextView tvUsername;
        @BindView(R.id.tvOptions)
        TextView tvOptions;

        UploadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(UploadEntity upload, GlideRequests glide) {
            switch (upload.getOpinion()) {
                case LIKED:
                    glide.load(R.drawable.ic_like_pressed).into(itemFeedLike);
                    glide.load(R.drawable.ic_dislike).into(itemFeedDislike);
                    break;
                case NEUTRAL:
                    glide.load(R.drawable.ic_like).into(itemFeedLike);
                    glide.load(R.drawable.ic_dislike).into(itemFeedDislike);
                    break;
                case DISLIKED:
                    glide.load(R.drawable.ic_like).into(itemFeedLike);
                    glide.load(R.drawable.ic_dislike_pressed).into(itemFeedDislike);
                    break;
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

        public void bind(UploadEntity upload, GlideRequests glide) {
            Context context = itemView.getContext();
            if (context != null)
                glide
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
