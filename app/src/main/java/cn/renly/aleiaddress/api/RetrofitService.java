package cn.renly.aleiaddress.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.renly.aleiaddress.App;
import cn.renly.aleiaddress.api.api.UploadApi;
import cn.renly.aleiaddress.api.bean.Contact;
import cn.renly.aleiaddress.utils.network.NetConfig;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static UploadApi uploadApi;

    private RetrofitService() {
        throw new AssertionError();
    }

    /**
     * 初始化网络通信服务
     */
    public static void init() {
        Cache cache = new Cache(new File(App.getContext().getCacheDir(), "HttpCache"),
                1024 * 1024 * 100);
        OkHttpClient client = new OkHttpClient.Builder().cache(cache)
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

//        Retrofit retrofit = new Retrofit.Builder()
//                .client(client)
//                .baseUrl(NetConfig.BASE_UPLOAD)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();
//        uploadApi = retrofit.create(UploadApi.class);
    }

    /**************************************             API             **************************************/

    public static Observable<ResponseBody> doUpload(List<Contact> contactList) {
        return uploadApi.doUpload(JSON.toJSONString(contactList))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
