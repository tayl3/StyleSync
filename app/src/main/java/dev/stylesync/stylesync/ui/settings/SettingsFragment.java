package dev.stylesync.stylesync.ui.settings;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import dev.stylesync.stylesync.R;
import dev.stylesync.stylesync.databinding.FragmentSettingsBinding;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;
    private ImageButton signInButton;
    private ImageButton signOutButton;

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
        if(user != null) {
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

        return root;
    }

    private void setWelcomeText(String username) {
        final TextView welcomeTextView = binding.welcomeTextView;
        if(username != null) {
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
            if(user != null && user.getDisplayName() != null) {
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
            if(response == null) {
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
                        if(binding != null) {
                            final TextView welcomeTextView = binding.welcomeTextView;
                            welcomeTextView.setText("Welcome, GUEST!");
                        }
                    }
                });
    }
}