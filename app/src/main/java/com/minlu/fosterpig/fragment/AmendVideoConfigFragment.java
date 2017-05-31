package com.minlu.fosterpig.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ViewsUitls;

/**
 * Created by user on 2017/2/8.
 */

public class AmendVideoConfigFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        String ip = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_IP_KEY, "");
        String userName = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_USER_NAME_KEY, "");
        String passWord = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_PASS_WORD_KEY, "");


        View view = inflater.inflate(R.layout.dialog_amend_config, null);
        final EditText ipEdit = (EditText) view.findViewById(R.id.et_dialog_amend_config_ip);
        final EditText userNameEdit = (EditText) view.findViewById(R.id.et_dialog_amend_config_user_name);
        final EditText passWordEdit = (EditText) view.findViewById(R.id.et_dialog_amend_config_pass_word);
        ipEdit.setText(ip);
        userNameEdit.setText(userName);
        passWordEdit.setText(passWord);

        builder.setView(view)
                .setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_IP_KEY, ipEdit.getText().toString());
                                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_USER_NAME_KEY, userNameEdit.getText().toString());
                                SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(), StringsFiled.VIDEO_LOGIN_PASS_WORD_KEY, passWordEdit.getText().toString());
                            }
                        })
                .setNegativeButton("取消", null);
        return builder.create();
    }
}
