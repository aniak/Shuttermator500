package shuttermator.annakarpiuk.com.request;

import android.util.Base64;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Requester {
    private static final String TAG = Requester.class.getSimpleName();

    private static final String CLIENT_ID = "ae467526e8d6be2981c1";
    private static final String CLIENT_SECRET = "9054ba1a52acf830e0c4e9cb389783edcdbae1af";

    private static final String ENDPOINT = "https://api.shutterstock.com/v2";
    private static final String REQUEST_FEATURED_COLLECTIONS = "/images/collections/featured";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private OkHttpClient mClient;

    public Requester () {
        mClient = new OkHttpClient();
    }

    /**
     * This method requests for the featured collections of the shuttersttock.
     *
     * @return String representing Json returned by the service or null if there was problem
     *
     * @throws IOException might occur during connection or stream closing
     */
    public String makeRequestFeaturesCollection() throws IOException {
            Request request = new Request.Builder().url(ENDPOINT+ REQUEST_FEATURED_COLLECTIONS).
                            header(AUTHORIZATION_HEADER, "Basic " +
                            Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes(), Base64.NO_WRAP))
                    .build();

            Response response = null;
            InputStream stream = null;
            try {
                response = mClient.newCall(request).execute();
                stream = response.body().byteStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                return sb.toString();
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
    }
}
