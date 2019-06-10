package com.marielm.android101;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

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
    // TODO


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
    service.getRepositories("")
        .enqueue(new Callback<List<Repository>>() {
          @Override
          public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
            // hide progress
            // display results
          }

          @Override
          public void onFailure(Call<List<Repository>> call, Throwable t) {
            // hide progress
            // show error
          }
        });
  }

  /**
   * Creates a short toast that displays error from throwable
   *
   * @param error comes back from onFailure case
   */
  private void showError(Throwable error) {
    Toast.makeText(ResultsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
          .inflate(R.layout.row_item_repo, parent);

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

    public RepositoryViewHolder(View itemView) {
      super(itemView);

      idView = findViewById(R.id.id);
      name = findViewById(R.id.name);
      description = findViewById(R.id.description);
      updatedOn = findViewById(R.id.updated_on);
    }

    public void bind(Repository repo) {

    }
  }
}
