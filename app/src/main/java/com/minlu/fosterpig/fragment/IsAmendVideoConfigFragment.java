package com.minlu.fosterpig.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.minlu.fosterpig.R;

/**
 * Created by user on 2017/2/8.
 */

public class IsAmendVideoConfigFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_is_amend_video_config, null);
        builder.setView(view)
                .setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ClickSureListener clickSureListener = (ClickSureListener) getActivity();
                                clickSureListener.onClickSure(dialog, id);
                            }
                        })
                .setNegativeButton("取消", null);
        return builder.create();
    }

    public interface ClickSureListener {
        void onClickSure(DialogInterface dialog, int id);
    }
}
