package com.suntek.ibmsapp.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.suntek.ibmsapp.R;

/**
 * 对讲view
 *
 * @author jimmy
 */
public class TalkView extends LinearLayout
{
    private Context context;

    private LinearLayout llHangUp;

    private ImageView ivTalk;

    private OnHangUpListener onHangUpListener;

    public TalkView(Context context)
    {
        super(context);
        init(context);
    }

    public TalkView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public TalkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setOrientation(HORIZONTAL);
        LayoutInflater inflater = LayoutInflater.from(context);
        View talkView = inflater.inflate(R.layout.view_talk, null);
        addView(talkView, -1, params);

        llHangUp = (LinearLayout) talkView.findViewById(R.id.ll_hang_up);
        ivTalk = (ImageView) talkView.findViewById(R.id.iv_talk);

        final AnimationDrawable animDrawable = (AnimationDrawable) ivTalk
                .getBackground();
        animDrawable.start();

        llHangUp.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onHangUpListener != null)
                    onHangUpListener.onHangUp(v);
            }
        });
    }

    public void setOnHangUpListener(OnHangUpListener onHangUpListener)
    {
        this.onHangUpListener = onHangUpListener;
    }

    public interface OnHangUpListener
    {
        void onHangUp(View view);
    }
}
