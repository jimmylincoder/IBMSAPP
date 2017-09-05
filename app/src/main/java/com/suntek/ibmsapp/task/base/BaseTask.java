package com.suntek.ibmsapp.task.base;

import android.content.Context;
import android.os.AsyncTask;

import com.suntek.ibmsapp.component.http.FHttpException;
import com.suntek.ibmsapp.widget.LoadingDialog;
import com.suntek.ibmsapp.widget.ToastHelper;


/**
 * Base Task
 *
 * @author jimmy
 */
public abstract class BaseTask extends AsyncTask<Void, Void, BaseTask.TaskResult>
{
    /**
     * 登陆请求返回信息
     */
    public static class TaskResult
    {
        //用户类
        private Object resultData;

        //错误信息类
        private FHttpException error;

        public TaskResult(Object resultData, FHttpException error)
        {
            this.resultData = resultData;
            this.error = error;
        }

        public Object getResultData()
        {
            return resultData;
        }

        public FHttpException getError()
        {
            return error;
        }
    }


    //上下文
    private Context context;

    public BaseTask(Context context)
    {
        this.context = context;
    }

    /**
     * 显示错误码
     *
     * @param error
     */
    private void showError(FHttpException error, Context context)
    {
        //业务错误
        if (error.getCode() == FHttpException.CODE_BUSINESS_ERROR)
        {
            // show error message from message
            LoadingDialog.getInstance(context).showRequestStatus(error.getMessage(), false, 2000);
        }
        //网络超时
        else if (error.getCode() == FHttpException.CODE_TIMEOUT_ERROR)
        {
            LoadingDialog.getInstance(context).showRequestStatus("网络超时", false, 1000);
        }
        //网络错误
        else
        {
            // show error message for network error
            LoadingDialog.getInstance(context).showRequestStatus("网络错误", false, 2000);
        }
    }

    /**
     * 提示错误
     *
     * @return
     */
    public void toastError(FHttpException error, Context context)
    {
        //业务错误
        if (error.getCode() == FHttpException.CODE_BUSINESS_ERROR)
        {
            // show error message from message
            ToastHelper.getInstance(context).shortShowMessage(error.getMessage());
        }
        //网络超时
        else if (error.getCode() == FHttpException.CODE_TIMEOUT_ERROR)
        {
            ToastHelper.getInstance(context).shortShowMessage("网络超时");
        }
        //网络错误
        else
        {
            // show error message for network error
            ToastHelper.getInstance(context).shortShowMessage("网络错误");
        }
    }

    @Override
    protected abstract TaskResult doInBackground(Void... params);

    protected void onPostExecute(TaskResult result)
    {
        FHttpException error = result.getError();
        if (error != null)
        {
            showError(error, context);
            return;
        }
    }


}
