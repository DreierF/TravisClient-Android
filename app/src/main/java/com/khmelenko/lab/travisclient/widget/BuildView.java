package com.khmelenko.lab.travisclient.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.converter.BuildStateHelper;
import com.khmelenko.lab.travisclient.converter.TimeConverter;
import com.khmelenko.lab.travisclient.network.response.Commit;
import com.khmelenko.lab.travisclient.network.response.IBuildState;
import com.khmelenko.lab.travisclient.network.response.RequestData;
import com.khmelenko.lab.travisclient.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * View with build info
 *
 * @author Dmytro Khmelenko
 */
public class BuildView extends LinearLayout {

    @Bind(R.id.build_indicator)
    View mIndicator;

    @Bind(R.id.build_number)
    TextView mTitle;

    @Bind(R.id.build_pull_request_title)
    TextView mPullRequest;

    @Bind(R.id.build_branch)
    TextView mBranch;

    @Bind(R.id.build_commit_message)
    TextView mCommitMessage;

    @Bind(R.id.build_commit_person)
    TextView mCommitPerson;

    @Bind(R.id.build_duration)
    TextView mDuration;

    @Bind(R.id.build_finished)
    TextView mFinished;

    public BuildView(Context context) {
        super(context);
        initializeViews(context);
    }

    public BuildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public BuildView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_build, this);
        ButterKnife.bind(this, view);
    }

    /**
     * Sets build duration
     *
     * @param durationInMillis Duration
     */
    public void setDuration(long durationInMillis) {
        if (durationInMillis != 0) {
            String duration = TimeConverter.durationToString(durationInMillis);
            duration = getContext().getString(R.string.build_duration, duration);
            mDuration.setText(duration);
            mDuration.setVisibility(VISIBLE);
        } else {
            mDuration.setVisibility(GONE);
        }
    }

    /**
     * Sets the message when the build was finished
     *
     * @param finishedAt String with the message when the build was finished
     */
    public void setFinishedAt(String finishedAt) {
        if (!TextUtils.isEmpty(finishedAt)) {
            String formattedDate = DateTimeUtils.parseAndFormatDateTime(finishedAt);
            formattedDate = getContext().getString(R.string.build_finished_at, formattedDate);
            mFinished.setText(formattedDate);
            mFinished.setVisibility(VISIBLE);
        } else {
            mFinished.setVisibility(GONE);
        }
    }

    /**
     * Sets branch data
     *
     * @param buildState Branch
     */
    public void setBuildState(IBuildState buildState) {
        if (buildState != null) {
            setTitle(getContext()
                    .getString(R.string.build_build_number, buildState.getNumber(), buildState.getState()));
            setStateIndicator(buildState.getState());
            setFinishedAt(buildState.getFinishedAt());
            setDuration(buildState.getDuration());
        }
    }

    /**
     * Sets commit data
     *
     * @param commit Last commit
     */
    public void setCommit(Commit commit) {
        if (commit != null) {
            mBranch.setVisibility(VISIBLE);
            mBranch.setText(commit.getBranch());
            mCommitMessage.setVisibility(VISIBLE);
            mCommitMessage.setText(commit.getMessage());
            mCommitPerson.setVisibility(VISIBLE);
            mCommitPerson.setText(commit.getCommitterName());
        }
    }

    public void setTitle(String string) {
        mTitle.setText(string);
    }

    public void setStateIndicator(String state) {
        if (!TextUtils.isEmpty(state)) {
            int buildColor = BuildStateHelper.getBuildColor(state);
            mIndicator.setBackgroundColor(buildColor);
            mTitle.setTextColor(buildColor);

            Drawable drawable = BuildStateHelper.getBuildImage(state);
            if (drawable != null) {
                mTitle.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
    }

    public void setPullRequestTitle(RequestData request) {
        mPullRequest.setVisibility(VISIBLE);
        mPullRequest.setText(request.getPullRequestTitle());
    }
}
