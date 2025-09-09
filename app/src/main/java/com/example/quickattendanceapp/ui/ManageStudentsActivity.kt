package com.example.quickattendanceapp.ui

import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickattendanceapp.R
import com.example.quickattendanceapp.adapter.ManageStudentAdapter
import com.example.quickattendanceapp.data.StudentRepository
import com.example.quickattendanceapp.databinding.ActivityManageStudentsBinding
import com.example.quickattendanceapp.model.Student
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * This activity allows the teacher to manage the master list of students.
 * They can view, add, delete, and now import students from a CSV file.
 * All changes are saved permanently to the device's storage.
 */
class ManageStudentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageStudentsBinding
    private lateinit var studentRepository: StudentRepository
    private lateinit var manageStudentAdapter: ManageStudentAdapter
    private var studentList = mutableListOf<Student>()

    // NEW: ActivityResultLauncher to handle the result from the file picker.
    // When the user selects a file, this code will be executed.
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // A file was selected, now we process it.
            importStudentsFromCsv(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStudentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentRepository = StudentRepository(this)
        setupRecyclerView()
        setupClickListeners() // Renamed for clarity
        loadStudents()
    }

    /**
     * Sets up click listeners for all buttons on the screen.
     */
    private fun setupClickListeners() {
        // Listener for the "Add Student" floating action button
        binding.fabAddStudent.setOnClickListener {
            showAddStudentDialog()
        }

        // NEW: Listener for the "Import from CSV" button
        binding.btnImportCsv.setOnClickListener {
            launchFilePicker()
        }
    }

    /**
     * NEW: Launches the system file picker to select a CSV file.
     */
    private fun launchFilePicker() {
        // This tells the system we want to open a file and we're looking for CSV types.
        filePickerLauncher.launch("text/csv")
    }

    /**
     * NEW: Reads and parses a CSV file from the given URI.
     * Expects a simple CSV format: rollNumber,studentName
     * Example: 101,John Doe
     */
    private fun importStudentsFromCsv(uri: Uri) {
        val newStudents = mutableListOf<Student>()
        try {
            // Open the file using the URI provided by the file picker
            val inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?

            // Read the file line by line
            while (reader.readLine().also { line = it } != null) {
                val tokens = line?.split(",") // Split the line by the comma
                if (tokens?.size == 2) {
                    val rollNumber = tokens[0].trim()
                    val name = tokens[1].trim()
                    if (rollNumber.isNotEmpty() && name.isNotEmpty()) {
                        newStudents.add(Student(rollNumber, name))
                    }
                }
            }
            reader.close()

            if (newStudents.isNotEmpty()) {
                // Add all the new students to our existing list
                studentList.addAll(newStudents)
                // Save the combined list
                studentRepository.saveStudents(studentList)
                // Refresh the display
                manageStudentAdapter.updateList(studentList)
                Snackbar.make(binding.root, "${newStudents.size} students imported successfully!", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(binding.root, "No valid students found in the file.", Snackbar.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(binding.root, "Error reading file: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        manageStudentAdapter = ManageStudentAdapter(studentList) { studentToDelete ->
            deleteStudent(studentToDelete)
        }
        binding.rvManageStudents.apply {
            layoutManager = LinearLayoutManager(this@ManageStudentsActivity)
            adapter = manageStudentAdapter
        }
    }

    private fun loadStudents() {
        studentList = studentRepository.loadStudents().toMutableList()
        manageStudentAdapter.updateList(studentList)
    }

    private fun showAddStudentDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_student, null)
        val etRollNumber = dialogView.findViewById<EditText>(R.id.etRollNumber)
        val etStudentName = dialogView.findViewById<EditText>(R.id.etStudentName)

        AlertDialog.Builder(this)
            .setTitle("Add New Student")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val rollNumber = etRollNumber.text.toString().trim()
                val name = etStudentName.text.toString().trim()

                if (rollNumber.isNotEmpty() && name.isNotEmpty()) {
                    val newStudent = Student(rollNumber, name)
                    addStudent(newStudent)
                } else {
                    Snackbar.make(binding.root, "Roll number and name cannot be empty.", Snackbar.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    private fun addStudent(student: Student) {
        studentList.add(student)
        studentRepository.saveStudents(studentList)
        manageStudentAdapter.updateList(studentList)
        Snackbar.make(binding.root, "Student Added: ${student.name}", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteStudent(student: Student) {
        studentList.remove(student)
        studentRepository.saveStudents(studentList)
        manageStudentAdapter.updateList(studentList)
        Snackbar.make(binding.root, "Student Deleted: ${student.name}", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                addStudent(student)
            }
            .show()
    }
}

