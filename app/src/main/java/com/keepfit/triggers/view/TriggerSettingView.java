package com.keepfit.triggers.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keepfit.triggers.R;
import com.keepfit.triggers.thread.TriggerThread;
import com.keepfit.triggers.utils.Extension;

/**
 * Created by Edward on 4/8/2016.
 */
public class TriggerSettingView extends LinearLayout {
    private static final String TAG = "TriggerSettingView";

    private Context context;
    private CheckBox checkbox;
    private TextView txtTitle;
    private TextView txtInfo;

    private TriggerThread thread;
    private SharedPreferences prefs;
    private String preferenceKey;

    public TriggerSettingView(Context context, TriggerThread thread) {
        super(context);
        this.context = context;
        this.thread = thread;

        initialize();
    }

    public TriggerSettingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initialize();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TriggerSettingView,
                0, 0);

        try {
            String title = a.getString(R.styleable.TriggerSettingView_setting_title);
            txtTitle.setText(title);
        } finally {
            a.recycle();
        }
    }

    private void initialize() {
        inflate(context, R.layout.view_trigger_setting, this);
        checkbox = (CheckBox) findViewById(R.id.check_trigger);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtInfo = (TextView) findViewById(R.id.txt_info);


        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        preferenceKey = thread.getName() + "_checked";
        boolean checked = prefs.getBoolean(preferenceKey, false);
        checkbox.setChecked(checked);
        thread.setEnabled(checked);

        txtTitle.setText(thread.getName());

        Extension.setRippleBackground(findViewById(R.id.container_text), context);
        setCheckAction();
        setupThread();
    }

    private void setCheckAction() {
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                doCheckAction(isChecked);
            }
        });
        findViewById(R.id.container_text).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setChecked(!checkbox.isChecked());
                doCheckAction(checkbox.isChecked());
            }
        });
    }

    private void doCheckAction(boolean isChecked) {
        thread.pauseThread(isChecked);
        thread.setEnabled(isChecked);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(preferenceKey, isChecked);
        editor.commit();
    }

    private void setupThread() {
        thread.setDisplay(txtInfo);
    }
}
