package com.sduduzog.slimlauncher.ui.main.notes

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sduduzog.slimlauncher.R
import com.sduduzog.slimlauncher.data.Note
import com.sduduzog.slimlauncher.ui.main.OnItemActionListener
import java.text.SimpleDateFormat
import java.util.*

class NotesListAdapter(private val fragment: NotesListFragment) : RecyclerView.Adapter<NotesListAdapter.ViewHolder>(), OnItemActionListener {

    private var deletedFrom = 0
    private var notes: ArrayList<Note> = arrayListOf()
    private var viewModel = ViewModelProviders.of(fragment).get(NotesViewModel::class.java)

    init {
        viewModel.notes.observe(fragment, Observer {
            if (it != null) {
                updateNotes(it.orEmpty())
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notes_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        with(note.title) {
            if (this != null && this.isNotEmpty()) {
                holder.title.text = this
                holder.title.visibility = View.VISIBLE
            } else {
                holder.title.visibility = View.GONE
            }
        }
        holder.body.text = note.body
        val fWatchDate = SimpleDateFormat("HH:mm, MMMM dd, yyyy", Locale.US)
        holder.edited.text = fragment.getString(R.string.notes_date_placeholder, fWatchDate.format(note.edited))
        val bundle = Bundle()
        bundle.putSerializable("note", note)
        holder.itemView.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_openNoteFragment, bundle))
    }

    override fun getItemCount() = notes.size

    override fun onViewMoved(oldPosition: Int, newPosition: Int): Boolean {
        // Do nothing
        return false
    }

    override fun onViewSwiped(position: Int) {
        deletedFrom = position
        if (position < notes.size) {
            val note = notes[position]
            viewModel.deleteNote(note)
            Snackbar.make(fragment.view!!, "Note deleted successfully", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.WHITE)
                    .setAction("UNDO") {
                        viewModel.saveNote(note)
                    }
                    .show()
        } else
            notifyDataSetChanged()
    }

    override fun onViewIdle() {
        // Do nothing
    }

    private fun updateNotes(newList: List<Note>) {
        val size = notes.size
        notes.clear()
        notes.addAll(newList)
        if (size > newList.size) {
            notifyItemRemoved(deletedFrom)
        } else notifyDataSetChanged()
    }

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val title: TextView = mView.findViewById(R.id.item_note_title)
        val body: TextView = mView.findViewById(R.id.item_note_body)
        val edited: TextView = mView.findViewById(R.id.item_note_edited)
    }
}