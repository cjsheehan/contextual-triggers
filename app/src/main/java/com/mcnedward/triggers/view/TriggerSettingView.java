package com.mcnedward.triggers.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcnedward.triggers.R;
import com.mcnedward.triggers.TriggerService;
import com.mcnedward.triggers.enums.TriggerType;
import com.mcnedward.triggers.thread.FirstThread;
import com.mcnedward.triggers.thread.IThread;
import com.mcnedward.triggers.thread.IThread;
import com.mcnedward.triggers.utils.Extension;

/**
 * Created by Edward on 4/8/2016.
 */
public class TriggerSettingView extends LinearLayout {
    private static final String TAG = "TriggerSettingView";

    private Context context;
    private CheckBox checkbox;
    private TextView txtTitle;
    private TextView txtInfo;

    private TriggerType triggerType;
    private IThread thread;

    public TriggerSettingView(Context context) {
        super(context);
        this.context = context;
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
            int triggerTypeId = a.getInteger(R.styleable.TriggerSettingView_setting_trigger_type, 0);
            triggerType = TriggerType.getById(triggerTypeId);
            txtTitle.setText(title);
        } finally {
            a.recycle();
        }

        setupThread();
    }

    private void initialize() {
        inflate(context, R.layout.view_trigger_setting, this);
        checkbox = (CheckBox) findViewById(R.id.check_trigger);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtInfo = (TextView) findViewById(R.id.txt_info);

        setCheckAction();
        Extension.setRippleBackground(findViewById(R.id.container_text), context);
    }

    private void setCheckAction() {
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
    }

    public void setupThread() {
        if (triggerType == null) {
            Log.e(TAG, "You need to define a trigger type as an enum attribute in the layout!");
            return;
        }
        switch (triggerType) {
            case FIRST:
                thread = new FirstThread(context, txtInfo);
                break;
        }
        TriggerService.addThread(thread);
    }

}
