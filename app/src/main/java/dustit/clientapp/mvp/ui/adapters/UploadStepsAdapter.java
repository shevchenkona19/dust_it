package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.fragments.CategoriesStepFragment;
import dustit.clientapp.mvp.ui.fragments.UploadFinishedStepFragment;

public class UploadStepsAdapter extends AbstractFragmentStepAdapter {

    private Context context;

    public UploadStepsAdapter(FragmentManager fm, Context context, Uri uploadUri) {
        super(fm, context);
        this.context = context;
        this.uploadUri = uploadUri;
    }

    private Uri uploadUri;

    @Override
    public Step createStep(@IntRange(from = 0) int position) {
        switch (position) {
            case 0:
                return CategoriesStepFragment.newInstance(uploadUri);
            case 1:
            default:
                return UploadFinishedStepFragment.newInstance();
        }
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        int titleId = 0;
        switch (position) {
            case 0:
                titleId = R.string.choose_categories_upload_title;
                break;
            case 1:
                titleId = R.string.upload_finished_title;
        }
        return new StepViewModel.Builder(context)
                .setTitle(titleId)
                .create();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
