package com.example.handsgivingt;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class NeedyHomepage extends AppCompatActivity {

    Button socialButton;
    Button profileButton;
    Button homepageButton;

    Fragment needyHomepageFragment;
    Fragment needySocialFragment;
    Fragment needyProfileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needy_homepage);
        socialButton = findViewById(R.id.needyhomepage_social_button);
        profileButton = findViewById(R.id.needyhomepage_profile_button);
        homepageButton = findViewById(R.id.needyhomepage_homepage_button);

        needyHomepageFragment = NeedyHomepageFragment.newInstance("","");




        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragments_frame, needyHomepageFragment, needyHomepageFragment.getTag())
                .commit();

        /*
        feedbackButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                Intent intent = new Intent( NeedyHomepage.this, FeedbackUser.class);
                                                startActivity( intent);
                                            }
                                        }
        );

         */

        socialButton.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View view)
                                            {
                                                /*
                                                Intent intent = new Intent( NeedyHomepage.this, NeedySocialPage.class);
                                                startActivity( intent);
                                                */
                                                needySocialFragment = NeedySocialPageFragment.newInstance("","");
                                                getSupportFragmentManager().beginTransaction()
                                                        .setReorderingAllowed(true)
                                                        .replace(R.id.fragments_frame, needySocialFragment, needySocialFragment.getTag())
                                                        .addToBackStack(null)
                                                        .commit();
                                            }
                                        }
        );

        homepageButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 getSupportFragmentManager().beginTransaction()
                                                         .setReorderingAllowed(true)
                                                         .replace(R.id.fragments_frame, needyHomepageFragment, needyHomepageFragment.getTag())
                                                         .addToBackStack(null)
                                                         .commit();
                                             }
                                         }
        );

        profileButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             @Override
                                             public void onClick(View view)
                                             {
                                                 needyProfileFragment = NeedyProfileFragment.newInstance("","");
                                                 getSupportFragmentManager().beginTransaction()
                                                         .setReorderingAllowed(true)
                                                         .replace(R.id.fragments_frame, needyProfileFragment, needyProfileFragment.getTag())
                                                         .addToBackStack(null)
                                                         .commit();

                                             }
                                         }
        );


    }
}