package com.sht.smartlock;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.igexin.sdk.PushManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.ui.activity.base.BaseActivity;
import com.sht.smartlock.util.LogUtil;
import com.sht.smartlock.widget.dialog.ProgressDialog;

import org.apache.http.Header;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TestActivity extends BaseActivity {
    private static final String STORE_PASS = "Fineswap";
    private static final String STORE_ALIAS = "rootca";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_test);
//        PushManager.getInstance().initialize(this.getApplicationContext());
        Button btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HttpClient.instance().hotelSearch("101","白石洲","100","300","2015-09-14","2015-09-25", new LoginCallBack());
                HttpClient.instance().test(22, 15, new LoginCallBack());
//                InputStream is = null;
//                try {
//                try {
//                    // Configure the library to use a custom 'bks' file to perform
//                    // SSL negotiation.
//                     KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
//                    is = getResources().openRawResource(R.raw.tclient);
//                    store.load(is, STORE_PASS.toCharArray());
//                    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//                    asyncHttpClient.setSSLSocketFactory(new SecureSocketFactory(store, STORE_ALIAS));
//                    asyncHttpClient.get("https://www.demo.com", new AsyncHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                            System.out.println("success " + new String(bytes));
//                        }
//
//                        @Override
//                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                            throwable.printStackTrace(System.out);
//                            LogUtil.log("FAILLLLLLLL");
//                        }
//                    });
//                } catch (IOException e) {
//                    throw new KeyStoreException(e);
//                } catch (CertificateException e) {
//                    throw new KeyStoreException(e);
//                } catch (NoSuchAlgorithmException e) {
//                    throw new KeyStoreException(e);
//                } catch (KeyManagementException e) {
//                    throw new KeyStoreException(e);
//                } catch (UnrecoverableKeyException e) {
//                    throw new KeyStoreException(e);
//                } finally {
//                    AsyncHttpClient.silentCloseInputStream(is);
//                }
//                } catch (KeyStoreException e) {
//                    Log.e("AAAA", "Unable to initialize key store", e);
//                }


                LogUtil.log("aaaaa");
                //              new AsyncTask<Void, Void, String>() {
//
//                    @Override
//                    protected String doInBackground(Void... params) {
//
//
//
//                            SSLContext sc = null;
//                            try {
//                                sc = SSLContext.getInstance("TLS");
//                            } catch (NoSuchAlgorithmException e) {
//                                e.printStackTrace();
//                            }
//                            try {
//                                sc.init(null, new TrustManager[]{new X509TrustManager() {
//                                    @Override
//                                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                                    }
//
//
//                                    @Override
//                                    public void checkServerTrusted(X509Certificate[] chain, String authType)
//
//
//                                            throws CertificateException {
//                                    }
//
//
//                                    @Override
//                                    public X509Certificate[] getAcceptedIssuers() {
//                                        return null;
//                                    }
//                                }}, new SecureRandom());
//                            } catch (KeyManagementException e) {
//                                e.printStackTrace();
//                            }
//                            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//                            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                                @Override
//                                public boolean verify(String arg0, SSLSession arg1) {
//                                    return true;
//                                }
//                            });
//                            String https = "https://www.demo.com";
//                            try {
//                                HttpsURLConnection conn = (HttpsURLConnection) new URL(https).openConnection();
//                                conn.setDoOutput(true);
//                                conn.setDoInput(true);
//                                conn.connect();
//                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                                StringBuffer sb = new StringBuffer();
//                                String line;
//                                while ((line = br.readLine()) != null)
//                                    sb.append(line);
//                                //  Log(sb.toString());
//                                System.out.println("Result:" + sb.toString());
//
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }

//
//                            URLConnection uc;
//
//                            uc = new URL("https://www.demo.com").openConnection();
//
//                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
//                            String line = "";
//                            StringBuffer result = new StringBuffer();
//                            while ((line = bufferedReader.readLine()) != null) {
//                                result.append(line);
//                            }
//                            System.out.println("Result:"  );

                //                       return null;
//                    }
                //               }.execute();
            }
        });

    }

    public class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }
            };
            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private class LoginCallBack extends HttpCallBack {
        public void onStart() {
            super.onStart();
            LogUtil.log("paramss: onstart");
        }

        @Override
        public void onFailure(String error, String message) {
            super.onFailure(error, message);
            ProgressDialog.disMiss();
            //  toastFail(R.string.login_fail);
            LogUtil.log("paramss f: " + error);
        }

        @Override
        public void onSuccess(ResponseBean responseBean) {
            ProgressDialog.disMiss();
            //   Toast.makeText(getApplicationContext(),responseBean.getData(),Toast.LENGTH_LONG).show();
            //   LogUtil.log("paramss: " + responseBean.toString());
            //   List<User> u= (List<User>) responseBean.getListDataWithGson(User.class);
            //  User u = responseBean.getData(User.class, "results");
            LogUtil.log("paramss s: " + responseBean.toString());

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }
}
