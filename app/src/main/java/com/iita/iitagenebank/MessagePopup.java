package com.iita.iitagenebank;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Simeon on 10/09/2015.
 */
public class MessagePopup extends Dialog implements View.OnClickListener
{
    public Activity c;
    public String popupTitle;
    public String popupMsg;
    public TextView popupcontent;
    public TextView popuptitle;
    public Button ok;

    public MessagePopup(Activity a, String title, String message)
    {
        super(a);
        this.c = a;
        this.popupTitle = title;
        this.popupMsg = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_popup);
        popuptitle = (TextView) findViewById(R.id.popup_title);
        popuptitle.setText(popupTitle);
        popupcontent = (TextView) findViewById(R.id.popup_content);
        popupcontent.setText(popupMsg);
        ok = (Button) findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_ok:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
