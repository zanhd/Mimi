package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zanhd.mimi.R;

import java.util.List;

import model.User;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public HomeRecyclerAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meme_row, viewGroup,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerAdapter.ViewHolder viewHolder, int position) {

        User user = userList.get(position);
        String imageUrl;

        viewHolder.usernameTextView.setText(user.getUsername());

        imageUrl = user.getImageUrl();

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.example_image) //in case there is no image this default image will show
                .fit()
                .into(viewHolder.memeImageView);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameTextView;
        public ImageView memeImageView;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            usernameTextView = itemView.findViewById(R.id.meme_row_post_meme);
            memeImageView = itemView.findViewById(R.id.meme_row_meme_imageView);

        }
    }
}
