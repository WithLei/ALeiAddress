package cn.renly.aleiaddress.api.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UploadApi {

    @POST("upload")
    Observable<ResponseBody>doUpload(@Query("contactList")String contactList);
}
