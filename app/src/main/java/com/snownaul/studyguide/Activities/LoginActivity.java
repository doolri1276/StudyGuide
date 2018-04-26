package com.snownaul.studyguide.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.snownaul.studyguide.R;

public class LoginActivity extends AppCompatActivity {

    //카카오 로그인을 위한 것들
    private SessionCallback callback;
    LoginButton loginButton;

    //구글 로그인을 위한 것들.
    private static final String TAG="GoogleActivity";
    private static final int RC_SIGN_IN=9001;

    private FirebaseAuth auth;

    private GoogleSignInClient client;



    GoogleSignInAccount account;
    GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //카카오 로그인을 위한 것들
        callback=new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        loginButton=findViewById(R.id.kakao_loginbtn);

        //구글 로그인을 위한 것들
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        client=GoogleSignIn.getClient(this,gso);

        FirebaseApp.initializeApp(this);

        auth=FirebaseAuth.getInstance();




    }

    @Override
    protected void onStart() {
        super.onStart();

        //로그인 이미 되어있는지 확인한다.
        FirebaseUser currentUser=auth.getCurrentUser();
        updateUI(currentUser);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //로그인 성공, authenticate with Firebase
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                //로그인 실패
                Log.w(TAG,"Google sign in failed",e);

                updateUI(null);
            }
        }




        if(Session.getCurrentSession().handleActivityResult(requestCode,resultCode,data)){
            return;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clickKakao(View v){
        loginButton.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    private class SessionCallback implements ISessionCallback{

        //로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            KakaoRequestMe();
        }

        //로그인에 실패한 사태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception!=null){
                Logger.e(exception);
            }
        }
    }

    //카카오톡 로그인//////////////////////////////////////////////
    //사용자 정보 요청
    public void KakaoRequestMe(){

        //사용자정보 요청 결과에 대한 Callback
        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.d("Error", "오류로 카카오로그인 실패 ");
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("Error", "오류로 카카오로그인 실패 ");
            }

            //회원이 아닌 경우
            @Override
            public void onNotSignedUp() {

            }

            //사용자 정보 요청에 성공한 경우
            @Override
            public void onSuccess(UserProfile result) {
                redirectFirstSetActivity();
            }
        });
    }//////////////////////////////////////////////////////////////

    //구글 로그인///////////////////////////////////////////////////
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG,"firebaseAuthWithGoogle : "+acct.getId());

        AuthCredential credential= GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG,"signInWithCredential:success");
                            FirebaseUser user=auth.getCurrentUser();
                            updateUI(user);
                        }else{
                            Log.w(TAG,"signInWithCredential : failure",task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    private void signIn(){
        Intent signInIntent=client.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            Log.i("User","Email : "+getString(R.string.google_status_fmt,user.getEmail()));
            Log.i("User","Uid : "+getString(R.string.firebase_status_fmt,user.getUid()));


        }else{
            Log.i("User",getString(R.string.signed_out));
        }
    }

    protected  void redirectFirstSetActivity(){
        final Intent intent=new Intent(this,FirstSettingActivity.class);
        startActivity(intent);
        finish();
    }
}
