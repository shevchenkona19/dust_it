package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.presenters.fragments.CategoriesStepFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.CategoriesRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesStepFragment;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;

public class CategoriesStepFragment extends Fragment implements BlockingStep, ICategoriesStepFragment {

    @BindView(R.id.ivUploadPhoto)
    ImageView ivUpload;
    @BindView(R.id.rvCategoriesUpload)
    RecyclerView rvCategories;

    private Unbinder unbinder;
    private CategoriesStepFragmentPresenter presenter = new CategoriesStepFragmentPresenter();
    private CategoriesRecyclerViewAdapter adapter;
    private ICategoriesStepFragmentInteraction interaction;
    private boolean sent = false;

    public interface ICategoriesStepFragmentInteraction {
        void sendCategories(String checkedCategories);
    }

    private Uri uploadUri;

    public static CategoriesStepFragment newInstance(Uri uploadUri) {
        Bundle args = new Bundle();
        args.putString(IConstants.IBundle.UPLOAD, uploadUri.toString());
        CategoriesStepFragment fragment = new CategoriesStepFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ICategoriesStepFragmentInteraction) {
            interaction = (ICategoriesStepFragmentInteraction) context;
        } else throw new
                RuntimeException(context.getClass().getSimpleName() + " must implement ICategoriesStepFragmentInteraction");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uploadUri = Uri.parse(getArguments().getString(IConstants.IBundle.UPLOAD));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload_photo_categories, container, false);
        unbinder = ButterKnife.bind(this, v);
        presenter.bind(this);
        adapter = new CategoriesRecyclerViewAdapter(inflater);
        adapter.useDarkColor();
        rvCategories.setAdapter(adapter);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 3));
        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        interaction = null;
        super.onDetach();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (!sent) {
            sent = true;
            callback.getStepperLayout().showProgress(getString(R.string.loading));
            interaction.sendCategories(adapter.getChecked());
        } else {
            callback.getStepperLayout().hideProgress();
            callback.goToNextStep();
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        String checked = adapter.getChecked();
        if (checked.equals("")) {
            return new VerificationError("");
        }
        return null;
    }

    @Override
    public void onSelected() {
        presenter.loadCategories();
        ivUpload.setImageURI(uploadUri);
    }

    @Override
    public void onError(@NonNull VerificationError error) {
        if (getContext() != null) {
            AlertDialog dialog = AlertBuilder.getEmptyCategoriesError(getContext());
            dialog.show();
        }
    }

    @Override
    public void onNotRegistered() {
        if (getContext() != null)
            AlertBuilder.showNotRegisteredPrompt(getContext());
    }

    @Override
    public void onCategoriesArrived(List<Category> categories) {
        adapter.updateItems(categories);
    }

    @Override
    public void onCategoriesFailedToLoad() {

    }
}
