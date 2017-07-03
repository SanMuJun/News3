package com.san.numberaddsubview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by San on 2016/12/26.
 */
public class NumberAddSubView extends LinearLayout implements View.OnClickListener {

     public Button btn_sub;
     public Button btn_add;
     public TextView tv_values;

    public int values=1;
    public int minValues=1;
    public int maxValues=10;

    public int getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(int maxValues) {
        this.maxValues = maxValues;
    }

    public int getMinValues() {
        return minValues;
    }

    public void setMinValues(int minValues) {
        this.minValues = minValues;
    }

    public int getValues() {
        String valueStr = tv_values.getText().toString().trim();
        if (!TextUtils.isEmpty(valueStr)){
            values =  Integer.valueOf(valueStr);
        }
        return values;
    }

    public void setValues(int values) {
        this.values = values;
        tv_values.setText(values+"");

    }







    public NumberAddSubView(Context context) {
        this(context,null);
    }

    public NumberAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public NumberAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

     View.inflate(context,R.layout.num_add_sub_view,this);
        btn_sub= (Button) findViewById(R.id.btn_sub);
        btn_add= (Button) findViewById(R.id.btn_add);
        tv_values= (TextView) findViewById(R.id.tv_values);

        getValues();
        btn_add.setOnClickListener(this);
        btn_sub.setOnClickListener(this);

        if (attrs!=null){
            TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.NumberAddSubView);


            int value = tintTypedArray.getInt(R.styleable.NumberAddSubView_values, 0);
            if (value>0){
                setValues(value);
            }
            int minValue = tintTypedArray.getInt(R.styleable.NumberAddSubView_minValues, 0);
            if (minValue!=0){
                setMinValues(minValue);
            }

            int maxValue = tintTypedArray.getInt(R.styleable.NumberAddSubView_maxValues, 0);
            if (maxValue>0){
                setMaxValues(maxValue);
            }



            Drawable drawableAddSub = tintTypedArray.getDrawable(R.styleable.NumberAddSubView_numAddSubBackground);
            if (drawableAddSub!=null){
                setBackground(drawableAddSub);
            }
            Drawable drawableAdd = tintTypedArray.getDrawable(R.styleable.NumberAddSubView_numAddBackground);
            if (drawableAdd!=null){
                btn_add.setBackground(drawableAdd);
            }
            Drawable drawableSub = tintTypedArray.getDrawable(R.styleable.NumberAddSubView_numSubBackground);
            if (drawableSub!=null){
                btn_sub.setBackground(drawableSub);
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                numberAdd();
                if (listener!=null){
                    listener.onNumberAdd(view,values);
                }
                break;
            case R.id.btn_sub:
                numberSub();
                if (listener!=null){
                    listener.onNumberSub(view,values);
                }
                break;

        }
    }

    private void numberAdd() {
        if (values<maxValues){
            values+=1;
        }
        setValues(values);
    }

    private void numberSub() {
        if (values>minValues){
            values-=1;
        }
        setValues(values);
    }






    public interface OnNumberClickListener{

        public void onNumberAdd(View view,int values);
        public void onNumberSub(View view,int values);
    }

    public OnNumberClickListener listener;

    public void seOnNumberClickListener(OnNumberClickListener listener) {
        this.listener = listener;
    }
}
