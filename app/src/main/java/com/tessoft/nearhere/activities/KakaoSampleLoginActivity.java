package com.tessoft.nearhere.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.kakao.widget.LoginButton;
import com.tessoft.nearhere.R;

/**
 * Created by Daeyong on 2015-12-13.
 */
public class KakaoSampleLoginActivity extends BaseActivity {
    private LoginButton loginButton;
    private final SessionCallback mySessionCallback = new MySessionStatusCallback();
    private Session session;

    /**
     * super.onCreate를 호출하여 Session처리를 맡긴다.
     * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kakao_login);
        // 로그인 버튼을 찾아온다.
        loginButton = (LoginButton) findViewById(R.id.com_kakao_login);

        session = Session.getCurrentSession();
        session.addCallback(mySessionCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.removeCallback(mySessionCallback);
    }

    protected void onResume() {
        super.onResume();

        if (session.isClosed()){
            loginButton.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.GONE);
            session.implicitOpen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MySessionStatusCallback implements SessionCallback {
        /**
         * 세션이 오픈되었으면 가입페이지로 이동 한다.
         */
        @Override
        public void onSessionOpened() {
            // 뺑글이 종료

            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            KakaoSampleLoginActivity.this.onSessionOpened();
        }

        /**
         * 세션이 삭제되었으니 로그인 화면이 보여야 한다.
         * @param exception  에러가 발생하여 close가 된 경우 해당 exception
         */
        @Override
        public void onSessionClosed(final KakaoException exception) {
            // 뺑글이 종료

            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈을 못했으니 다시 로그인 버튼 노출.
            loginButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onSessionOpening() {
            //뺑글이 시작
        }
    }

    protected void onSessionOpened(){
        final Intent intent = new Intent(KakaoSampleLoginActivity.this, KakaoSampleSignupActivity.class);
        startActivity(intent);
        finish();
    }

    protected void setBackground(Drawable drawable) {
        final View root = findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            root.setBackground(drawable);
        } else {
            root.setBackgroundDrawable(drawable);
        }
    }

}