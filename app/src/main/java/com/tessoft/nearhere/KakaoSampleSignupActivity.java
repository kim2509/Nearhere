package com.tessoft.nearhere;

import android.content.Intent;
import android.os.Bundle;

import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.Logger;

/**
 * Created by Daeyong on 2015-12-13.
 */
public class KakaoSampleSignupActivity extends BaseActivity {
    /**
     * Main으로 넘길지 가입 페이지를 그릴지 판단하기 위해 me를 호출한다.
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestMe();
    }

    /**
     * 자동가입앱인 경우는 가입안된 유저가 나오는 것은 에러 상황.
     */
    protected void showSignup() {
        Logger.getInstance().d("not registered user");
        redirectLoginActivity();
    }

    protected void redirectMainActivity() {
//        final Intent intent = new Intent(this, SampleMainActivity.class);
//        startActivity(intent);
//        finish();
    }

    protected void redirectLoginActivity() {
        Intent intent = new Intent(this, KakaoSampleLoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 사용자의 상태를 알아 보기 위해 me API 호출을 한다.
     */
    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            protected void onSuccess(final UserProfile userProfile) {
                Logger.getInstance().d("UserProfile : " + userProfile);
                userProfile.saveUserToCache();
                redirectMainActivity();
            }

            @Override
            protected void onNotSignedUp() {
                showSignup();
            }

            @Override
            protected void onSessionClosedFailure(final APIErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.getInstance().d(message);
                redirectLoginActivity();
            }
        });
    }

}