package com.dicoding.latihanmembuataplikasicatatandengansqlite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.latihanmembuataplikasicatatandengansqlite.R
import com.dicoding.latihanmembuataplikasicatatandengansqlite.databinding.ItemNoteBinding
import com.dicoding.latihanmembuataplikasicatatandengansqlite.entity.Note

// Adapter untuk menampilkan daftar catatan di RecyclerView
class NoteAdapter(private val onItemClickCallback: OnItemClickCallback) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // List untuk menampung catatan
    var listNotes = ArrayList<Note>()
        set(listNotes) {
            // Jika list baru tidak kosong, list lama dihapus dan diisi dengan yang baru
            if (listNotes.size > 0) {
                this.listNotes.clear()
            }
            this.listNotes.addAll(listNotes)
            // Memberitahu adapter bahwa data telah berubah agar UI diperbarui
            notifyDataSetChanged()
        }

    // Membuat ViewHolder baru yang berisi layout untuk item catatan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    // Menghubungkan data dengan tampilan di ViewHolder
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(listNotes[position])
    }

    // Mengembalikan jumlah item di listNotes
    override fun getItemCount(): Int = this.listNotes.size

    // ViewHolder untuk memegang tampilan item catatan
    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Menggunakan view binding untuk mengakses komponen UI dari item_note.xml
        private val binding = ItemNoteBinding.bind(itemView)

        // Menghubungkan data dari objek Note ke tampilan UI
        fun bind(note: Note) {
            binding.tvItemTitle.text = note.title
            binding.tvItemDate.text = note.date
            binding.tvItemDescription.text = note.description
            // Menangani event klik pada item catatan
            binding.cvItemNote.setOnClickListener {
                // Memanggil callback saat item diklik, meneruskan catatan yang dipilih dan posisinya
                onItemClickCallback.onItemClicked(note, adapterPosition)
            }
        }
    }

    // Menambahkan item catatan baru ke dalam listNotes dan memperbarui UI
    fun addItem(note: Note) {
        this.listNotes.add(note)
        notifyItemInserted(this.listNotes.size - 1)
    }

    // Memperbarui item catatan yang ada di posisi tertentu dan memperbarui UI
    fun updateItem(position: Int, note: Note) {
        this.listNotes[position] = note
        notifyItemChanged(position)
    }

    // Menghapus item catatan dari posisi tertentu, memperbarui UI setelah item dihapus
    fun removeItem(position: Int) {
        this.listNotes.removeAt(position)
        notifyItemRemoved(position)
        // Memperbarui rentang item yang ada di bawah posisi yang dihapus
        notifyItemRangeChanged(position, this.listNotes.size)
    }

    // Interface untuk menangani event klik pada item catatan
    interface OnItemClickCallback {
        // Metode untuk menerima catatan yang dipilih dan posisinya
        fun onItemClicked(selectedNote: Note?, position: Int?)
    }
}
