package com.example.artest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface RetrofitInterface {

    @Streaming
    @GET
    Call<ResponseBody> downloadFileByUrl(@Url String fileUrl);
}
