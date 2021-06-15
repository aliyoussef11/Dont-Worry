package com.example.dontworry;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeactivateParentAccount extends Fragment {
    private static final String ALLOWED_CHARACTERS ="AZNKGTOPQHDS";
    EditText Confirmation, UsernameParent;
    TextView RandomText;
    Button Deactivate;
    String UsernameFromLogin, UsernameFromRegistration, UsernameFromAddLocationActivity;
    String Final_Username;
    private StorageReference storageProfilePicsRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deactivate_layout, container, false);

        //Generate a random Text
        final Random random = new Random();
        final StringBuilder sb=new StringBuilder(6);
        for (int i=0;i<6;++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }

        //Referencing
        storageProfilePicsRef = FirebaseStorage.getInstance().getReference().child("Profile Pic Parent");
        Confirmation = view.findViewById(R.id.ConfirmDeactivation);
        UsernameParent = view.findViewById(R.id.ConfirmUsernameDeactivation);
        RandomText = view.findViewById(R.id.randomText);
        Deactivate = view.findViewById(R.id.deactivate);

        //Fetch The Username Using Intents
        UsernameFromLogin = getActivity().getIntent().getStringExtra("UsernameParentFromLogin");
        UsernameFromRegistration = getActivity().getIntent().getStringExtra("UsernameParentFromRegistration");
        UsernameFromAddLocationActivity = getActivity().getIntent().getStringExtra("UsernameFromAddLocationActivity");

        //Take The Correct Username
        if (TextUtils.isEmpty(UsernameFromRegistration) && TextUtils.isEmpty(UsernameFromAddLocationActivity)) {
            Final_Username = UsernameFromLogin;
        } else if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromAddLocationActivity)) {
            Final_Username = UsernameFromRegistration;
        } else if (TextUtils.isEmpty(UsernameFromLogin) && TextUtils.isEmpty(UsernameFromRegistration)) {
            Final_Username = UsernameFromAddLocationActivity;
        }

        RandomText.setText(sb);

        Deactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Take Values From Edit Text
                String confirmation = Confirmation.getText().toString().trim();
                String username = UsernameParent.getText().toString().trim();

                if(confirmation.isEmpty()){
                    Confirmation.setError("Please Fill This Text");
                    Confirmation.requestFocus();
                }
                else if(username.isEmpty()){
                    UsernameParent.setError("Please Fill Your Username");
                    UsernameParent.requestFocus();
                }
                else if(!(confirmation.equals(sb.toString()))){
                    Toast.makeText(getActivity(), "Confirmation Error, Try Again", Toast.LENGTH_LONG).show();
                }
                else if(!(username.equals(Final_Username))){
                    Toast.makeText(getActivity(), "Username Error, Try Again", Toast.LENGTH_LONG).show();
                }
                else{
                    //Toast.makeText(getActivity(), "Matching", Toast.LENGTH_LONG).show();
                    final DatabaseReference referenceParents = FirebaseDatabase.getInstance().getReference("Parents");
                    final DatabaseReference referenceLocation = FirebaseDatabase.getInstance().getReference("Location");
                    final DatabaseReference ParentUser = FirebaseDatabase.getInstance().getReference("ParentUser");

                    referenceParents.child(Final_Username).removeValue();
                    referenceLocation.child(Final_Username).removeValue();
                    storageProfilePicsRef.child(Final_Username+".jpg").delete();
                    ParentUser.child(Final_Username).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_LONG).show();
                                final SweetAlertDialog sweet = new SweetAlertDialog(getActivity(),  SweetAlertDialog.SUCCESS_TYPE);
                                sweet.setTitleText("Your Account Has Been Deactivated");
                                sweet.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sweet.dismiss();
                                        Intent i = new Intent(getActivity(), Welcome.class);
                                        startActivity(i);
                                    }
                                }, 2000);
                            }
                            else{
                                Toast.makeText(getActivity(), "Error When Deleting", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }

            }
        });

        return view;
    }
}
