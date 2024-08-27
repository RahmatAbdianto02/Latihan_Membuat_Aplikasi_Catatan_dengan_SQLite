package com.dicoding.latihanmembuataplikasicatatandengansqlite

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.latihanmembuataplikasicatatandengansqlite.adapter.NoteAdapter
import com.dicoding.latihanmembuataplikasicatatandengansqlite.databinding.ActivityMainBinding
import com.dicoding.latihanmembuataplikasicatatandengansqlite.db.NoteHelper
import com.dicoding.latihanmembuataplikasicatatandengansqlite.entity.Note
import com.dicoding.latihanmembuataplikasicatatandengansqlite.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteAdapter

    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null){
            // akan panggil jika request codenya ADD

            when(result.resultCode){
                NoteAddUpdateActivity.RESULT_ADD -> {
                    val note = result.data?.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE) as Note
                    adapter.addItem(note)
                    binding.rvNotes.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("satu item berhasil di hapus")
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "notes"

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.setHasFixedSize(true)

        adapter = NoteAdapter(object: NoteAdapter.OnItemClickCallback{
            override fun onItemClicked(selectedNote: Note?, position: Int?) {
                val intent = Intent(this@MainActivity,NoteAddUpdateActivity::class.java)
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION,position)
                resultLauncher.launch(intent)
            }
        })
        binding.rvNotes.adapter = adapter

        binding.fabAdd.setOnClickListener{
            val intent = Intent(this@MainActivity,NoteAddUpdateActivity::class.java)
            resultLauncher.launch(intent)
        }

        loadNotesAsync()

        if (savedInstanceState == null){
            // proses ambil data
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null){
                adapter.listNotes = list
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
       outState.putParcelableArrayList(EXTRA_STATE,adapter.listNotes)
    }


    private fun showSnackbarMessage(message:String){
        Snackbar.make(binding.rvNotes,message,Snackbar.LENGTH_SHORT).show()
    }

    private fun loadNotesAsync(){
        lifecycleScope.launch {
            binding.root.visibility = View.VISIBLE
            val noteHelper = NoteHelper.getInstance(applicationContext)
            noteHelper.open()
            val deferredNotes = async(Dispatchers.IO){
                val cursor = noteHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            binding.progressbar.visibility = View.INVISIBLE
            val notes = deferredNotes.await()
            if (notes.size > 0){
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("tidak ada data saat ini")
            }
            noteHelper.close()
        }
    }
}