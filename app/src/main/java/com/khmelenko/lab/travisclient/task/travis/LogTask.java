package com.khmelenko.lab.travisclient.task.travis;

import android.text.TextUtils;

import com.khmelenko.lab.travisclient.event.travis.LogFailEvent;
import com.khmelenko.lab.travisclient.event.travis.LogLoadedEvent;
import com.khmelenko.lab.travisclient.task.Task;
import com.khmelenko.lab.travisclient.task.TaskError;
import com.khmelenko.lab.travisclient.task.TaskException;

import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Task for getting log
 *
 * @author Dmytro Khmelenko
 */
public final class LogTask extends Task<String> {

    private final String mAuth;
    private final long mJobId;

    /**
     * Constructor
     *
     * @param auth  Authorization header
     * @param jobId Job ID
     */
    public LogTask(String auth, long jobId) {
        mAuth = auth;
        mJobId = jobId;
    }

    @Override
    public String execute() throws TaskException {
        String redirectUrl = "";

        try {
            if(TextUtils.isEmpty(mAuth)) {
                mRestClient.getRawApiService().getLog(String.valueOf(mJobId));
            } else {
                mRestClient.getRawApiService().getLog(mAuth, String.valueOf(mJobId));
            }

        } catch (TaskException exception) {
            Response response = exception.getTaskError().getResponse();
            for (Header header : response.getHeaders()) {
                if (header.getName().equals("Location")) {
                    redirectUrl = header.getValue();
                    break;
                }
            }

            boolean redirect = response.getStatus() == 307 && !TextUtils.isEmpty(redirectUrl);
            if (!redirect) {
                throw exception;
            }
        }
        return redirectUrl;
    }

    @Override
    public void onSuccess(String result) {
        LogLoadedEvent event = new LogLoadedEvent(result);
        mEventBus.post(event);
    }

    @Override
    public void onFail(TaskError error) {
        LogFailEvent event = new LogFailEvent(error);
        mEventBus.post(event);
    }
}
