package com.example.chat.ui.adapters

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.databinding.ViewholderUserBinding
import io.getstream.chat.android.client.models.User

class UsersAdapter(private val onClick: (User) -> Unit): ListAdapter<User, UsersAdapter.MyViewHolder>(DIFF_UTIL) {

    companion object {
        val DIFF_UTIL = object: DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return false
            }
        }
    }

    inner class MyViewHolder(val binding: ViewholderUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            /*binding.avatarImageView.setUserData(user)
            binding.usernameTextView.text = user.id
            binding.lastActiveTextView.text = convertDate(user.lastActive!!.time)
            binding.rootLayout.setOnClickListener {
                onClick(user)
            }*/
        }

        private fun convertDate(milliseconds: Long): String {
            return DateFormat.format("dd/MM/yyyy hh:mm a", milliseconds).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ViewholderUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUser = getItem(position)
        holder.bind(currentUser)
    }
}