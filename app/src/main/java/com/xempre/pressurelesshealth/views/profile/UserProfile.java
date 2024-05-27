package com.xempre.pressurelesshealth.views.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xempre.pressurelesshealth.MainActivity;
import com.xempre.pressurelesshealth.R;
import com.xempre.pressurelesshealth.api.ApiClient;
import com.xempre.pressurelesshealth.api.GoogleFitApi;
import com.xempre.pressurelesshealth.databinding.FragmentUserProfileBinding;
import com.xempre.pressurelesshealth.interfaces.ArticleService;
import com.xempre.pressurelesshealth.interfaces.UserService;
import com.xempre.pressurelesshealth.models.Article;
import com.xempre.pressurelesshealth.models.User;
import com.xempre.pressurelesshealth.views.MainActivityView;
import com.xempre.pressurelesshealth.views.leaderboard.LeaderboardList;
import com.xempre.pressurelesshealth.views.profile.challenge.ChallengeList;
import com.xempre.pressurelesshealth.views.profile.goal.GoalList;
import com.xempre.pressurelesshealth.views.shared.ChangeFragment;
import com.xempre.pressurelesshealth.views.shared.CustomDialog;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfile extends Fragment {

    private FragmentUserProfileBinding binding;
    EditText sys;
    EditText dis;

    ImageView imageView;

    private final Handler handler = new Handler(Looper.getMainLooper());
    User user;
    MainActivityView mainViewActivity;
    GoogleFitApi googleFitApi;

    Fragment challengeList = new ChallengeList();
    Fragment goalList = new GoalList();

    Fragment leaderboardList = new LeaderboardList();

    Fragment currentFragment = null;


    private void loadChildFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frameProfile, fragment);
        transaction.commit();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewActivity = (MainActivityView)getActivity();
        googleFitApi = mainViewActivity.getGoogleFitApi();

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        imageView = binding.imageView;
        loadUserData();
        //ChangeFragment.change(this.mainViewActivity, R.id.frameProfile, challengeList, false);


        if (savedInstanceState == null) {
            currentFragment = challengeList;
            loadChildFragment(currentFragment);
            binding.btnChallenge.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.Hip1));
            binding.btnLogros.setBackgroundColor(Color.GRAY);
        }



        binding.btnLogros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnLogros.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.Hip1));
                binding.btnChallenge.setBackgroundColor(Color.GRAY);
                currentFragment = goalList;
                loadChildFragment(currentFragment);
            }
        });

        binding.btnLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeFragment.change(mainViewActivity, R.id.frame_layout, leaderboardList);
                /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, leaderboardList);
                fragmentTransaction.commit();*/
            }
        });

        binding.btnChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ChangeFragment.change(mainViewActivity, R.id.frameProfile, challengeList, false);
                binding.btnChallenge.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.Hip1));
                binding.btnLogros.setBackgroundColor(Color.GRAY);
                currentFragment = challengeList;
                loadChildFragment(currentFragment);
            }
        });

        binding.fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadArticleData();
            }
        });

        return binding.getRoot();

    }
    public void getBitmapFromURL(String src) {

        // Ejecutar operaciones de red en un hilo separado usando Runnable
        final Bitmap[] myBitmap = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Coloca aquí tu código de operaciones de red

                // Ejemplo:
                // HttpURLConnection connection = ...
                // Realizar operaciones de red...
                try{
                Log.e("src",src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap[0] = BitmapFactory.decodeStream(input);}
                catch (Exception e){}
                // Si necesitas actualizar la interfaz de usuario, usa el Handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Actualizar la interfaz de usuario si es necesario
                        imageView.setImageBitmap(myBitmap[0]);
                    }
                });
            }
        }).start();
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
/*
    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = this.mainViewActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameProfile, fragment);
        fragmentTransaction.commit();
    }
*/
    public void loadUserData(){

        UserService userService = ApiClient.createService(getContext(), UserService.class,1);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref",MODE_PRIVATE);

        int userId = sharedPreferences.getInt("userId", 0);

        if (userId != 0){
            Call<User> call = userService.getUserById(userId);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    user = response.body();
                    Log.d("a",response.toString());
                    if(!user.getAvatarURL().equals("")) {
                        Log.d("IMAGEN1", user.getAvatarURL());
                        getBitmapFromURL(user.getAvatarURL());
                    }
                    else {
                        Log.d("IMAGEN", "SEGUNDA IMAGEN");
                        imageView.setImageResource(R.drawable.user);
                    }
                    binding.textView5.setText("Bienvenido " + user.getFirstName()+" " + user.getLastName());
                    binding.tvUserPoints.setText(user.getPoints().toString());

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    //Toast.makeText(getContext(), "ERROR"+t.toString(), Toast.LENGTH_LONG).show();
                    Log.d("a",t.getMessage());
                }
            });
        }


    }

    public void loadArticleData(){
        ArticleService articleService = ApiClient.createService(getContext(), ArticleService.class,1);

            Call<Article> call = articleService.getRandomArticle();

            call.enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    Article data = response.body();
                    CustomDialog dialog = new CustomDialog();
                    dialog.create(getActivity(), data.getTitle(), data.getContent());

                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    //Toast.makeText(getContext(), "ERROR"+t.toString(), Toast.LENGTH_LONG).show();
                    Log.d("a",t.getMessage());
                }
            });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}