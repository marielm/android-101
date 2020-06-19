package com.marielm.android101

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

private const val KEY_USERNAME = "key_username"
private const val GITHUB_URL = "https://api.github.com/"

private const val DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'"
private const val DATE_FORMAT = "dd-MM-yyyy"


class ResultsActivity : AppCompatActivity() {
    private var progressBar: View? = null
    private var recyclerView: RecyclerView? = null
    private var toolbar: Toolbar? = null
    private var adapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // bind activity views
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById(R.id.recycler_view)
        toolbar = findViewById(R.id.toolbar)

        // set toolbar navigation
        toolbar?.setNavigationOnClickListener { onBackPressed() }


        // retrieve username from intent extras bundle
        val githubUsername: String? = intent.extras!!.getString(KEY_USERNAME)

        // create & set adapter
        adapter = MyAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter


        // Create Retrofit Object & API Service
        val retrofit = Retrofit.Builder()
                .baseUrl(GITHUB_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service: GithubService = retrofit.create(GithubService::class.java)


        showProgress(true)

        // make api call
        githubUsername?.let {
            service.getRepositories(it)
                    .enqueue(object : Callback<List<Repository>> {
                        override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                            showProgress(false)
                            if (response.isSuccessful && response.body() != null) {
                                adapter!!.setData(response.body() as List<Repository>)
                            } else {
                                showError(Throwable("response was not successful! :O"))
                            }
                        }

                        override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                            showProgress(false)
                            showError(t)
                        }
                    })
        } ?: run {
            showProgress(false)
            showError(Throwable("No username to fetch repositories for."))
        }
    }

    /**
     * Creates a short toast that displays error from throwable
     *
     * @param error comes back from onFailure case
     */
    private fun showError(error: Throwable) {
        Toast.makeText(this@ResultsActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows and hides progress bar
     *
     * @param show .
     */
    private fun showProgress(show: Boolean) {
        progressBar!!.visibility = if (show) View.VISIBLE else View.GONE
    }


    /**
     * Custom Adapter that contains the data fetched from the API
     */
    class MyAdapter : RecyclerView.Adapter<RepositoryViewHolder>() {
        private var data: List<Repository> = ArrayList()

        fun setData(data: List<Repository>) {
            this.data = data
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_item_repo, parent, false)

            return RepositoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
            val repository: Repository = data[position]
            holder.bind(repository)
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }


    /**
     * A ViewHolder that will bind our data to our views
     */
    class RepositoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val idView: TextView = itemView.findViewById(R.id.id)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val updatedOn: TextView = itemView.findViewById(R.id.updated_on)

        fun bind(repo: Repository) {

            itemView.setOnClickListener { Snackbar.make(itemView, repo.watchers.toString(), Snackbar.LENGTH_SHORT).show() }

            idView.text = repo.id.toString()
            name.text = repo.name

            if (TextUtils.isEmpty(repo.description)) {
                description.setText(R.string.add_desc)
            } else {
                description.text = repo.description
            }

            // try to format date/time coming back
            try {
                val currentFormat = SimpleDateFormat(DATE_FORMAT_UTC)
                val parsedDate: Date = currentFormat.parse(repo.updatedOn)

                val formatted = SimpleDateFormat(DATE_FORMAT)
                val formattedDate: String = formatted.format(parsedDate)
                updatedOn.text = formattedDate
            } catch (e: Exception) {
                updatedOn.text = repo.updatedOn
            }

        }
    }

    companion object {
        fun create(context: Context, username: String): Intent {
            return Intent(context, ResultsActivity::class.java)
                    .putExtra(KEY_USERNAME, username)
        }
    }
}
