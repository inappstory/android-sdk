package io.casestory.sdk.stories.api.networkclient;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.casestory.sdk.stories.api.networkclient.interceptors.HeadersInterceptor;
import io.casestory.sdk.stories.api.networkclient.interceptors.RepeatInterceptor;
import io.casestory.sdk.stories.api.networkclient.interceptors.UserAgentInterceptor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Основной класс для работы с API. Для взаимодействия с сервером используется OkHttp и Retrofit 2.0
 */

public class ApiClient {

    /**
     * Реализация интерфейса, реализующего запросы к CMS API
     * Singleton
     */
    private static ApiInterface mApi;
    private static ApiInterface mFastApi;

    /**
     * Клиент, используется для коннекта с сервером, как в рамках запросов,
     * реализуемых mApi, так и напрямую (например, для скачивания файлов)
     * Singleton
     */
    private static OkHttpClient mApiClient;
    private static OkHttpClient mImageClient;
    private static OkHttpClient mFastApiClient;

    /**
     * Контекст приложения
     */
    private static Context appContext;

    public static void setContext(Context context) {
        appContext = context;
    }


    /**
     * Получаем наш mApi. Или генерируем, если еще не создан
     */
    public static ApiInterface getApi() {
        if (mApi == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiSettings.getInstance().getCmsUrl())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getApiOk())
                    .build();
            mApi = (ApiInterface)retrofit.create(apiInterface);
        }
        return mApi;
    }

    public static synchronized ApiInterface getFastApi() {
        if (mFastApi == null) {
            long c = System.currentTimeMillis();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiSettings.getInstance().getCmsUrl())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getFastApiOk())
                    .build();
            mFastApi = (ApiInterface)retrofit.create(apiInterface);
        }
        return mFastApi;
    }

    public static Class apiInterface = ApiInterface.class;

    /**
     * Получаем наш mApiClient. Или генерируем, если еще не создан
     */
    public static OkHttpClient getApiOk() {
        if (mApiClient == null) {
            mApiClient = createOk("OKApiCache", ApiSettings.getInstance().getCmsId(), ApiSettings.getInstance().getCmsKey(), -1);
        }
        return mApiClient;
    }

    public static OkHttpClient getFastApiOk() {
        if (mFastApiClient == null) {
            mFastApiClient = createOk("OKApiCache", ApiSettings.getInstance().getCmsId(), ApiSettings.getInstance().getCmsKey(),
                    5);
        }
        return mFastApiClient;
    }

    public static OkHttpClient getImageApiOk() {
        if (mImageClient == null) {
            mImageClient = createOk("OKApiCache", ApiSettings.getInstance().getCmsId(), "", -1);
        }
        return mImageClient;
    }


    /**
     * На данный момент используется для создания mApiClient.
     * Возможно потребуется для создания клиентов с иным таймаутом.
     */
    private static OkHttpClient createOk(String cacheDirName, String appId, String key, long timeout) {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };


        File cacheDir = new File(ApiSettings.getInstance().getCacheDirPath(), cacheDirName + File.pathSeparator);
        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
            try {
                if (!cacheDir.mkdirs()) {
                    throw new IOException("mkdirs returned false");
                }
            } catch (IOException e) {
                //Log.e("API", "unable to create OkHttp cache dir " + cacheDir, e);
            }
        }
        HttpLoggingInterceptor loggingBody = new HttpLoggingInterceptor();
        HttpLoggingInterceptor loggingHeaders = new HttpLoggingInterceptor();
        loggingBody.setLevel(HttpLoggingInterceptor.Level.BODY);
        loggingHeaders.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .cache(new Cache(cacheDir, 10 * 1024 * 1024))
                .addInterceptor(new UserAgentInterceptor(appContext))
                .addInterceptor(new RepeatInterceptor())
                .addInterceptor(loggingBody);
        if (key != null && !key.isEmpty()) {
            client.addInterceptor(new HeadersInterceptor(key));
        }




        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
            sslContext.init(null, trustAllCerts, new SecureRandom());
            client.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (timeout != -1) {
            client.connectTimeout(timeout, TimeUnit.SECONDS);
            client.readTimeout(timeout, TimeUnit.SECONDS);
            client.writeTimeout(timeout, TimeUnit.SECONDS);
        }
        return client.build();
    }

}
