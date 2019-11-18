package com.marielm.android101;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsActivity extends AppCompatActivity {

  private static final String EXTRA_USERNAME = "extra_username";
  private static final String GITHUB_URL = "https://api.github.com/";
  private View progressBar;
  private RecyclerView recyclerView;
  private Toolbar toolbar;
  private MyAdapter adapter;


  public static Intent create(Context context, String username) {
    return new Intent(context, ResultsActivity.class)
        .putExtra(EXTRA_USERNAME, username);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_results);

    // bind activity views
    progressBar = findViewById(R.id.progress_bar);
    recyclerView = findViewById(R.id.recycler_view);
    toolbar = findViewById(R.id.toolbar);

    // set toolbar navigation
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });


    // retrieve username from intent extras bundle
    String githubUsername = getIntent().getExtras().getString(EXTRA_USERNAME);


    // create & set adapter
    adapter = new MyAdapter();
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);


    // Create Retrofit Object & API Service
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(GITHUB_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    GithubService service = retrofit.create(GithubService.class);


    // make api call
    service.getRepositories(githubUsername)
        .enqueue(new Callback<List<Repository>>() {
          @Override
          public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
            showProgress(false);
            if (response.isSuccessful()) {
              adapter.setData(response.body());
            } else {
              showError(new Throwable("response was not successful! :O"));
            }
          }

          @Override
          public void onFailure(Call<List<Repository>> call, Throwable t) {
            showProgress(false);
            showError(t);
          }
        });
  }

  /**
   * Creates a short toast that displays error from throwable
   *
   * @param error comes back from onFailure case
   */
  private void showError(Throwable error) {
    Toast.makeText(ResultsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
  }

  /**
   * Shows and hides progress bar
   *
   * @param show .
   */
  private void showProgress(boolean show) {
    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  /**
   * Custom Adapter that contains the data fetched from the API
   */
  class MyAdapter extends RecyclerView.Adapter<RepositoryViewHolder> {
    private List<Repository> data = new ArrayList<>();

    public void setData(List<Repository> data) {
      this.data = data;
    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.row_item_repo, parent, false);

      return new RepositoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {
      Repository repository = data.get(position);
      holder.bind(repository);
    }

    @Override
    public int getItemCount() {
      return data.size();
    }
  }

  /**
   * A ViewHolder that will bind our data to our views
   */
  private class RepositoryViewHolder extends RecyclerView.ViewHolder {

    private final TextView idView;
    private final TextView name;
    private final TextView description;
    private final TextView updatedOn;
    private View itemView;

    public RepositoryViewHolder(View itemView) {
      super(itemView);

      idView = itemView.findViewById(R.id.id);
      name = itemView.findViewById(R.id.name);
      description = itemView.findViewById(R.id.description);
      updatedOn = itemView.findViewById(R.id.updated_on);
      this.itemView = itemView;
    }

    public void bind(final Repository repo) {

      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Snackbar.make(itemView, String.valueOf(repo.watchers), Snackbar.LENGTH_SHORT).show();
        }
      });

      idView.setText(String.valueOf(repo.id));
      name.setText(repo.name);

      if (TextUtils.isEmpty(repo.description)) {
        description.setText(getString(R.string.add_desc));
      } else {
        description.setText(repo.description);
      }

      // try to format date/time coming back
      try {
        SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date parsedDate = currentFormat.parse(repo.updatedOn);

        SimpleDateFormat formatted = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = formatted.format(parsedDate);
        updatedOn.setText(formattedDate);
      } catch (Exception e) {
        updatedOn.setText(repo.updatedOn);
      }
    }
  }
}
