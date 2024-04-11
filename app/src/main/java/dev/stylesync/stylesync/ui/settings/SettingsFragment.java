package dev.stylesync.stylesync.ui.settings;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.databinding.FragmentSettingsBinding;
import dev.stylesync.stylesync.data.UserData;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;
    private ImageButton signInButton;
    private ImageButton signOutButton;
    private Button selectColorsButton;
    private Button selectActivitiesButton;
    private UserData userData;

    // Define the colors and their names
    final int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA}; // Add more colors as needed
    final String[] colorNames = new String[]{"Red", "Green", "Blue", "Yellow", "Magenta"}; // Add more color names as needed

    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        signInButton = root.findViewById(R.id.google_sign_in_button);
        signOutButton = root.findViewById(R.id.sign_out_button);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            setWelcomeText(user.getDisplayName());
        } else {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            setWelcomeText(null);
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create and launch sign-in intent
                Intent signInIntent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build();
                signInLauncher.launch(signInIntent);
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                signInButton.setVisibility(View.VISIBLE);
                signOutButton.setVisibility(View.GONE);
            }
        });

        userData = new UserData();

        // Add a Button that will open the multi-choice dialog when clicked
        selectColorsButton = root.findViewById(R.id.select_colors_button);

        selectActivitiesButton = root.findViewById(R.id.select_activities_button);

        selectColorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the multi-choice dialog inside the Button's onClick method
                String[] colorOptions = getResources().getStringArray(R.array.color_options);
                boolean[] checkedColors = new boolean[colorOptions.length]; // This will keep track of which colors are selected

                new AlertDialog.Builder(requireContext())
                    .setTitle("Select Colors")
                    .setMultiChoiceItems(colorOptions, checkedColors, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedColors[which] = isChecked;
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // When the user closes the dialog, update the favoriteColors in the userData object in your SettingsViewModel with the selected colors
                            List<String> selectedColors = new ArrayList<>();
                            for (int i = 0; i < checkedColors.length; i++) {
                                if (checkedColors[i]) {
                                    selectedColors.add(colorOptions[i]);
                                }
                            }
                            // Update the favorite colors in the user preference
                            userData.getUserPreference().setFavoriteColors(selectedColors);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });

        selectActivitiesButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Create the multi-choice dialog inside the Button's onClick method
            String[] activityOptions = getResources().getStringArray(R.array.activities_options);
            boolean[] checkedActivities = new boolean[activityOptions.length]; // This will keep track of which activities are selected

            new AlertDialog.Builder(requireContext())
                .setTitle("Select Activities")
                .setMultiChoiceItems(activityOptions, checkedActivities, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedActivities[which] = isChecked;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // When the user closes the dialog, update the schedules in the userData object with the selected activities
                        List<String> selectedActivities = new ArrayList<>();
                        for (int i = 0; i < checkedActivities.length; i++) {
                            if (checkedActivities[i]) {
                                selectedActivities.add(activityOptions[i]);
                            }
                        }
                        // Update the schedules in the user data
                        userData.getUserPreference().setSchedules(selectedActivities);
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
        }
    });

        return root;
    }

    private void setWelcomeText(String username) {
        final TextView welcomeTextView = binding.welcomeTextView;
        if (username != null) {
            settingsViewModel.setUsername(username);
            welcomeTextView.setText("Welcome, " + username + "!");
        } else {
            welcomeTextView.setText("Welcome, GUEST!");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            settingsViewModel.setAuthenticated(true);
            if (user != null && user.getDisplayName() != null) {
                settingsViewModel.setUsername(user.getDisplayName());
                settingsViewModel.setUserId(user.getUid());
            } else {
                settingsViewModel.setUsername("GUEST");
                settingsViewModel.setUserId("NULL");
            }

            final TextView welcomeTextView = binding.welcomeTextView;
            welcomeTextView.setText("Welcome, " + settingsViewModel.getUsername() + "!");

            //Navigate to home screen
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_settings_to_home);
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            if (response == null) {
                Log.d("SettingsFragment", "User cancelled login");
            } else {
                int errorCode = response.getError().getErrorCode();
                Log.d("SettingsFragment", "Error code: " + errorCode);
            }

            settingsViewModel.setUsername("GUEST");
            settingsViewModel.setUserId("NULL");
            settingsViewModel.setAuthenticated(false);
        }
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        // ...
                        settingsViewModel.setUsername("GUEST");
                        settingsViewModel.setUserId("NULL");
                        settingsViewModel.setAuthenticated(false);
                        if (binding != null) {
                            final TextView welcomeTextView = binding.welcomeTextView;
                            welcomeTextView.setText("Welcome, GUEST!");
                        }
                    }
                });
    }
}