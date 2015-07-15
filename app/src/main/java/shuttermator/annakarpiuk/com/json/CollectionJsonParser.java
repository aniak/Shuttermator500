package shuttermator.annakarpiuk.com.json;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import shuttermator.annakarpiuk.com.shuttermator500.R;

public class CollectionJsonParser {

    private static final String JSON_DATA_ARRAY = "data";
    private static final String JSON_ITEM_ID = "id";
    private static final String JSON_ITEM_COVER = "cover_item";
    private static final String JSON_ITEM_COVER_URL = "url";

    public static final class FeaturedItem {
        private int id;
        private String url;

        public int getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }
    };

    public static List<FeaturedItem> parseCollectionResult(String json, Context context) {
        List<FeaturedItem> items = new ArrayList<FeaturedItem>();

        try {
            JSONObject jObject = new JSONObject(json);
            JSONArray jArray = jObject.getJSONArray(JSON_DATA_ARRAY);

            for(int i=0; i<jArray.length(); i++) {
                FeaturedItem featuredItem = new FeaturedItem();
                JSONObject item = jArray.getJSONObject(i);
                featuredItem.id = item.getInt(JSON_ITEM_ID);
                JSONObject cover = item.getJSONObject(JSON_ITEM_COVER);
                featuredItem.url = cover.optString(JSON_ITEM_COVER_URL);

                items.add(featuredItem);
            }

        } catch (JSONException e) {
            Toast.makeText(context,
                    R.string.obtaining_data_problem, Toast.LENGTH_LONG)
                    .show();
        }
        return items;
    }
}
