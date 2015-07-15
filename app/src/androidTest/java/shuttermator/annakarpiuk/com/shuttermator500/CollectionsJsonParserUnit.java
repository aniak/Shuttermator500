package shuttermator.annakarpiuk.com.shuttermator500;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import shuttermator.annakarpiuk.com.json.CollectionJsonParser;


public class CollectionsJsonParserUnit extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCorrectJsonParsing() throws IOException {
        AssetManager assets = getInstrumentation().getContext().getAssets();
        String featuredCollection = readAsset(assets.open("FeaturedCollectionCorrect.txt"));

        List<CollectionJsonParser.FeaturedItem> collection =
                CollectionJsonParser.parseCollectionResult(featuredCollection, null);

        assertEquals(collection.size(), 65);
        assertEquals(collection.get(0).getId(), 35635493);
        assertEquals(collection.get(0).getUrl(), "http://ak.picdn.net/assets/cms/59b0c89c39e0ee75777fba12a4089ddc46445cfa-shutterstock_278725967 - COVER.jpg");

        assertEquals(collection.size(), 65);
        assertEquals(collection.get(1).getId(), 32145340);
        assertEquals(collection.get(1).getUrl(), "http://ak.picdn.net/assets/cms/c31d696e6fd2119546562e862b2e153c3c1856b4-shutterstock_250520212 - COVER.jpg");
    }

    public void testUncorrectJsonParsing() throws IOException {
        AssetManager assets = getInstrumentation().getContext().getAssets();
        String featuredCollection = readAsset(assets.open("FeaturedCollectionUncorrect.txt"));

        List<CollectionJsonParser.FeaturedItem> collection =
                CollectionJsonParser.parseCollectionResult(featuredCollection, getInstrumentation().getTargetContext());

        assertEquals(collection.size(), 0);
    }

    private String readAsset(InputStream stream) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            stream.close();
        }
    }
}
