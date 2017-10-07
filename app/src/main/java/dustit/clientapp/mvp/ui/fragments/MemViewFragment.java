package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;

/**
 * Created by shevc on 07.10.2017.
 * Let's GO!
 */

public class MemViewFragment extends Fragment {

    private MemEntity memEntity;
    private static String MEM_KEY = "kiy";

    @BindView(R.id.sdvMemViewIcon)
    SimpleDraweeView sdvIcon;
    @BindView(R.id.ivMemViewMenu)
    ImageView ivMenu;
    @BindView(R.id.ivMemViewBack)
    ImageView ivBack;
    @BindView(R.id.ivMemViewLike)
    ImageView ivLike;
    @BindView(R.id.tvMemViewLikeCount)
    TextView tvLikeCount;
    @BindView(R.id.ivMemViewLikeExpand)
    ImageView ivLikeExpand;
    @BindView(R.id.cvMemViewCard)
    CardView cvMemView;

    private Unbinder unbinder;

    public interface IMemViewFragmentInteractionListener {
        void closeFragment();
        void onLikePressed(String id);
    }

    private IMemViewFragmentInteractionListener interactionListener;

    public static MemViewFragment newInstance(MemEntity memEntity) {
        Bundle args = new Bundle();
        args.putParcelable(MEM_KEY, memEntity);
        MemViewFragment fragment = new MemViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IMemViewFragmentInteractionListener) {
            interactionListener = (IMemViewFragmentInteractionListener) context;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        memEntity = getArguments().getParcelable(MEM_KEY);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mem_view, container, false);
        unbinder = ButterKnife.bind(this, v);
        sdvIcon.setImageURI(Uri.parse(memEntity.getUrl()));
        tvLikeCount.setText(memEntity.getLikes());
        if (memEntity.isLiked()) {
            ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_filled));
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionListener.closeFragment();
            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactionListener.onLikePressed(memEntity.getId());
                ivLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_filled));
            }
        });
        ivLikeExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //expand layout
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }
}
