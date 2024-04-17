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
import com.xempre.pressurelesshealth.databinding.FragmentRegisterBinding;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.ErrorResponse;
import com.xempre.pressurelesshealth.models.ResponseLogin;
import com.xempre.pressurelesshealth.models.User;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends Fragment {

    FragmentRegisterBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());

        binding.btnRegBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment= new LoginFragment();
                ChangeFragment.change(getContext(), R.id.PrincipalContainerView, loginFragment);
            }
        });

        binding.btnRegRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etRegisterPassword.getText().toString().equals("")) {Toast.makeText(getContext(), "La contraseña no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (binding.etRegisterConfirmPassword.getText().toString().equals("")) {Toast.makeText(getContext(), "La contraseña no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (binding.etRegisterEmail.getText().toString().equals("")) {Toast.makeText(getContext(), "El correo no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (binding.etRegisterUsername.getText().toString().equals("")) {Toast.makeText(getContext(), "El nombre de usuario no puede ir en blanco.", Toast.LENGTH_SHORT).show();return;}
                if (binding.etRegisterName.getText().toString().equals("")) {Toast.makeText(getContext(), "El nombre no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (binding.etRegisterLastame.getText().toString().equals("")) {Toast.makeText(getContext(), "El apellido no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (!binding.etRegisterPassword.getText().toString().equals(binding.etRegisterConfirmPassword.getText().toString())){Toast.makeText(getContext(), "La contraseña no coincide.", Toast.LENGTH_SHORT).show(); return;}
                register();

            }
        });

        return binding.getRoot();
    }

    public void register(){

        UserService userService = ApiClient.createService(getContext(), UserService.class, 0);
        User temp = new User();
        temp.setPassword(binding.etRegisterPassword.getText().toString());
        temp.setUsername(binding.etRegisterUsername.getText().toString());
        temp.setFirstName(binding.etRegisterName.getText().toString());
        temp.setLastName(binding.etRegisterLastame.getText().toString());
        temp.setEmail(binding.etRegisterEmail.getText().toString());

        Call<User> call = userService.register(temp);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {


                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), "Registrado Correctamente", Toast.LENGTH_SHORT).show();
                    LoginFragment loginFragment = new LoginFragment();
                    ChangeFragment.change(getContext(), R.id.PrincipalContainerView, loginFragment);

                } else {
                    Gson json = new Gson();
                    String mensaje = json.toJson(response.errorBody());

                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                    Log.d("a", mensaje);
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), String.valueOf(call.request().body()), Toast.LENGTH_LONG).show();
                Log.d("b",t.getMessage());
            }
        });
    }

}