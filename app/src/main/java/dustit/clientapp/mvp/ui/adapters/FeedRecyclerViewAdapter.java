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
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.RefreshedMem;
import dustit.clientapp.mvp.model.entities.RestoreMemEntity;
import dustit.clientapp.utils.GlideRequests;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.containers.Pair;
import dustit.clientapp.utils.managers.ReviewManager;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static dustit.clientapp.utils.IConstants.BASE_URL;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MEM_VIEW_TYPE = 0;
    private static final int AD_VIEW_TYPE = 1;
    private static final int INFO_VIEW_TYPE = 2;
    private List<MemEntity> mems;
    private List<UnifiedNativeAd> preloadAdList;
    private LayoutInflater layoutInflater;
    private IFeedInteractionListener interactionListener;
    private boolean isLoading = true;
    private boolean isError = false;
    private boolean isMemesEnded = false;
    private boolean isHot = false;
    private Context context;
    private RecyclerView rvFeed;
    private GlideRequests glide;

    public FeedRecyclerViewAdapter(Context context, IFeedInteractionListener feedInteractionListener, RecyclerView rvFeed) {
        layoutInflater = LayoutInflater.from(context);
        mems = new ArrayList<>();
        mems.add(null);
        preloadAdList = new ArrayList<>();
        this.context = context;
        interactionListener = feedInteractionListener;
        interactionListener.getMoreAds();
        this.rvFeed = rvFeed;
    }

    public void addPreloadAds(List<UnifiedNativeAd> nativeAds) {
        preloadAdList.addAll(nativeAds);
    }

    @Override
    public long getItemId(int position) {
        final MemEntity mem = mems.get(position);
        if (position % 5 == 0 && position != 0) {
            return position;
        } else if (mem == null) {
            return -2;
        } else {
            return mem.getId();
        }
    }

    @Override
    public int getItemCount() {
        return mems.size();
    }

    @Override
    public int getItemViewType(int position) {
        final MemEntity mem = mems.get(position);
        if (position % 5 == 0 && position != 0) {
            return AD_VIEW_TYPE;
        } else if (mem == null) {
            return INFO_VIEW_TYPE;
        } else {
            return MEM_VIEW_TYPE;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case INFO_VIEW_TYPE:
                return new InfoViewHolder(layoutInflater.inflate(R.layout.item_info, parent, false));
            case AD_VIEW_TYPE:
                return new AdViewHolder(layoutInflater.inflate(R.layout.item_ad_view, parent, false));
            case MEM_VIEW_TYPE:
            default:
                MemViewHolder memViewHolder = new MemViewHolder(layoutInflater.inflate(R.layout.item_feed, parent, false));
                bindClicksForMemView(memViewHolder);
                return memViewHolder;
        }
    }

    private void bindClicksForMemView(MemViewHolder memViewHolder) {
        memViewHolder.itemFeedLike.setOnClickListener(v -> {
            if (!interactionListener.isRegistered()) {
                interactionListener.onNotRegistered();
                return;
            }
            MemEntity mem = mems.get(memViewHolder.getAdapterPosition());
            switch (mem.getOpinion()) {
                case DISLIKED:
                    mem.addDislikes(-1);
                    mem.addLikes(1);
                    mem.setOpinion(IConstants.OPINION.LIKED);
                    bind(memViewHolder, mem);
                    interactionListener.postLike(mem);
                    if (context != null)
                        ReviewManager.get().positiveCount(new WeakReference<>(context));
                    break;
                case NEUTRAL:
                    mem.addLikes(1);
                    mem.setOpinion(IConstants.OPINION.LIKED);
                    bind(memViewHolder, mem);
                    interactionListener.postLike(mem);
                    if (context != null)
                        ReviewManager.get().positiveCount(new WeakReference<>(context));
                    break;
                case LIKED:
                    mem.addLikes(-1);
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
            MemEntity mem = mems.get(memViewHolder.getAdapterPosition());
            switch (mem.getOpinion()) {
                case LIKED:
                    mem.addLikes(-1);
                    mem.addDislikes(1);
                    mem.setOpinion(IConstants.OPINION.DISLIKED);
                    bind(memViewHolder, mem);
                    interactionListener.postDislike(mem);
                    break;
                case NEUTRAL:
                    mem.addDislikes(1);
                    mem.setOpinion(IConstants.OPINION.DISLIKED);
                    bind(memViewHolder, mem);
                    interactionListener.postDislike(mem);
                    break;
                case DISLIKED:
                    mem.addDislikes(-1);
                    mem.setOpinion(IConstants.OPINION.NEUTRAL);
                    bind(memViewHolder, mem);
                    interactionListener.deleteDislike(mem);
                    break;
            }
        });
        memViewHolder.icComments.setOnClickListener(v -> {
            MemEntity mem = mems.get(memViewHolder.getAdapterPosition());
            interactionListener.onCommentsSelected(memViewHolder.itemView, mem);
        });
        memViewHolder.itemFeed.setOnClickListener(v -> {
            MemEntity mem = mems.get(memViewHolder.getAdapterPosition());
            interactionListener.onMemSelected(memViewHolder.itemView, mem);
        });
        memViewHolder.tvOptions.setOnClickListener(v -> {
            MemEntity mem = mems.get(memViewHolder.getAdapterPosition());
            PopupMenu menu = new PopupMenu(context, memViewHolder.tvOptions);
            menu.inflate(R.menu.feed_item_menu);
            if (mem.isFavorite())
                menu.getMenu().getItem(2).setTitle(context.getString(R.string.remove_from_favorites));
            else
                menu.getMenu().getItem(2).setTitle(context.getString(R.string.add_to_favourites_title));

            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.report_meme:
                        interactionListener.reportMeme(mem);
                        return true;
                    case R.id.addToFavorites:
                        if (mem.isFavorite())
                            interactionListener.removeFromFavourites(mem);
                        else
                            interactionListener.addToFavourites(mem);
                        return true;
                    case R.id.shareMeme:
                        interactionListener.shareMem(mem);
                        return true;
                    default:
                        return false;
                }
            });
            menu.show();
        });
    }

    public void setGlideLoader(GlideRequests glide) {
        this.glide = glide;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int pos = holder.getAdapterPosition();
        if (holder instanceof MemViewHolder) {
            final MemViewHolder memViewHolder = (MemViewHolder) holder;
            final MemEntity mem = mems.get(pos);
            memViewHolder.bind(mem, glide);
            glide
                    .load(Uri.parse(BASE_URL + "/feed/imgs?id=" + mem.getId()))
                    .placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.placeholder_color)))
                    .transition(withCrossFade())
                    .into(memViewHolder.itemFeed);
            if (mem.getUserId() != -1) {
                memViewHolder.sdvUserIcon.setImageURI(IConstants.USER_IMAGE_URL + mem.getUsername());
                memViewHolder.tvUsername.setText(mem.getUsername());
                memViewHolder.sdvUserIcon.setOnClickListener(v -> interactionListener.gotoAccount(mem));
            } else {
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                        .path(String.valueOf(R.drawable.icon_memspace))
                        .build();
                memViewHolder.sdvUserIcon.setImageURI(uri);
                memViewHolder.tvUsername.setText("MemSpace");
                memViewHolder.sdvUserIcon.setOnClickListener(null);
            }
        } else if (holder instanceof AdViewHolder) {
            if (preloadAdList.size() > 0) {
                AdViewHolder adViewHolder = (AdViewHolder) holder;
                UnifiedNativeAd nativeAd = preloadAdList.get((pos / 5) - 1);
                populateAdView(nativeAd, adViewHolder);
                if ((pos + 5) / 5 - 1 >= preloadAdList.size()) {
                    interactionListener.getMoreAds();
                }
            }
        } else if (holder instanceof InfoViewHolder) {
            InfoViewHolder info = (InfoViewHolder) holder;
            info.bind(isLoading, isError, isMemesEnded, isHot);
            final MemEntity mem = mems.get(pos);

            if (mem == null && isError) {
                info.tvInfo.setText(R.string.cant_load_feed);
                info.btn.setText(R.string.retry_load_feed);
                info.btn.setOnClickListener(v -> {
                    if (mems.size() > 1) {
                        interactionListener.loadMore(mems.size() - 1);
                        isLoading = true;
                        notifyItemChanged(mems.size());
                    } else {
                        interactionListener.reloadFeedBase();
                        isLoading = true;
                        notifyItemChanged(mems.size());
                    }
                });
            } else if (isMemesEnded && mem == null) {
                if (isHot) {
                    info.tvInfo.setText(R.string.memes_ended);
                    return;
                }
                info.tvInfo.setText(R.string.feed_memes_ended);
                info.btn.setText(R.string.goto_hot);
                info.btn.setOnClickListener(v -> interactionListener.gotoHot());
            } else {
                if (!isLoading && !isMemesEnded) {
                    interactionListener.loadMore(mems.size() - 1);
                    isLoading = true;
                }
            }
        }
    }

    private void populateAdView(UnifiedNativeAd nativeAd, AdViewHolder adViewHolder) {
        populateNativeAdView(nativeAd, adViewHolder.getAdView());
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    private void bind(MemViewHolder memViewHolder, MemEntity mem) {
        if (memViewHolder != null) {
            if (memViewHolder.itemFeedLike != null) {
                switch (mem.getOpinion()) {
                    case LIKED:
                        glide.load(R.drawable.ic_like_pressed).into(memViewHolder.itemFeedLike);
                        glide.load(R.drawable.ic_dislike).into(memViewHolder.itemFeedDislike);
                        break;
                    case NEUTRAL:
                        glide.load(R.drawable.ic_like).into(memViewHolder.itemFeedLike);
                        glide.load(R.drawable.ic_dislike).into(memViewHolder.itemFeedDislike);
                        break;
                    case DISLIKED:
                        glide.load(R.drawable.ic_like).into(memViewHolder.itemFeedLike);
                        glide.load(R.drawable.ic_dislike_pressed).into(memViewHolder.itemFeedDislike);
                        break;
                }
                memViewHolder.tvCommentsCount.setText(String.valueOf(mem.getCommentsCount()));
                memViewHolder.tvDislikeCount.setText(String.valueOf(mem.getDislikes()));
                memViewHolder.tvLikeCount.setText(String.valueOf(mem.getLikes()));
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

    private Pair<Integer, MemEntity> findMemAndPositionById(int id) {
        Pair<Integer, MemEntity> pair = null;
        for (int i = 0; i < mems.size() - 1; i++) {
            if (mems.get(i).getId() == id) {
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

        void getMoreAds();

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

        public void bind(MemEntity mem, GlideRequests glide) {
            switch (mem.getOpinion()) {
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
            tvCommentsCount.setText(String.valueOf(mem.getCommentsCount()));
            tvDislikeCount.setText(String.valueOf(mem.getDislikes()));
            tvLikeCount.setText(String.valueOf(mem.getLikes()));
        }
    }

    static class AdViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ad_view)
        UnifiedNativeAdView adView;
        @BindView(R.id.ad_media)
        MediaView mediaView;
        @BindView(R.id.ad_headline)
        TextView headline;
        @BindView(R.id.ad_body)
        TextView body;
        @BindView(R.id.ad_call_to_action)
        Button callToAction;
        @BindView(R.id.ad_icon)
        ImageView icon;
        @BindView(R.id.ad_stars)
        RatingBar stars;
        @BindView(R.id.ad_advertiser)
        TextView advertiser;


        AdViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
            adView.setMediaView(mediaView);
            // Register the view used for each individual asset.
            adView.setHeadlineView(headline);
            adView.setBodyView(body);
            adView.setCallToActionView(callToAction);
            adView.setIconView(icon);
            adView.setStarRatingView(stars);
            adView.setAdvertiserView(advertiser);
        }

        public UnifiedNativeAdView getAdView() {
            return adView;
        }
    }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btnItemFeedRetry)
        Button btn;
        @BindView(R.id.tvInfoText)
        TextView tvInfo;
        @BindView(R.id.pbLoading)
        ProgressBar pbLoading;

        InfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(boolean isLoading, boolean isError, boolean isMemesEnded, boolean isHot) {
            if (isLoading) {
                btn.setVisibility(View.INVISIBLE);
                tvInfo.setVisibility(View.INVISIBLE);
                pbLoading.setVisibility(View.VISIBLE);
                return;
            }
            if (isError || isMemesEnded) {
                if (isMemesEnded && isHot) {
                    pbLoading.setVisibility(View.INVISIBLE);
                    tvInfo.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.INVISIBLE);
                    return;
                }
                pbLoading.setVisibility(View.INVISIBLE);
                tvInfo.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
            }
        }
    }
}
