package dev.stylesync.stylesync.ui.settings;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

import dev.stylesync.stylesync.MainActivity;
import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.databinding.FragmentSettingsBinding;
import dev.stylesync.stylesync.data.UserData;
import dev.stylesync.stylesync.service.UserService;
import dev.stylesync.stylesync.utility.Database;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;
    private ImageButton signInButton;
    private ImageButton signOutButton;
    private Button selectColorsButton;
    private Button selectActivitiesButton;
    private Button manualAddButton;
    private Button savePreferences;
    private EditText inputClothes;
    private UserData userData;
    private Database db;
    private UserService userService;


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

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        signInButton = root.findViewById(R.id.google_sign_in_button);
        signOutButton = root.findViewById(R.id.sign_out_button);

        db = Database.getInstance(); // used to save user preferences
        userService = UserService.getInstance((MainActivity) getActivity());
        userData = userService.getUserData();

        // Add a Button that will open the multi-choice dialog when clicked
        selectColorsButton = root.findViewById(R.id.select_colors_button);

        selectActivitiesButton = root.findViewById(R.id.select_activities_button);

        manualAddButton = root.findViewById(R.id.manual_entry_button);
        inputClothes = root.findViewById(R.id.input_clothes);

        savePreferences = root.findViewById(R.id.save_preferences_button);
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

        settingsViewModel.getUserId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String userId) {
                // Update user data when userId changes
                userService.initUserData();
                userData = userService.getUserData();

                // Update checked boxes in selectColorsButton dialog
                updateColorSelection();
                updateActivitySelection();
            }
        });

        selectColorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] colorOptions = getResources().getStringArray(R.array.color_options);
                boolean[] checkedColors = new boolean[colorOptions.length]; // This will keep track of which colors are selected

                // Initialize checkedColors based on user's current preferences
                List<String> favoriteColors = userData.getUserPreference().getFavoriteColors();
                for (int i = 0; i < colorOptions.length; i++) {
                    if (favoriteColors.contains(colorOptions[i])) {
                        checkedColors[i] = true;
                    }
                }

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
                                List<String> selectedColors = new ArrayList<>();
                                for (int i = 0; i < checkedColors.length; i++) {
                                    if (checkedColors[i]) {
                                        selectedColors.add(colorOptions[i]);
                                    }
                                }
                                userService.getUserData().getUserPreference().setFavoriteColors(selectedColors);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        selectActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] activityOptions = getResources().getStringArray(R.array.activities_options);
                boolean[] checkedActivities = new boolean[activityOptions.length]; // This will keep track of which activities are selected

                // Initialize checkedActivities based on user's current preferences
                List<String> schedules = userData.getUserPreference().getSchedules();
                for (int i = 0; i < activityOptions.length; i++) {
                    if (schedules.contains(activityOptions[i])) {
                        checkedActivities[i] = true;
                    }
                }

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
                                List<String> selectedActivities = new ArrayList<>();
                                for (int i = 0; i < checkedActivities.length; i++) {
                                    if (checkedActivities[i]) {
                                        selectedActivities.add(activityOptions[i]);
                                    }
                                }
                                userService.getUserData().getUserPreference().setSchedules(selectedActivities);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        manualAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clothingItem = inputClothes.getText().toString();
                if(clothingItem.isEmpty()) {
                    return;
                }

                UserData.Cloth cloth = new UserData.Cloth(clothingItem,null, null);
                userService.getUserData().getClothes().add(cloth);

                inputClothes.setText(""); // Clear the EditText
            }
        });

        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Saving");
                System.out.println(userService.getUserData().getUserPreference().getFavoriteColors().toString());
                userService.saveUserData();
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
            userService.initUserData();
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
            userService.initUserData();
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
                        userService.initUserData();
                        if (binding != null) {
                            final TextView welcomeTextView = binding.welcomeTextView;
                            welcomeTextView.setText("Welcome, GUEST!");
                        }
                    }
                });
    }

    private void updateColorSelection() {
        String[] colorOptions = getResources().getStringArray(R.array.color_options);
        boolean[] checkedColors = new boolean[colorOptions.length]; // This will keep track of which colors are selected

        // Initialize checkedColors based on user's current preferences
        List<String> favoriteColors = userData.getUserPreference().getFavoriteColors();
        for (int i = 0; i < colorOptions.length; i++) {
            if (favoriteColors.contains(colorOptions[i])) {
                checkedColors[i] = true;
            }
        }

        selectColorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                List<String> selectedColors = new ArrayList<>();
                                for (int i = 0; i < checkedColors.length; i++) {
                                    if (checkedColors[i]) {
                                        selectedColors.add(colorOptions[i]);
                                    }
                                }
                                userData.getUserPreference().setFavoriteColors(selectedColors);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private void updateActivitySelection() {
        String[] activityOptions = getResources().getStringArray(R.array.activities_options);
        boolean[] checkedActivities = new boolean[activityOptions.length]; // This will keep track of which activities are selected

        // Initialize checkedActivities based on user's current preferences
        List<String> schedules = userData.getUserPreference().getSchedules();
        for (int i = 0; i < activityOptions.length; i++) {
            if (schedules.contains(activityOptions[i])) {
                checkedActivities[i] = true;
            }
        }

        selectActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                List<String> selectedActivities = new ArrayList<>();
                                for (int i = 0; i < checkedActivities.length; i++) {
                                    if (checkedActivities[i]) {
                                        selectedActivities.add(activityOptions[i]);
                                    }
                                }
                                userData.getUserPreference().setSchedules(selectedActivities);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
}