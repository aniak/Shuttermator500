package shuttermator.annakarpiuk.com.shuttermator500;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import shuttermator.annakarpiuk.com.json.CollectionJsonParser;
import shuttermator.annakarpiuk.com.request.Requester;

public class ShuttermatorMainFragment extends Fragment {

    private ListView mPicturesList;
    private PicturesAdapter mAdapter;
    private RequestAsync mAsyncDownloader;
    private Picasso mPicasso;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shuttermator_main, container, false);
        mPicturesList = (ListView) view.findViewById(android.R.id.list);
        mAdapter = new PicturesAdapter(getActivity());
        mPicturesList.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // start loading data
        mAsyncDownloader = new RequestAsync(this);
        mAsyncDownloader.execute();
    }

    private class PicturesAdapter extends BaseAdapter {
        private List<CollectionJsonParser.FeaturedItem> mItems;
        private Context mContext;
        private LayoutInflater mInflater;
        private int mImageSize;

        public PicturesAdapter(Context context){
            super();
            mContext = context;
            mInflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            mPicasso = Picasso.with(mContext);
            mImageSize = getResources().getDimensionPixelSize(R.dimen.gallery_image_size);
            mItems = new ArrayList<CollectionJsonParser.FeaturedItem>();
        }

        public void addCollection(List<CollectionJsonParser.FeaturedItem> newItems) {
            mItems.addAll(newItems);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            if(position >= mItems.size()) return null;
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(position >= mItems.size()) return 0;
            return mItems.get(position).getId();
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView picture;
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_image, null);
            }

            picture = (ImageView)convertView.findViewById(R.id.picture);

            // load image to the given view
            mPicasso.load(mItems.get(position).getUrl())
                    .resize(mImageSize, mImageSize)
                    .centerCrop().into(picture);

            return convertView;
        }
    }

    // since there is no 'refresh' button available, on orientation change
    // doesn't keep loaded data but refresh it
    private class RequestAsync extends AsyncTask<Void, Void, String> {

        // keep weak reference to the fragment to protect against
        // memory leak and reaching view that is no longer visible
        private WeakReference<ShuttermatorMainFragment> mFragment;

        public RequestAsync(ShuttermatorMainFragment fragment) {
            mFragment = new WeakReference<ShuttermatorMainFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ShuttermatorMainFragment fragment = mFragment.get();
            // activity/fragment doesn't exist any more so there is no view to
            // deliver changes
            if(fragment == null) return;

            TextView emptyListText = (TextView)fragment.getView().findViewById(android.R.id.empty);
            emptyListText.setText(R.string.loading);
        }

        @Override
        protected String doInBackground(Void... v) {
            Requester requester = new Requester();
            String featuredCollection = null;
            try {
                featuredCollection = requester.makeRequestFeaturesCollection();
            } catch (IOException e) {
                return null;
            }
            return featuredCollection;
        }

        @Override
        protected void onPostExecute(String items) {
            ShuttermatorMainFragment fragment = mFragment.get();
            // activity/fragment doesn't exist any more so there is no view to
            // deliver changes
            if(fragment == null) return;

            // get list empty view to be modified depending on query result
            TextView emptyListText = (TextView)fragment.getView().findViewById(android.R.id.empty);

            if(items != null) {
                List<CollectionJsonParser.FeaturedItem> parsedCollection;
                parsedCollection =
                        CollectionJsonParser.parseCollectionResult(items, getActivity());

                fragment.mAdapter.addCollection(parsedCollection);

                if(mAdapter.getCount() != 0) {
                    emptyListText.setVisibility(View.GONE);
                } else {
                    emptyListText.setText(R.string.nothing_loaded);
                }
            } else {
                // show toast that something went wrong and update list empty view
                Toast.makeText(getActivity(),
                        R.string.internet_problem, Toast.LENGTH_LONG)
                        .show();
                emptyListText.setText(R.string.nothing_loaded);
            }
        }
    }
}
