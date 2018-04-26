package com.snownaul.studyguide.Classes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

/**
 * Created by alfo6-11 on 2018-04-24.
 */

public class GlobalApplication extends Application {

    private static volatile GlobalApplication obj=null;
    private static volatile Activity currentActivity=null;

    @Override
    public void onCreate() {
        super.onCreate();
        obj=this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GlobalApplication getGlobalApplicationContext(){
        return obj;
    }

    public static Activity getCurrentActivity(){
        return currentActivity;
    }

    //Activity가 올라올 때마다 Activity 의 onCreate에서 호출해 줘야 한다.
    public static void setCurrentActivity(Activity currentActivity){
        GlobalApplication.currentActivity=currentActivity;
    }

    private static class KakaoSDKAdapter extends KakaoAdapter{

        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType() {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Activity getTopActivity() {
                    return null;
                }

                @Override
                public Context getApplicationContext() {
                    return GlobalApplication.getGlobalApplicationContext();
                }
            };
        }
    }
}
