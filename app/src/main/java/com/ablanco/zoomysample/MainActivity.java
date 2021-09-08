package com.ablanco.zoomysample;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Integer> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImages.addAll(Arrays.asList(R.drawable.img1, R.drawable.img2,
                R.drawable.img3, R.drawable.img4, R.drawable.img5,
                R.drawable.img6, R.drawable.img7, R.drawable.img8,
                R.drawable.img9, R.drawable.img10));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new CommonItemSpaceDecoration(5));
        recyclerView.setAdapter(new Adapter(mImages));

    }

    class Adapter extends RecyclerView.Adapter<ImageViewHolder> {

        private final List<Integer> images;

        Adapter(List<Integer> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new ImageViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
            ((ImageView) holder.itemView).setImageResource(images.get(position));
            holder.itemView.setTag(holder.getBindingAdapterPosition());
            Zoomy.Builder builder = new Zoomy.Builder(MainActivity.this)
                    .target(holder.itemView)
                    .interpolator(new OvershootInterpolator())
                    .tapListener(v -> Toast.makeText(MainActivity.this, "Tap on: "
                            + v.getTag(), Toast.LENGTH_SHORT).show())
                    .longPressListener(v -> Toast.makeText(MainActivity.this, "Long press on: "
                            + v.getTag(), Toast.LENGTH_SHORT).show()).doubleTapListener(v -> Toast.makeText(MainActivity.this, "Double tap on: "
                            + v.getTag(), Toast.LENGTH_SHORT).show());

            builder.register();
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class CommonItemSpaceDecoration extends RecyclerView.ItemDecoration {
        private final int mSpace;

        CommonItemSpaceDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.set(mSpace, mSpace, mSpace, mSpace);
        }
    }
}
