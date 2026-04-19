package com.sisindia.ai.mtrainer.android.rest;

import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SaveTokenRequest;
import com.sisindia.ai.mtrainer.android.models.SaveTokenResponse;
import com.sisindia.ai.mtrainer.android.models.UserRequest;
import com.sisindia.ai.mtrainer.android.models.UserResponse;
import com.sisindia.ai.mtrainer.android.models.VerifyOtpResponse;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthApi {

    String PRE_AUTH_TOKEN = "Token";
    // String GENERATE_OTP = "api/LoginOperation/login"; //http://50.31.147.142/api/LoginOperation/login
    //String GENERATE_OTP = "api/LoginOperation/LoginUpdated";
    String GENERATE_OTP = "api/loginOperation/FireBaseLogin"; //http://50.31.147.142/api/LoginOperation/login
    String OTP_VERIFACTION = "api/LoginOperation/OTPVerification"; //http://50.31.147.142/api/LoginOperation/OTPVerification
    String GC_GET_TOKEN_RESPONSE = "SISAPI/api/values/SaveUserToken";

    @POST(PRE_AUTH_TOKEN)
    @FormUrlEncoded
    Single<PreAuthResponse> getPreAuth(@Field(RestConstants.GRANT_TYPE_KEY) String grantType,
                                       @Field(RestConstants.USER_NAME_KEY) String userName,
                                       @Field(RestConstants.PASSWORD_KEY) String password);

    @POST(GC_GET_TOKEN_RESPONSE)
    Call<SaveTokenResponse> gettoken(@Body SaveTokenRequest request);

    @POST(GENERATE_OTP)
    Single<UserResponse> generateOtp(@Body UserRequest request);

    @POST(OTP_VERIFACTION)
    Single<VerifyOtpResponse> verifyOtp(@Body UserRequest request);
}