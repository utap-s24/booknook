package com.example.booknook.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.booknook.MainViewModel
import com.example.booknook.R

class SearchUserRowAdapter(private val viewModel: MainViewModel,
                           private var userList: List<User>) : RecyclerView.Adapter<SearchUserRowAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val displayNameTextView: TextView = itemView.findViewById(R.id.displayNameTextView)
        val addButton: ImageButton = itemView.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_user_row, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.usernameTextView.text = currentUser.username
        holder.displayNameTextView.text = currentUser.displayName
        if (viewModel.isFriend(currentUser)) {
            holder.addButton.setImageResource(R.drawable.baseline_check_circle_24)
        } else {
            holder.addButton.setImageResource(R.drawable.baseline_add_circle_outline_24)
        }

        holder.addButton.setOnClickListener {
            if (viewModel.isFriend(currentUser)) {
                holder.addButton.setImageResource(R.drawable.baseline_add_circle_outline_24)
            } else {
                holder.addButton.setImageResource(R.drawable.baseline_check_circle_24)
            }
            viewModel.updateFriendsList(currentUser)
        }

        holder.usernameTextView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun submitList(newList: List<User>) {
        userList = newList
        notifyDataSetChanged()
    }
}
