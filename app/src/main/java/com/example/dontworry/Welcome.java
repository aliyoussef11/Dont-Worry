package com.example.dontworry;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity{
    Animation scaleUp, scaleDown;
    TextView textView1, textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        textView1 = findViewById(R.id.textv1);
        textView2 = findViewById(R.id.textv2);

        scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.scale_down);

        textView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    textView1.startAnimation(scaleUp);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    textView1.startAnimation(scaleDown);
                }
                return true;
            }
        });

        textView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    textView2.startAnimation(scaleUp);
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    textView2.startAnimation(scaleDown);
                }
                return true;
            }
        });
    }


    public void Login(View view) {
        Intent i = new Intent(Welcome.this, LoginActivity.class);
        startActivity(i);
    }


    public void SignUp(View view) {
        showAlertDialog();
    }

    private void showAlertDialog() {
        Dialog dialog = new Dialog(Welcome.this);
        dialog.setContentView(R.layout.custom_dialog2);

        final Button btChild = dialog.findViewById(R.id.bt_child);
        final Button btParent = dialog.findViewById(R.id.bt_parent);

        btChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Welcome.this, ChildRegistration.class);
                startActivity(i);
            }
        });

        btParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Welcome.this, ParentRegistration.class);
                startActivity(i);
            }
        });
        dialog.show();
    }
}
