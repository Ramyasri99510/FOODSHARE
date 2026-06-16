package com.foodshare.foodshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.foodshare.foodshare.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends BaseActivity {

    private EditText etName, etEmail, etPhone, etPassword;
    private RadioGroup rgRole;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        rgRole = findViewById(R.id.rgRole);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // Check if user came from Google Sign-in in LoginActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            // Check if this user already exists in Firestore (security check)
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            // Already registered, should have been handled in Login, but let's be safe
                            navigateByRole(doc.getString("role"));
                        } else {
                            // Truly a new user via Google
                            etEmail.setText(currentUser.getEmail());
                            etEmail.setEnabled(false); // Can't change Google email
                            etName.setText(currentUser.getDisplayName());
                            findViewById(R.id.tilPassword).setVisibility(View.GONE);
                            
                            showGoogleContinueDialog(currentUser.getEmail());
                        }
                    });
        }

        btnCreateAccount.setOnClickListener(v -> handleRegistration());
    }

    private void showGoogleContinueDialog(String email) {
        new AlertDialog.Builder(this)
                .setTitle("Complete Profile")
                .setMessage("You are signed in as " + email + ". Please select your role and enter your phone number to complete registration.")
                .setPositiveButton("Continue", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Sign Out", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void handleRegistration() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String phone = etPhone.getText().toString().trim();
        String name = etName.getText().toString().trim();
        
        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in your name and phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            // Google User finishing profile
            saveUserToFirestore(currentUser.getUid(), name, currentUser.getEmail(), phone, getSelectedRole(), true);
        } else {
            // New Email User
            registerNewEmailUser();
        }
    }

    private String getSelectedRole() {
        int selectedRoleId = rgRole.getCheckedRadioButtonId();
        if (selectedRoleId == R.id.rbReceiver) return "Receiver";
        if (selectedRoleId == R.id.rbNgo) return "NGO";
        return "Donor";
    }

    private void registerNewEmailUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields including password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        // Send verification email with explicit listener
                        user.sendEmailVerification()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Verification email sent to " + email, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        saveUserToFirestore(user.getUid(), name, email, phone, getSelectedRole(), false);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void saveUserToFirestore(String uid, String name, String email, String phone, String role, boolean isGoogleUser) {
        User newUser = new User(uid, name, email, phone, role, "", "", System.currentTimeMillis());
        
        db.collection("users").document(uid).set(newUser)
                .addOnSuccessListener(aVoid -> {
                    if (isGoogleUser) {
                        Toast.makeText(this, "Welcome to FoodShare!", Toast.LENGTH_SHORT).show();
                        navigateByRole(role);
                    } else {
                        // For email users, show verification dialog
                        showVerificationPendingDialog();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showVerificationPendingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Verify Your Email")
                .setMessage("A verification link has been sent to your email. Please verify your account, then log in.")
                .setPositiveButton("Go to Login", (dialog, which) -> {
                    mAuth.signOut();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void navigateByRole(String role) {
        Intent intent;
        if ("NGO".equals(role)) {
            intent = new Intent(this, NGODashboardActivity.class);
        } else if ("Receiver".equals(role)) {
            intent = new Intent(this, ReceiverBrowseActivity.class);
        } else {
            intent = new Intent(this, DonorHomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}