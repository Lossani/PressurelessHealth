package com.xempre.pressurelesshealth.views.auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.FragmentRestartPasswordBinding;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.ResponseLogin;
import com.xempre.pressurelesshealth.models.RestartPassword;
import com.xempre.pressurelesshealth.models.User;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;
import com.xempre.pressurelesshealth.views.shared.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestartPasswordFragment extends Fragment {

    FragmentRestartPasswordBinding binding;

    String email;
    public RestartPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestartPasswordBinding.inflate(getLayoutInflater());
        //binding.llSend.setVisibility(View.INVISIBLE);
        binding.llNewPassword.setVisibility(View.INVISIBLE);
        // Inflate the layout for this fragment


        binding.btnSendCode.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!binding.etSendCode.getText().toString().equals("")) sendCode();
                 else Toast.makeText(getContext(), "Debe ingresar un correo.", Toast.LENGTH_LONG).show();
             }
         }
        );

        binding.btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if ( !binding.etValidNumber.getText().toString().equals("") &&   !binding.etNewPassword.getText().toString().equals("") && !binding.etConfirmPassword.getText().toString().equals("") &&
                         binding.etNewPassword.getText().toString().equals(binding.etConfirmPassword.getText().toString())){
                     sendNewPassword();
                 } else {
                     Toast.makeText(getContext(), "Valide los datos ingresados.", Toast.LENGTH_LONG).show();
                 }

             }
         }
        );

        binding.btnCancelRestartPassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 returnToLogin();
             }
         }
        );

        return binding.getRoot();

    }

    public void sendCode(){

        binding.btnSendCode.setActivated(false);

        UserService userService = ApiClient.createService(getContext(), UserService.class,0);
        RestartPassword temp = new RestartPassword();
        temp.setEmail(binding.etSendCode.getText().toString());
        email = binding.etSendCode.getText().toString();

        Call<Void> call = userService.resetPassword(temp);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {


                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Se envió el correo.", Toast.LENGTH_LONG).show();
                    binding.llSend.setVisibility(View.INVISIBLE);
                    binding.llNewPassword.setVisibility(View.VISIBLE);
                }
                else {

                    try {
                        JSONObject errorObject = new JSONObject(response.errorBody().string());

                        String errorMessage = errorObject.getString("response");

                        CustomDialog dialog = new CustomDialog();
                        dialog.create(getActivity(), "Error", errorMessage);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();

                        Toast.makeText(getContext(), "Error processing error message", Toast.LENGTH_SHORT).show();
                    }

                    binding.btnSendCode.setActivated(true);
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
                Log.d("a",t.getMessage());
            }
        });

        ApiClient.destroy();
    }

    public void sendNewPassword(){
        Log.d("a", "entre a funcion");
//        email = "maxwellapv@gmail.com";
        UserService userService = ApiClient.createService(getContext(), UserService.class,0);
        RestartPassword temp = new RestartPassword();
        temp.setEmail(email);
        temp.setCode(binding.etValidNumber.getText().toString());
        temp.setPassword(binding.etNewPassword.getText().toString());

        Call<Void> call = userService.resetPasswordValidation(temp);

        Log.d("a", "entre a funcion 2");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {


                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), "Se actualizo la contraseña.", Toast.LENGTH_LONG).show();
                    returnToLogin();
                }
                else {
                    Toast.makeText(getContext(), "Valide los datos ingresados.", Toast.LENGTH_LONG).show();
                    Gson gson = new Gson();
                    String json = gson.toJson(temp);
                    Log.d("a",json);
                    Log.d("a",response.message());
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
                Log.d("a",t.getMessage());
            }
        });

        ApiClient.destroy();
    }
    public void returnToLogin(){
        LoginFragment loginFragment = new LoginFragment();
        ChangeFragment.change(getContext(), R.id.PrincipalContainerView, loginFragment);
    }
}

