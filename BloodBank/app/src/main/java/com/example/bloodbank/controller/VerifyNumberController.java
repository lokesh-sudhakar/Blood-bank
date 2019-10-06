package com.example.bloodbank.controller;

import android.app.ProgressDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.example.bloodbank.R;
import com.example.bloodbank.databinding.ConductorVerifyNumberBinding;
import com.example.bloodbank.eventbus.VerificationEvent;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

/**
 * @author Lokesh chennamchetty
 * @date 05/09/2019
 */
public class VerifyNumberController extends Controller {

    private final static String COUNTRY_CODE = "+91";
    private ConductorVerifyNumberBinding binding;
    private ProgressDialog loadingBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthCredential phoneCredential;
    private String phoneNumber;
    private boolean isControllerChanged = false;


    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.conductor_verify_number, container, false);
        init();
        return binding.getRoot();
    }

    public void init() {
        loadingBar = new ProgressDialog(getActivity());
        binding.sendOtpBtn.setEnabled(false);
        addNumberChangeListner();
        verifyNumberHandling();
        addAuthCallback();
        controllerChangeListener();
    }

    private void controllerChangeListener() {
        getRouter().addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                isControllerChanged = true;
            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }
        });
    }

    private void addAuthCallback() {
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull final PhoneAuthCredential phoneAuthCredential) {
                loadingBar.dismiss();
                phoneCredential = phoneAuthCredential;
                if(isControllerChanged) {
                    new Thread(
                            new Runnable() {

                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                            EventBus.getDefault().post(new VerificationEvent(phoneCredential));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                }else {
                    isControllerChanged=false;
                    getRouter().pushController(RouterTransaction.with(RegistrationController.newInstance(phoneNumber))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
//                mVerificationId = verificationId;
                loadingBar.dismiss();
                mResendToken = token;
                getRouter().pushController(RouterTransaction.with(EnterOtpController.newInstance(verificationId, token, phoneNumber)));
                Toast.makeText(getApplicationContext(), "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();

            }
        };
    }

    private void verifyNumberHandling() {
        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = COUNTRY_CODE + binding.mobileNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(getApplicationContext(), "Please enter your phone number first...", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            getActivity(),
                            callbacks);
                }
            }
        });
    }

    private void addNumberChangeListner() {
        binding.mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 10) {
                    binding.sendOtpBtn.setEnabled(true);
                }
            }
        });
    }
}
