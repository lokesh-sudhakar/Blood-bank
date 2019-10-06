package com.example.bloodbank.controller;

import android.Manifest;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.example.bloodbank.R;
import com.example.bloodbank.databinding.ConductorEnterOtpBinding;
import com.example.bloodbank.eventbus.VerificationEvent;
import com.example.bloodbank.utilities.GenericTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * @author Lokesh chennamchetty
 * @date 05/09/2019
 */
public class EnterOtpController extends Controller {

    private static final String TAG = "enter_otp";
    private static final String KEY_VERIFICATION_ID = "verificationId";
    private static final String KEY_FORCE_SENDING_TOKEN = "forceSendingToken";
    private static final String KEY_PHONE_NUMBER = "phoneNumber";
    private ConductorEnterOtpBinding binding;
    private PhoneAuthProvider.ForceResendingToken token;
    private FirebaseAuth mAuth;
    private PhoneAuthCredential credential;
    private String verificationId;
    private String phoneNumber;
    private String otpCode;

    public EnterOtpController(Bundle bundle) {
        super(bundle);
    }

    public static EnterOtpController newInstance(String verificationId,
                                                 PhoneAuthProvider.ForceResendingToken token, String phoneNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_VERIFICATION_ID, verificationId);
        bundle.putParcelable(KEY_FORCE_SENDING_TOKEN, token);
        bundle.putString(KEY_PHONE_NUMBER,phoneNumber);
        return new EnterOtpController(bundle);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.conductor_enter_otp, container, false);
        mAuth = FirebaseAuth.getInstance();
        init();
        return binding.getRoot();
    }

    public void init() {
        extractData();
        binding.phoneNumber.setText(phoneNumber);
        handleSubmitOtp();
        setOtpTextListner();
    }



    private void handleSubmitOtp() {
        binding.submitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificationId != null) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, getEnteredCode());
                    signInWithPhoneAuthCredential(credential);
                }else if(credential!=null){
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    public String getEnteredCode() {
        return binding.otpLayout.otp1.getText().toString() +
                binding.otpLayout.otp2.getText() +
                binding.otpLayout.otp3.getText() +
                binding.otpLayout.otp4.getText() +
                binding.otpLayout.otp5.getText() +
                binding.otpLayout.otp6.getText();
    }

    private void extractData() {
        if (getArgs().containsKey(KEY_FORCE_SENDING_TOKEN) && getArgs().containsKey(KEY_VERIFICATION_ID)) {
            token = getArgs().getParcelable(KEY_FORCE_SENDING_TOKEN);
            verificationId = getArgs().getString(KEY_VERIFICATION_ID);
            phoneNumber = getArgs().getString(KEY_PHONE_NUMBER);
        }
    }

    @Override
    public boolean handleBack() {
        return super.handleBack();
    }

    private void setOtpTextListner() {
        binding.otpLayout.otp1.addTextChangedListener(new GenericTextWatcher(binding.otpLayout.otp2,binding.otpLayout.otp1));
        binding.otpLayout.otp2.addTextChangedListener(new GenericTextWatcher(binding.otpLayout.otp3,binding.otpLayout.otp1));
        binding.otpLayout.otp3.addTextChangedListener(new GenericTextWatcher(binding.otpLayout.otp4,binding.otpLayout.otp2));
        binding.otpLayout.otp4.addTextChangedListener(new GenericTextWatcher(binding.otpLayout.otp5,binding.otpLayout.otp3));
        binding.otpLayout.otp5.addTextChangedListener(new GenericTextWatcher(binding.otpLayout.otp6,binding.otpLayout.otp4));
        binding.otpLayout.otp6.addTextChangedListener(new GenericTextWatcher(binding.otpLayout.otp6,binding.otpLayout.otp5));

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"Log in Success",Toast.LENGTH_SHORT);
                            getRouter().pushController(RouterTransaction.with(RegistrationController.newInstance(phoneNumber))
                                    .pushChangeHandler(new HorizontalChangeHandler())
                                    .popChangeHandler(new HorizontalChangeHandler()));
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                        } else {

                            Toast.makeText(getApplicationContext(),"Log in failure",Toast.LENGTH_SHORT);
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"Enter correct code",Toast.LENGTH_SHORT);

                            }
                        }
                    }
                });
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(VerificationEvent event) {
        Toast.makeText(getActivity(), event.credential.getSmsCode(), Toast.LENGTH_SHORT).show();
        //click
        steOtpTextFromCredentials(event.credential.getSmsCode());
    }


    @AfterPermissionGranted()
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_location_rationale),
                    RC_CAMERA_AND_LOCATION, perms);
        }
    }


    private void steOtpTextFromCredentials(String code) {
        binding.otpLayout.otp1.setText((""+code.charAt(0)));
        binding.otpLayout.otp2.setText((""+code.charAt(1)));
        binding.otpLayout.otp3.setText((""+code.charAt(2)));
        binding.otpLayout.otp4.setText((""+code.charAt(3)));
        binding.otpLayout.otp5.setText((""+code.charAt(4)));
        binding.otpLayout.otp6.setText((""+code.charAt(5)));
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);
        EventBus.getDefault().unregister(this);
    }

}
