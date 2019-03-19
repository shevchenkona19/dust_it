package dustit.clientapp.mvp.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.Achievement;
import dustit.clientapp.mvp.model.entities.AchievementsEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.UploadEntity;
import dustit.clientapp.mvp.presenters.activities.NewAccountActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.AccountViewPagerAdapter;
import dustit.clientapp.mvp.ui.adapters.AchievementAdapter;
import dustit.clientapp.mvp.ui.fragments.MemViewFragment;
import dustit.clientapp.mvp.ui.fragments.UserFavouritesList;
import dustit.clientapp.mvp.ui.fragments.UserPhotoList;
import dustit.clientapp.mvp.ui.interfaces.INewAccountActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.bus.FavouritesBus;

public class AccountActivity extends AppCompatActivity implements INewAccountActivityView, AppBarLayout.OnOffsetChangedListener, UserPhotoList.OnFragmentInteractionListener, UserFavouritesList.OnFragmentInteractionListener, MemViewFragment.IMemViewRatingInteractionListener {

    private static final int PICK_IMAGE = 222;
    private static final int CROPPED_IMAGE = 223;
    private static final int PERMISSION_DIALOG = 1010;
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 5;
    public static boolean isReload = false;
    private final NewAccountActivityPresenter presenter = new NewAccountActivityPresenter();
    @BindView(R.id.tbAccountSettingsToolbar)
    Toolbar toolbar;
    @BindView(R.id.tvAccountUsername)
    TextView tvUsername;
    @BindView(R.id.achievementsList)
    RecyclerView rvAchievements;
    @BindView(R.id.tlAccountTabs)
    TabLayout tabs;
    @BindView(R.id.account_tabs)
    ViewPager vpAccount;
    @BindView(R.id.fabAddPhoto)
    FloatingActionButton fabAddPhoto;
    @BindView(R.id.sdvAccountUserIcon)
    SimpleDraweeView sdvIcon;
    @BindView(R.id.appbarAccount)
    AppBarLayout apbAccount;
    @BindView(R.id.clLayout)
    CoordinatorLayout clLayout;
    @BindView(R.id.btnRegister)
    Button btnRegister;

    @Inject
    UserSettingsDataManager userSettingsDataManager;
    private String username = "";
    private String userId = "";
    private boolean isMe = false;
    private AchievementAdapter achievementAdapter;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    private AccountViewPagerAdapter adapter;

    private int[] tabIcons = {
            R.drawable.ic_view_list,
            R.drawable.ic_view_grid,
            R.drawable.ic_saved,
            R.drawable.ic_people
    };

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(IConstants.IBundle.IS_ME, isMe);
        outState.putString(IConstants.IBundle.USER_ID, userId);
        outState.putBoolean(IConstants.IBundle.RELOAD, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        requestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        final Transition slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        slide.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                if (!(isMe && !userSettingsDataManager.isRegistered())) {
                    loadAchievements();
                    adapter = new AccountViewPagerAdapter(getSupportFragmentManager(), userId, isMe);
                    vpAccount.setAdapter(adapter);
                    if (tabs != null) {
                        tabs.setupWithViewPager(vpAccount);
                        tabs.getTabAt(0).setIcon(tabIcons[0]);
                        tabs.getTabAt(1).setIcon(tabIcons[1]);
                        tabs.getTabAt(2).setIcon(tabIcons[2]);
                        if (isMe)
                            tabs.getTabAt(3).setIcon(tabIcons[3]);
                        else {
                            if (tabs.getTabAt(3) != null)
                                tabs.removeTabAt(3);
                        }
                    }
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        final Window window = getWindow();
        window.setEnterTransition(slide);
        window.setReturnTransition(slide);
        window.setExitTransition(slide);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        sdvIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        App.get().getAppComponent().inject(this);
        presenter.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) bundle = savedInstanceState;
        if (bundle != null) {
            if (!bundle.getBoolean(IConstants.IBundle.IS_ME)) {
                userId = bundle.getString(IConstants.IBundle.USER_ID);
            } else {
                userId = presenter.loadMyId();
                isMe = true;
            }
        }
        apbAccount.addOnOffsetChangedListener(this);
        mMaxScrollSize = apbAccount.getTotalScrollRange();
        loadUsername();
        checkReload();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                int flags = 0;
                window.getDecorView().setSystemUiVisibility(flags);
            } else {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
                window.setStatusBarColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (savedInstanceState != null) {
            if (!savedInstanceState.getBoolean(IConstants.IBundle.IS_ME)) {
                userId = savedInstanceState.getString(IConstants.IBundle.USER_ID);
            } else {
                userId = presenter.loadMyId();
                isMe = true;
            }
            if (savedInstanceState.getBoolean(IConstants.IBundle.RELOAD)) {
                loadAchievements();
            }
        }
    }

    @Override
    protected void onDestroy() {
        sdvIcon.setLayerType(View.LAYER_TYPE_NONE, null);
        FavouritesBus.destroy();
        presenter.unbind();
        super.onDestroy();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                int flags = 0;
                window.getDecorView().setSystemUiVisibility(flags);
            } else {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
                window.setStatusBarColor(Color.WHITE);
            }
        }
        rvAchievements.setHasFixedSize(true);
        sdvIcon.setLegacyVisibilityHandlingEnabled(true);
        vpAccount.setOffscreenPageLimit(3);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpAccount.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vpAccount.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                    case 1:
                        showAddPhoto();
                        break;
                    default:
                        hideAddPhoto();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (isMe) {
            toolbar.inflateMenu(R.menu.profile_menu);
            toolbar.setOnMenuItemClickListener(item -> {
                Intent i;
                switch (item.getItemId()) {
                    case R.id.toSettings:
                        i = new Intent(AccountActivity.this, SettingsActivity.class);
                        i.putExtras(getIntent().getExtras() != null ? getIntent().getExtras() : new Bundle());
                        break;
                    case R.id.toEditAccount:
                        i = new Intent(AccountActivity.this, PersonalSettingsActivity.class);
                        break;
                    case R.id.toWriteUs:
                        i = new Intent(AccountActivity.this, UserFeedbackActivity.class);
                        break;
                    default:
                        return true;
                }
                startActivity(i);
                return true;
            });
            if (userSettingsDataManager.isRegistered()) {
                sdvIcon.setOnClickListener(view -> {
                    final AlertDialog dialog = new AlertDialog.Builder(AccountActivity.this)
                            .setTitle(getString(R.string.change_profile_pic_title))
                            .setMessage(getString(R.string.change_profile_pic_question))
                            .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                                final int permCheckRead = ContextCompat.checkSelfPermission(AccountActivity.this,
                                        Manifest.permission.READ_EXTERNAL_STORAGE);
                                final int permCheckWrite = ContextCompat.checkSelfPermission(AccountActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (permCheckRead != PackageManager.PERMISSION_GRANTED
                                        && permCheckWrite != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AccountActivity.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            PERMISSION_DIALOG);
                                } else {
                                    final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    getIntent.setType("image/*");
                                    final Intent pickIntent = new Intent(Intent.ACTION_PICK);
                                    pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                    final Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_photo));
                                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                                    startActivityForResult(chooserIntent, PICK_IMAGE);
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .create();
                    dialog.setOnShowListener(dialogInterface -> {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    });
                    dialog.show();
                });
            } else {
                tvUsername.setText("");
                vpAccount.setVisibility(View.GONE);
                tabs.setVisibility(View.GONE);
                btnRegister.setVisibility(View.VISIBLE);
                btnRegister.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, ChooserActivity.class)));
                sdvIcon.setOnClickListener(view -> onNotRegistered());
                rvAchievements.setVisibility(View.GONE);
                Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/drawable/noimage");
                sdvIcon.setImageURI(uri);
                btnRegister.setVisibility(View.VISIBLE);
                btnRegister.setOnClickListener((view -> startActivity(new Intent(this, ChooserActivity.class))));
            }
        } else {
            fabAddPhoto.setVisibility(View.GONE);
        }
        toolbar.setNavigationOnClickListener(v -> unRevealActivity());
        achievementAdapter = new AchievementAdapter(this, isMe);
        rvAchievements.setAdapter(achievementAdapter);
        rvAchievements.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fabAddPhoto.setOnClickListener(v -> {
            if (presenter.isRegistered()) {
                startActivityForResult(new Intent(AccountActivity.this, PhotoUploadActivity.class), IConstants.IRequest.UPLOAD_PHOTO);
                return;
            }
            onNotRegistered();
        });
        startPostponedEnterTransition();
    }

    private void hideAddPhoto() {
        fabAddPhoto.setVisibility(View.GONE);
    }

    private void showAddPhoto() {
        fabAddPhoto.setVisibility(View.VISIBLE);
    }

    protected void unRevealActivity() {
        finishAfterTransition();
    }

    private void checkReload() {
        if (!isReload) return;
        loadAchievements();
        isReload = false;
    }

    private void loadAchievements() {
        if ((isMe && !userSettingsDataManager.isRegistered())) return;
        presenter.getAchievements(userId);
    }

    private void loadUsername() {
        if ((isMe && !userSettingsDataManager.isRegistered())) return;
        presenter.getUsername(userId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_DIALOG: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        final Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");
                        final Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");
                        final Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.choose_photo));
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                        startActivityForResult(chooserIntent, PICK_IMAGE);
                    }
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "JPEG_" + timeStamp + "_";
        final File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                return;
            }
            final Uri imageSource = data.getData();
            try {
                final File image = createImageFile();
                final Uri destinationUri = Uri.fromFile(image);
                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDefault));
                options.setActiveWidgetColor(getResources().getColor(R.color.colorPrimaryDefault));
                options.setToolbarTitle(getString(R.string.crop_photo));
                if (imageSource != null) {
                    UCrop.of(imageSource, destinationUri)
                            .withOptions(options)
                            .start(this, CROPPED_IMAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CROPPED_IMAGE) {
            if (resultCode == UCrop.RESULT_ERROR || data == null) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                try {
                    throw UCrop.getError(data);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return;
            }
            Uri croppedImage;
            try {
                croppedImage = UCrop.getOutput(data);
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                return;
            }
            if (croppedImage != null) {
                presenter.uploadImage(croppedImage.getPath());
                sdvIcon.setVisibility(View.GONE);
            }
        } else if (requestCode == IConstants.IRequest.UPLOAD_PHOTO) {
            if (resultCode == IConstants.IRequest.RESULT_OK) {
                adapter.refreshUploads();
            }
        }
    }

    @Override
    public void onUploadFinished() {
        sdvIcon.setVisibility(View.VISIBLE);
        Fresco.getImagePipeline().clearCaches();
        sdvIcon.setImageURI(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + username);
    }

    @Override
    public void onUploadFailed() {
        showSnackbar(getString(R.string.error));
        sdvIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUsernameArrived(String username) {
        this.username = username;
        tvUsername.setText(username);
        sdvIcon.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/userPhoto?targetUsername=" + username));
    }

    @Override
    public void onUsernameFailedToLoad() {
        showSnackbar(getString(R.string.error));
    }

    @Override
    public void onAchievementsLoaded(AchievementsEntity achievementsEntity) {
        List<Achievement> items = new ArrayList<>();
        items.add(achievementsEntity.getLikes());
        items.add(achievementsEntity.getDislikes());
        items.add(achievementsEntity.getComments());
        items.add(achievementsEntity.getViews());
        items.add(achievementsEntity.getFavourites());
        items.add(achievementsEntity.getReferral());
        achievementAdapter.update(achievementsEntity.isFirstHundred(), achievementsEntity.isFirstThousand(), items);
        rvAchievements.scheduleLayoutAnimation();
    }

    @Override
    public void onFailedToLoadAchievements() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            sdvIcon.animate()
                    .scaleY(0).scaleX(0).alpha(0)
                    .setDuration(200)
                    .start();
            sdvIcon.setClickable(false);
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
            sdvIcon.setClickable(true);
            sdvIcon.animate()
                    .scaleY(1).scaleX(1).alpha(1)
                    .start();
        }
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.accountFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onUploadSelected(View animStart, UploadEntity upload) {
        MemViewFragment fragment = MemViewFragment.newInstance(upload.toMemEntity(), false, userId);
        showFragment(fragment);
    }

    @Override
    public void onCommentsSelected(View animStart, UploadEntity upload) {
        MemViewFragment fragment = MemViewFragment.newInstance(upload.toMemEntity(), true, userId);
        showFragment(fragment);
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(clLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteSelected(MemEntity memEntity) {
        MemViewFragment fragment = MemViewFragment.newInstance(memEntity, userId, true);
        showFragment(fragment);
    }

    @Override
    public void closeMemView() {
        getSupportFragmentManager().popBackStack();
    }
}
