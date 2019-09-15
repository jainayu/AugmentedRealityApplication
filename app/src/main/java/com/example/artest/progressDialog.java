package com.example.artest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.common.logging.nano.Vr;

import java.util.zip.Inflater;

public class progressDialog extends AppCompatDialogFragment {


    private ProgressBar mProgressBar;
    Dialog alertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        alertDialog = new Dialog(getContext());
        android.view.View root = LayoutInflater.from(getContext()).inflate(R.layout.progress,(ViewGroup) null);
        alertDialog.setContentView(root);
        alertDialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
        ViewGroup viewGroup;
        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar2);
        mProgressBar.setMax(100);
        alertDialog.setCancelable(false);
        return alertDialog;
    }

    void updateprogress(int n){
        mProgressBar.setProgress(n);
    }


}
