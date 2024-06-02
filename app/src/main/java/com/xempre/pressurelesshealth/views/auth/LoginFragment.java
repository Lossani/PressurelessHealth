package com.xempre.pressurelesshealth.views.auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.FragmentLoginBinding;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.ResponseLogin;
import com.xempre.pressurelesshealth.models.User;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.editTextText4.getText().toString().equals("") && !binding.editTextTextPassword.getText().toString().equals("")){
                    Login();
                }
            }
        });

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               RegisterFragment registerFragment = new RegisterFragment();
               ChangeFragment.change(getContext(), R.id.PrincipalContainerView, registerFragment);
           }
       }
        );

        binding.tvRecoverPassword.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       RestartPasswordFragment restartPasswordFragment = new RestartPasswordFragment();
                                                       ChangeFragment.change(getContext(), R.id.PrincipalContainerView, restartPasswordFragment);
                                                   }
                                               }
        );

        return binding.getRoot();
    }

    public void Login(){
        binding.loginProgressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);
        binding.btnRegister.setEnabled(false);
        binding.tvRecoverPassword.setTextColor(Color.parseColor("#AEAEAE"));
        binding.tvRecoverPassword.setEnabled(false);
        binding.editTextText4.setEnabled(false);
        binding.editTextTextPassword.setEnabled(false);

        UserService userService = ApiClient.createService(getContext(), UserService.class,0);
        User temp = new User();
        temp.setPassword(binding.editTextTextPassword.getText().toString());
        temp.setUsername(binding.editTextText4.getText().toString());

        Call<ResponseLogin> call = userService.login(temp);
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.isSuccessful()) {

                    Log.d("a",response.message());
                    Log.d("a",response.body().getToken());

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().getToken());
                    editor.putInt("userId", response.body().getUser().getIdUser());
                    editor.apply();



                    Log.d("TESTPERRO",  sharedPreferences.getString("token", null) );


                    // Verificar si el token se ha guardado correctamente
                    String savedToken = sharedPreferences.getString("token", null);
                    if (savedToken != null) {
                        // Si el token se ha guardado correctamente, iniciar el MainActivityView
                        Toast.makeText(getContext(), "Logueado Correctamente", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), MainActivityView.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        // Si el token no se ha guardado correctamente, mostrar un mensaje de error o tomar alguna otra acción
                        Toast.makeText(getContext(), "Error al guardar el token", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getContext(), "Datos incorrectos, verifique usuario y/o contraseña.", Toast.LENGTH_LONG).show();
                    Gson gson = new Gson();
                    String json = gson.toJson(temp);
//                    Toast.makeText(getContext(), temp.getUsername(), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getContext(), temp.getPassword(), Toast.LENGTH_LONG).show();
                    Log.d("a",json);
                    Log.d("a",response.message());

                    binding.loginProgressBar.setVisibility(View.INVISIBLE);
                    binding.btnLogin.setEnabled(true);
                    binding.btnRegister.setEnabled(true);
                    binding.tvRecoverPassword.setEnabled(true);
                    binding.editTextText4.setEnabled(true);
                    binding.editTextTextPassword.setEnabled(true);
                    binding.tvRecoverPassword.setTextColor(Color.parseColor("#6750A3"));
                }

            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
                Log.d("a",t.getMessage());
            }
        });

        ApiClient.destroy();
    }

}