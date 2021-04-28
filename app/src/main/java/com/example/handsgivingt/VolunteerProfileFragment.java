package com.example.handsgivingt;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VolunteerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VolunteerProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button pdegis;
    CircleImageView profilim;

    ListView listView;
    TextView userNameSurname, userMail;
    private FirebaseFunctions mFunctions;

    private String currentUserId;

    public Uri imguri;
    private StorageReference mStorageRef;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VolunteerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NeedyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VolunteerProfileFragment newInstance(String param1, String param2) {
        VolunteerProfileFragment fragment = new VolunteerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final List<JSONObject> requestList = new ArrayList<JSONObject>();

        View view = inflater.inflate(R.layout.fragment_volunteer_profile, container, false);
        listView = view.findViewById(R.id.previousHelpsListview);
        userNameSurname = view.findViewById(R.id.userNameSurnameTW);
        pdegis = view.findViewById(R.id.pdegis);
        userMail = view.findViewById(R.id.userMailTW);
        mFunctions = FirebaseFunctions.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String emailo = "";
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                emailo = profile.getEmail();
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("email", emailo);
        final String finalEmailo = emailo;
        mFunctions.getHttpsCallable("getVolunteerFinishedRequests")
                .call(data)
                .addOnSuccessListener( new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject allRequests = new JSONObject(json);


                            Iterator<String> keys = allRequests.keys();
                            List<String> keys2 = new ArrayList<String>();

                            while(keys.hasNext()) {
                                String key = keys.next();
                                if (allRequests.get(key) instanceof JSONObject) {
                                    keys2.add(key);
                                }
                            }
                            for(int a = 0; a < keys2.size(); a++)
                            {
                                requestList.add(allRequests.getJSONObject(keys2.get(a)));

                            }

                            VolunteerPreviousRequestsAdapter adapter = new VolunteerPreviousRequestsAdapter(getActivity(), R.layout.previous_request_custom_listview_layout, requestList);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Log.i("@@@@:", "Position: " + position);
                                }
                            });
                        } catch (Exception e){
                            Log.wtf("Error",e.toString());
                        }
                    }
                });
        Map<String, Object> data2 = new HashMap<>();
        data2.put("email", emailo);
        mFunctions.getHttpsCallable("getCurrentUserInfo")
                .call(data2)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        try{
                            Gson g = new Gson();
                            String json = g.toJson(httpsCallableResult.getData());
                            JSONObject jsonObject = new JSONObject(json);
                            addElements(jsonObject, finalEmailo);
                        } catch (Exception e){
                            Log.d("Error",e.toString());
                        }
                    }
                });



        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        pdegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filechooser();
            }
        });

        profilim = view.findViewById(R.id.profileImage);

        StorageReference islandRef = mStorageRef.child(currentUserId + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                profilim.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                StorageReference islandRef = mStorageRef.child(currentUserId + ".png");
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        profilim.setImageBitmap(bmp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        StorageReference islandRef = mStorageRef.child(currentUserId + ".jpeg");
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                profilim.setImageBitmap(bmp);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });
                    }
                });
            }
        });

        return view;
    }

    private void addElements(JSONObject dat, String email) throws JSONException {
        userNameSurname.setText(dat.getJSONObject(email).getString("Name") + " " + dat.getJSONObject(email).getString("Surname"));
        userMail.setText(dat.getJSONObject(email).getString("Email"));
    }

    private void filechooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((cr.getType(uri)));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imguri = data.getData();
            StorageReference Ref = mStorageRef.child(currentUserId + "." + getExtension(imguri));

            Ref.putFile(imguri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Fotoğraf yüklenmiştir.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Fotoğraf yüklenemedi.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}