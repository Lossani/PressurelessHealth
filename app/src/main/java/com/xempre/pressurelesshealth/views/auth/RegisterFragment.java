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

import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.databinding.FragmentRegisterBinding;
import com.xempre.pressurelesshealth.interfaces.UserService;
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

        return binding.getRoot();
    }

//    public void Register(){
//
//        UserService userService = ApiClient.createService(getContext(), UserService.class);
//        User temp = new User();
//        temp.setPassword(binding.etRegisterName.getText().toString());
//        temp.setUsername(binding.etRegisterLastame.getText().toString());
//        temp.setPassword(binding.etRegisterPassword.getText().toString());
//        temp.setUsername(binding.etRegisterUsername.getText().toString());
//
//        Call<ResponseLogin> call = userService.register(temp);
//        call.enqueue(new Callback<ResponseLogin>() {
//            @Override
//            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
//
//
//                if (response.isSuccessful()) {
//
//                    Log.d("a",response.message());
//                    Log.d("a",response.body().getToken());
//
//                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("token", response.body().getToken());
//                    editor.putInt("userId", response.body().getUser().getIdUser());
//                    editor.apply();
//
//                    Toast.makeText(getContext(), "Logueado Correctamente", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(getContext(), MainActivityView.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getContext(), "Valide los datos ingresados.", Toast.LENGTH_LONG).show();
//                    Log.d("a",response.message());
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseLogin> call, Throwable t) {
//                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_LONG).show();
//                Log.d("a",t.getMessage());
//            }
//        });
//    }

}