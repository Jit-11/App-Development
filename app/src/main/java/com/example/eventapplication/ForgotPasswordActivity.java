package com.example.eventapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailPhoneInput;
    private Button sendOtpButton;
    private TextView countdownText;
    private String generatedOtp;
    private static final int REQUEST_SMS_PERMISSION = 123;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailPhoneInput = findViewById(R.id.emailPhoneInput);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        countdownText = findViewById(R.id.countdownText);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_SMS_PERMISSION);
        }

        sendOtpButton.setOnClickListener(v -> {
            String input = emailPhoneInput.getText().toString().trim();
            if (isEmail(input)) {
                sendOtpByEmail(input);
            } else if (isPhoneNumber(input)) {
                sendOtpBySms(input);
            } else {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid phone number or email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to determine if the input is an email
    private boolean isEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    // Function to determine if the input is a valid phone number
    private boolean isPhoneNumber(String input) {
        return android.util.Patterns.PHONE.matcher(input).matches() && input.length() == 10;
    }

    // Function to generate a random 6-digit OTP
    private String generateRandomOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Function to send OTP via Email
    private void sendOtpByEmail(String email) {
        generatedOtp = generateRandomOtp();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your OTP");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, your OTP for password recovery is " + generatedOtp);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
        startOtpCountdown();

        // Navigate to OTP verification after sending email OTP
        navigateToOtpVerification();
    }

    // Function to send OTP via SMS
    private void sendOtpBySms(String phoneNumber) {
        generatedOtp = generateRandomOtp();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Your OTP is: " + generatedOtp, null, null);
            Toast.makeText(this, "OTP sent via SMS", Toast.LENGTH_SHORT).show();
            startOtpCountdown();

            // Navigate to OTP verification after sending SMS OTP
            navigateToOtpVerification();
        } else {
            Toast.makeText(this, "SMS permission is required", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to start a countdown timer for OTP expiry (e.g., 60 seconds)
    private void startOtpCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                countdownText.setText("Resend OTP in: " + time);
            }

            @Override
            public void onFinish() {
                countdownText.setText("You can resend OTP now.");
                sendOtpButton.setEnabled(true);  // Re-enable OTP send button
            }
        }.start();

        sendOtpButton.setEnabled(false);  // Disable OTP send button during countdown
    }

    // Navigation to OTP verification screen
    private void navigateToOtpVerification() {
        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyActivity.class);
        intent.putExtra("otp", generatedOtp); // Pass the generated OTP
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}