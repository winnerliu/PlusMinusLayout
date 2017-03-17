package app.winner.com.plusminuslayout;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by WinnerLiu on 2016/8/5.
 */
public class PlusMinusLayout extends ViewGroup implements View.OnClickListener {

    private int margin = DensityUtil.dip2px(getContext(), 0.5f);// per line margin
    private int mMaxChildWidth = 0;
    private int mMaxChildHeight = 0;
    private int count = 0;
    private ImageView mPlusView;
    private ImageView mMinusView;
    private TextView mTextView;

    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 999;
    private int mQty;
    private OnTypeClickListener mListener;

    public PlusMinusLayout(Context context) {
        super(context);
        init();
    }

    public PlusMinusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlusMinusLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setBackgroundColor(Color.parseColor("#9C9C9C"));
        addView(initMinusView());
        addView(initEditView());
        addView(initPlusView());
    }


    private View initPlusView() {
        mPlusView = new ImageView(getContext());
        mPlusView.setTag("plusTag");
        mPlusView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mPlusView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mPlusView.setImageResource(R.drawable.plus_icon);
        int pading = DensityUtil.dip2px(getContext(), 5);
        mPlusView.setPadding(pading, pading, pading, pading);
        mPlusView.setBackgroundColor(Color.parseColor("#ffffff"));
        mPlusView.setOnClickListener(this);

        return mPlusView;
    }

    private View initMinusView() {
        mMinusView = new ImageView(getContext());
        mMinusView.setTag("minusTag");
        mMinusView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mMinusView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mMinusView.setImageResource(R.drawable.minus_unpressed_icon);
        int pading = DensityUtil.dip2px(getContext(), 5);
        mMinusView.setPadding(pading, pading, pading, pading);
        mMinusView.setBackgroundColor(Color.parseColor("#ffffff"));
        mMinusView.setOnClickListener(this);

        return mMinusView;
    }

    private View initEditView() {
        mTextView = new TextView(getContext());
        mTextView.setTag("editTag");
        mTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTextView.setTextColor(getContext().getResources().getColor(R.color.global_text_color));
        mTextView.setTextSize(18);
        mTextView.setText("1");
        mTextView.setOnClickListener(this);

        mTextView.setGravity(Gravity.CENTER);
        mTextView.setBackgroundColor(Color.parseColor("#ffffff"));

        return mTextView;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        mMaxChildWidth = 0;
        mMaxChildHeight = 0;

        int modeW = MeasureSpec.AT_MOST, modeH = MeasureSpec.AT_MOST;
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED)
            modeW = MeasureSpec.UNSPECIFIED;
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED)
            modeH = MeasureSpec.UNSPECIFIED;

        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), modeW);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), modeH);

        count = getChildCount();
        if (count == 0) {
            super.onMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            return;
        }
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            mMaxChildWidth = Math.max(mMaxChildWidth, child.getMeasuredWidth());
            mMaxChildHeight = Math.max(mMaxChildHeight, child.getMeasuredHeight());
        }
        setMeasuredDimension(resolveSize(mMaxChildWidth, widthMeasureSpec), resolveSize(mMaxChildHeight, heightMeasureSpec));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = margin;
        int parentH = b - t;
        int parentW = r - l;
        if (count == 0 || count != 3) {
            new Exception("only has three childs");
            return;
        }

        if (parentW / parentH <= 2) {
            new Exception("The width must be longer than 2 times the height");
            return;
        }

        int minusHeight = parentH - 2 * margin;
        int minusWidth = minusHeight;

        int plusHeight = minusHeight;
        int plusWidth = minusWidth;

        int editHeight = plusHeight;
        int editWidth = parentW - minusWidth - plusWidth - 4 * margin;

        //init minusView
        View minusView = this.getChildAt(0);
        if (minusView == null) return;
        if (minusWidth != minusView.getMeasuredWidth() || minusHeight != minusView.getMeasuredHeight()) {
            minusView.measure(makeMeasureSpec(minusWidth, EXACTLY), makeMeasureSpec(minusHeight, EXACTLY));
        }
        minusView.layout(left, margin, left + minusWidth, margin + minusHeight);
        left = left + minusWidth + margin;

        //init editView
        View editView = this.getChildAt(1);
        if (editView == null) return;
        if (editWidth != editView.getMeasuredWidth() || editHeight != editView.getMeasuredHeight()) {
            editView.measure(makeMeasureSpec(editWidth, EXACTLY), makeMeasureSpec(editHeight, EXACTLY));
        }
        editView.layout(left, margin, left + editWidth, margin + editHeight);
        left = left + editWidth + margin;

        // init plusView
        View plusView = this.getChildAt(2);
        if (plusView == null) return;
        if (plusWidth != plusView.getMeasuredWidth() || plusHeight != plusView.getMeasuredHeight()) {
            plusView.measure(makeMeasureSpec(plusWidth, EXACTLY), makeMeasureSpec(plusHeight, EXACTLY));
        }
        plusView.layout(left, margin, left + plusWidth, margin + plusHeight);
    }


    @Override
    public void onClick(View v) {
        if (v.getTag().equals("minusTag")) {
            onMinus();
            if (mListener != null) {
                mListener.onMinusAction(mQty);
            }
        } else if (v.getTag().equals("plusTag")) {
            onPlus();
            if (mListener != null) {
                mListener.onPlusAction(mQty);
            }
        } else if (v.getTag().equals("editTag")) {
            if (mListener != null) {
                mListener.onEditClickAction(mQty);
            }
        }
    }

    public void setNumber(int qty) {
        mQty = qty;
        mTextView.setText(String.valueOf(qty));
        refreshButtonState();
    }

    private void onMinus() {
        if (mQty > MIN_QUANTITY) {
            mQty--;
            setNumber(mQty);
        }
    }

    private void onPlus() {
        if (mQty < MAX_QUANTITY) {
            mQty++;
            setNumber(mQty);
        }
    }

    private void refreshButtonState(){
        if (mQty >= MAX_QUANTITY) {
            mPlusView.setImageResource(R.drawable.plus_unpressed_icon);
            mPlusView.setEnabled(false);
        }else{
            mPlusView.setImageResource(R.drawable.plus_icon);
            mPlusView.setEnabled(true);
        }

        if (mQty == MIN_QUANTITY) {
            mMinusView.setImageResource(R.drawable.minus_unpressed_icon);
            mMinusView.setEnabled(false);
        }else{
            mMinusView.setImageResource(R.drawable.minus_icon);
            mMinusView.setEnabled(true);
        }
    }

    public interface OnTypeClickListener {
        void onPlusAction(int qty);

        void onMinusAction(int qty);

        void onEditClickAction(int qty);
    }

    public void setOnTypeClickListener(OnTypeClickListener listener) {
        this.mListener = listener;
    }

    public ImageView getmPlusView() {
        return mPlusView;
    }


    public ImageView getmMinusView() {
        return mMinusView;
    }


    public TextView getmTextView() {
        return mTextView;
    }
}
