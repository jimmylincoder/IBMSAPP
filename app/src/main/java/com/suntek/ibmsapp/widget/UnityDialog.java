package com.suntek.ibmsapp.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suntek.ibmsapp.R;


/**
 * 统一 Dialog
 *
 * @author deVin
 */
public class UnityDialog extends Dialog implements TextWatcher
{
    //定义回调事件，用于dialog的点击事件
    public interface OnConfirmDialogListener
    {
        public void confirm(UnityDialog unityDialog, String content);

    }

    //关闭回调事件
    public interface OnCancelDialogListener
    {
        public void cancel(UnityDialog unityDialog);
    }

    private OnConfirmDialogListener onConfirmDialogListener;
    private OnCancelDialogListener onCancelDialogListener;

    //标题
    private TextView tvTitle;

    //提示
    private TextView tvHint;

    //输入内容
    private EditText etContent;

    //取消按钮
    private TextView tvCancel;

    //关闭按钮
    private LinearLayout llCancel;

    //确定按钮
    private TextView tvConfirm;

    //操作布局
    private LinearLayout llHandle;

    //标题文本
    private String titleStr = null;

    //提示文本
    private String hintStr = null;

    //提示文本
    private SpannableString hintCharsequence = null;

    //是否显示输入框
    private boolean isShowContentEdit = false;

    //输入框提示文本
    private String contentHint = null;

    //输入框文本
    private String contentStr = null;

    //取消按钮文本
    private String cancelStr = null;

    //确定按钮文本
    private String confirmStr = null;

    //输入框显示最小行数,0为不限制
    private int showMinLines = 0;

    //输入框显示最大行数,0为不限制
    private int showMaxLines = 0;

    //输入框可输入最大行数,0为不限制
    private int inputMaxLines = 0;

    //输入框可输入最大长度,0为不限制
    private int inputMaxLength = 0;

    //关闭按钮打开关闭,0=默认，1=打开，2=关闭
    private int isCloseShow=0;

    public UnityDialog(Context context)
    {
        super(context);
    }

    protected void onCreate(Bundle savedinstanceState)
    {
        super.onCreate(savedinstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_uniuty_dialog);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        etContent = (EditText) findViewById(R.id.et_content);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        tvConfirm = (TextView) findViewById(R.id.tv_confirm);
        llHandle = (LinearLayout)findViewById(R.id.ll_handle);
        llCancel = (LinearLayout) findViewById(R.id.ll_cancel);

        if (titleStr != null)
        {
            tvTitle.setText(titleStr);
        }

        if (hintStr != null)
        {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(hintStr);
        }
        else
        {
            tvHint.setVisibility(View.VISIBLE);
            tvHint.setText(hintCharsequence);
            tvHint.setMovementMethod(new LinkMovementMethod());
        }

        if (isShowContentEdit)
        {
            etContent.setVisibility(View.VISIBLE);
            etContent.setHint(contentHint);
            etContent.setText(contentStr);
        }
        else
        {
            tvHint.setMinLines(3);
            tvHint.setVisibility(View.VISIBLE);
        }

        if (cancelStr != null)
        {
            tvCancel.setVisibility(View.VISIBLE);
            tvCancel.setText(cancelStr);
            tvCancel.setOnClickListener(clickListener);
        }

        if (confirmStr != null)
        {
            tvConfirm.setVisibility(View.VISIBLE);
            tvConfirm.setText(confirmStr);
            tvConfirm.setOnClickListener(clickListener);
        }

        if(cancelStr == null && confirmStr == null)
        {
            llHandle.setVisibility(View.GONE);
            if(isCloseShow==0)
            {
                llCancel.setVisibility(View.VISIBLE);
                llCancel.setOnClickListener(clickListener);
            }
        }

        if(isCloseShow==1)
        {
            llCancel.setVisibility(View.VISIBLE);
            llCancel.setOnClickListener(clickListener);
        }
        else
        {
            llCancel.setVisibility(View.GONE);
        }

        if (showMinLines != 0)
        {
            etContent.setMinLines(showMinLines);
        }

        if (showMaxLines != 0)
        {
            etContent.setMaxLines(showMaxLines);
        }

        if (inputMaxLength != 0)
        {
            etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputMaxLength)});
        }

        if (inputMaxLines != 0)
        {
            etContent.addTextChangedListener(this);
        }

    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public UnityDialog setTitle(String title)
    {
        titleStr = title;
        return this;
    }

    /**
     * 设置提示信息
     *
     * @param hint 提示信息
     */
    public UnityDialog setHint(String hint)
    {
        hintStr = hint;
        return this;
    }

    /**
     * 设置提示信息
     *
     * @param hint 提示信息
     */
    public UnityDialog setSpannableHint(SpannableString hint)
    {
        hintCharsequence = hint;
        return this;
    }

    /**
     * 设置内容输入框
     *
     * @param hint    提示框信息
     * @param content 内容
     */
    public UnityDialog setContent(String hint, String content)
    {
        isShowContentEdit = true;
        contentHint = hint;
        this.contentStr = content;
        return this;
    }

    /**
     * 设置内容输入框
     *
     * @param hint           提示框信息
     * @param content        内容
     * @param showMinLines   输入框显示的最小行数
     * @param showMaxLines   输入框显示的最大行数
     * @param inputMaxLines  输入框支持的最大行数
     * @param inputMaxLength 输入框支持的最大长度
     */
    public UnityDialog setContent(String hint, String content,
                                  int showMinLines, int showMaxLines, int inputMaxLines, int inputMaxLength)
    {
        isShowContentEdit = true;
        contentHint = hint;
        this.contentStr = content;
        this.showMinLines = showMinLines;
        this.showMaxLines = showMaxLines;
        this.inputMaxLength = inputMaxLength;
        this.inputMaxLines = inputMaxLines;
        return this;
    }

    /**
     * 设置关闭按钮
     *
     * @param cancelStr              按钮文本
     * @param onCancelDialogListener 回调接口
     */
    public UnityDialog setCancel(String cancelStr, OnCancelDialogListener onCancelDialogListener)
    {

        this.cancelStr = cancelStr;
        this.onCancelDialogListener = onCancelDialogListener;
        return this;
    }

    /**
     * 设置确定按钮
     *
     * @param confirmStr              按钮文本
     * @param onConfirmDialogListener 回调接口
     */
    public UnityDialog setConfirm(String confirmStr, OnConfirmDialogListener onConfirmDialogListener)
    {
        this.confirmStr = confirmStr;
        this.onConfirmDialogListener = onConfirmDialogListener;
        return this;
    }

    /**
     * 设置关闭按钮打开关闭
     *
     */
    public UnityDialog showClose(int isCloseShow)
    {
        this.isCloseShow=isCloseShow;
        return this;
    }

    /**
     * 设置触摸外部不隐藏
     */
    public UnityDialog isCancelable(boolean cancelable)
    {
        super.setCancelable(cancelable);
        return this;
    }

    private View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (v == tvCancel || v == llCancel)
            {
                if (onCancelDialogListener == null)
                {
                    UnityDialog.this.dismiss();
                }
                else
                {
                    onCancelDialogListener.cancel(UnityDialog.this);
                }
            }
            else if (v == tvConfirm)
            {
                if (onConfirmDialogListener != null)
                {
                    onConfirmDialogListener.confirm(UnityDialog.this, etContent.getText().toString());
                }
            }

        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        int lines = etContent.getLineCount();
        // 限制最大输入行数
        if (lines > inputMaxLines)
        {
            String str = s.toString();
            int cursorStart = etContent.getSelectionStart();
            int cursorEnd = etContent.getSelectionEnd();
            if (cursorStart == cursorEnd && cursorStart < str.length() && cursorStart >= 1)
            {
                str = str.substring(0, cursorStart - 1) + str.substring(cursorStart);
            }
            else
            {
                str = str.substring(0, s.length() - 1);
            }
            // setText会触发afterTextChanged的递归
            etContent.setText(str);
            // setSelection用的索引不能使用str.length()否则会越界
            etContent.setSelection(etContent.getText().length());
            return;
        }
        String summary = etContent.getText().toString();
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }

}
