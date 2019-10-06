package com.example.bloodbank.controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.example.bloodbank.R;
import com.example.bloodbank.databinding.ActivityRegistrationBinding;
import com.example.bloodbank.models.User;
import com.example.bloodbank.utilities.ControllerTags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.rxbinding3.view.RxView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author Lokesh chennamchetty
 * @date 08/09/2019
 */
public class RegistrationController extends Controller {

    public static final String KEY_PHONE_NUMBER = "phone_number";
    private ActivityRegistrationBinding binding;
    private FirebaseAuth mAuth;
    private CompositeDisposable disposable;
    private DatabaseReference mDatabase;
    private User user;
    private Calendar myCalendar;

    public RegistrationController(Bundle bundle) {
        super(bundle);
    }

    public static RegistrationController newInstance(String phoneNumber){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PHONE_NUMBER,phoneNumber);
        return  new RegistrationController(bundle);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_registration, container, false);
        disposable = new CompositeDisposable();
        user = new User();
        init();
        return binding.getRoot();
    }

    public void init(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        binding.phoneNumber.setText(getArgs().getString(KEY_PHONE_NUMBER));
        FirebaseDatabase.getInstance().getReference();
        binding.customToolbar.title.setText(getResources().getString(R.string.registration));
        //Todo need to remove
//        handleSignOut();
        handleSubmitBtn();
        handleGenderRadioBtn();
        handleDatePicker();
        handleLocationClick();
    }

    private void handleLocationClick() {
        disposable.add(RxView.clicks(binding.location).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
//                EasyPermissions.onRequestPermissionsResult(getActivity(),);
            }
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,getActivity());
    }

    private void handleDatePicker() {
        myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener dateSetListner = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int mount, int date) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, mount);
                myCalendar.set(Calendar.DAY_OF_MONTH, date);
                updateDate();
            }
        };

        disposable.add(RxView.clicks(binding.birthDate)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        new DatePickerDialog(getActivity(), dateSetListner, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                }));
    }

    public void updateDate(){
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.birthDate.setText(sdf.format(myCalendar.getTime()));
    }



    private void handleGenderRadioBtn() {
        binding.radioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId){
                    case R.id.radioMale:
                        user.setMale(true);
                        binding.profilePic.setImageDrawable(getResources().getDrawable(R.drawable.man));
                        break;
                    case R.id.radioFemale:
                        user.setMale(false);
                        binding.profilePic.setImageDrawable(getResources().getDrawable(R.drawable.girl));
                        break;
                }
            }
        });
    }

    private void handleSubmitBtn() {

        disposable.add(RxView.clicks(binding.register).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit){
                User user = new User();
                    checkData(user);

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    //Go to login
                    Toast.makeText(getActivity(),"You are not logged in",Toast.LENGTH_SHORT).show();
                    if (getRouter().hasRootController())
                    {
                        getRouter().popToTag(ControllerTags.VERIFY_NUMBER_CONTROLLER,new HorizontalChangeHandler());
                    }
                }
                else{
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mDatabase.child("users").child(uid).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(),"registration complete",Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }));
    }

    private void checkData(User user) {
        if(isEmpty(binding.name)){
            showToast("Name filed cannot be empty");
        }else{
            user.setUserName(binding.name.getText().toString());
        }
        if(isEmpty(binding.email)){
            showToast("Email filed cannot be empty");
        }else{
            user.setEmail(binding.email.getText().toString());
        }
        if(isEmpty(binding.password)){
            showToast("Password can't be empty");
        }else if(!binding.password.getText().toString().equals(binding.reEnterPassword.getText())){
            showToast("password does not match");
        }else{
            user.setPassword(binding.password.getText().toString());
        }
    }

    private void showToast(String s) {
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT);
    }

    public  boolean isEmpty(EditText view){
        return view.getText().length()==0;
    }

//    private void handleSignOut() {
//        disposable.add(
//        RxView.clicks(binding.signOutBtn)
//                .subscribe(new Consumer<Unit>() {
//                    @Override
//                    public void accept(Unit unit) {
//                        mAuth.signOut();
//                        getRouter().setRoot(RouterTransaction.with(new VerifyNumberController())
//                                .pushChangeHandler(new HorizontalChangeHandler())
//                                .popChangeHandler(new HorizontalChangeHandler()));
//                    }
//                }));
//    }


}
