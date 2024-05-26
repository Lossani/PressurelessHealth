package com.xempre.pressurelesshealth.views.auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.Patterns;
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
import com.xempre.pressurelesshealth.views.shared.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

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

        InputFilter letterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        InputFilter alphanumericFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (!Character.isLetterOrDigit(c)) {
                        return "";
                    }
                }
                return null;
            }
        };

        binding.etRegisterName.setFilters(new InputFilter[]{letterFilter});
        binding.etRegisterLastname.setFilters(new InputFilter[]{letterFilter});
        binding.etRegisterUsername.setFilters(new InputFilter[]{alphanumericFilter});
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
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                String email = binding.etRegisterEmail.getText().toString();
                String pass = binding.etRegisterPassword.getText().toString();

                if (binding.etRegisterPassword.getText().toString().trim().isEmpty()) {Toast.makeText(getContext(), "La contraseña no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (email.trim().isEmpty() || !pattern.matcher(email).matches()) {Toast.makeText(getContext(), "El correo no es valido.", Toast.LENGTH_SHORT).show(); return;}
                if (binding.etRegisterUsername.getText().toString().trim().isEmpty()) {Toast.makeText(getContext(), "El nombre de usuario no puede ir en blanco.", Toast.LENGTH_SHORT).show();return;}
                if (binding.etRegisterName.getText().toString().trim().isEmpty()) {Toast.makeText(getContext(), "El nombre no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (binding.etRegisterLastname.getText().toString().trim().isEmpty()) {Toast.makeText(getContext(), "El apellido no puede ir en blanco.", Toast.LENGTH_SHORT).show(); return;}
                if (pass.length()<6) {Toast.makeText(getContext(), "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show(); return;}
                if (!pass.equals(binding.etRegisterConfirmPassword.getText().toString())){Toast.makeText(getContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show(); return;}
                register();

            }
        });

        return binding.getRoot();
    }

    public void register(){

        UserService userService = ApiClient.createService(getContext(), UserService.class, 0);
        User temp = new User();
        temp.setPassword(binding.etRegisterPassword.getText().toString());
        temp.setUsername(binding.etRegisterUsername.getText().toString().trim());
        temp.setFirstName(binding.etRegisterName.getText().toString().trim());
        temp.setLastName(binding.etRegisterLastname.getText().toString().trim());
        temp.setEmail(binding.etRegisterEmail.getText().toString().trim());

        Call<User> call = userService.register(temp);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {


                if (response.isSuccessful()) {

                    Toast.makeText(getContext(), "Registrado Correctamente", Toast.LENGTH_SHORT).show();
                    LoginFragment loginFragment = new LoginFragment();
                    ChangeFragment.change(getContext(), R.id.PrincipalContainerView, loginFragment);

                } else {
                    try {
                        JSONObject errorObject = new JSONObject(response.errorBody().string());

                        String errorMessage = errorObject.getString("response");

                        CustomDialog dialog = new CustomDialog();
                        dialog.create(getActivity(), "Error", errorMessage);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();

                        Toast.makeText(getContext(), "Error processing error message", Toast.LENGTH_SHORT).show();
                    }
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