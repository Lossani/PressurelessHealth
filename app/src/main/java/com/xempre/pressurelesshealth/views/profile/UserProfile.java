package com.xempre.pressurelesshealth.views.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.databinding.FragmentUserProfileBinding;
import com.xempre.pressurelesshealth.interfaces.MeasurementService;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.Measurement;
import com.xempre.pressurelesshealth.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserProfile extends Fragment {

    private FragmentUserProfileBinding binding;
    EditText sys;
    EditText dis;

    User user;
    MainActivity mainViewActivity;
    GoogleFitApi googleFitApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewActivity = (MainActivity)getActivity();
        googleFitApi = mainViewActivity.getGoogleFitApi();

//        sys = getView().findViewById(R.id.etSystolic);
//        dis = getView().findViewById(R.id.etDiastolic);
//        message = getView().findViewById(R.id.textView2);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        //binding.imageView.setImageBitmap(getBitmapFromURL("https://static.vecteezy.com/system/resources/previews/008/442/086/non_2x/illustration-of-human-icon-user-symbol-icon-modern-design-on-blank-background-free-vector.jpg"));
        loadUserData();
        return binding.getRoot();

    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    int sr = Integer.parseInt(sys.getText().toString());
//                    int dr = Integer.parseInt(dis.getText().toString());
//                    saveButton(sr,dr);
//                } catch (Exception ignored){
//                    Toast.makeText(getContext(), "Asegurece de ingresar números validos.", Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });
    }

    public void loadUserData(){

        // below line is to create an instance for our retrofit api class.
            UserService userService = ApiClient.createService(UserService.class);
//        Toast.makeText(getContext(), "Data added to API", Toast.LENGTH_SHORT).show();
        // passing data from our text fields to our modal class.
//        Date date = new Date();

        // calling a method to create a post and passing our modal class.
        Call<User> call = userService.getUserById(2);

        // on below line we are executing our method.
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // this method is called when we get response from our api.
//                // below line is for hiding our progress bar.
//                loadingPB.setVisibility(View.GONE);
//
//                // on below line we are setting empty text
//                // to our both edit text.
//                jobEdt.setText("");
//                nameEdt.setText("");

                // we are getting response from our body
                // and passing it to our modal class.
                user = response.body();
                Log.d("a",response.toString());
                binding.textView5.setText("Bienvenido " + user.getUsername());


//                // on below line we are getting our data from modal class and adding it to our string.
                String responseString = "Response Code : " + response.code() + "\nName : "  + "\n" + "Job : ";

//                // below line we are setting our
//                // string to our text view.
//                message.setText(responseString);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //Toast.makeText(getContext(), "ERROR"+t.toString(), Toast.LENGTH_LONG).show();
                Log.d("a",t.getMessage());
                // setting text to our text view when
                // we get error response from API.
//                responseTV.setText("Error found is : " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}