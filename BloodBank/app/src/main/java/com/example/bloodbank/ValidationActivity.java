package com.example.bloodbank;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.example.bloodbank.controller.RegistrationController;
import com.example.bloodbank.controller.VerifyNumberController;
import com.example.bloodbank.databinding.ActivityValidationBinding;
import com.example.bloodbank.utilities.ControllerTags;

/**
 * @author Lokesh chennamchetty
 * @date 05/09/2019
 */
public class ValidationActivity extends AppCompatActivity {

    private Router mRouter;
    private ActivityValidationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_validation);
        mRouter = Conductor.attachRouter(this,binding.container,savedInstanceState);
        if (!mRouter.hasRootController()) {
//            mRouter.setRoot(RouterTransaction.with(new VerifyNumberController())
            mRouter.setRoot(RouterTransaction.with(RegistrationController.newInstance("7021868057"))
                    .tag(ControllerTags.VERIFY_NUMBER_CONTROLLER));
        }
    }
}
