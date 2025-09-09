package com.example.quickattendanceapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickattendanceapp.adapter.StudentAdapter
import com.example.quickattendanceapp.data.StudentRepository
import com.example.quickattendanceapp.databinding.ActivityAttendanceBinding
import com.example.quickattendanceapp.model.Student
import com.example.quickattendanceapp.utils.ReportGenerator
import com.google.android.material.snackbar.Snackbar

/**
 * This activity is where the teacher takes the daily attendance.
 * It displays a list of students with checkboxes. After marking, the teacher can generate
 * and share a formatted report.
 */
class AttendanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAttendanceBinding
    private lateinit var studentRepository: StudentRepository
    private lateinit var studentAdapter: StudentAdapter
    private var studentList = listOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository
        studentRepository = StudentRepository(this)

        // Set up the RecyclerView and the report generation button
        setupRecyclerView()
        setupReportButton()

        // Load the student list to populate the screen
        loadStudents()
    }

    /**
     * Configures the RecyclerView with its layout manager and adapter.
     */
    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(emptyList()) // Start with an empty list
        binding.rvAttendance.apply {
            layoutManager = LinearLayoutManager(this@AttendanceActivity)
            adapter = studentAdapter
        }
    }

    /**
     * Sets a click listener on the "Generate Report" button.
     */
    private fun setupReportButton() {
        binding.btnGenerateReport.setOnClickListener {
            // Get the latest state of the student list from the adapter
            val finalList = studentAdapter.getStudentList()
            // Get the period text from the EditText
            val period = binding.etPeriod.text.toString().trim()

            if (finalList.isNotEmpty()) {
                // Generate the report text, including the period
                val report = ReportGenerator.generateReport(finalList, period)
                // Share the generated report
                shareReport(report)
            } else {
                Snackbar.make(binding.root, "No students to generate a report for.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Loads the student list from the repository. If the list is empty, it shows a message.
     * Otherwise, it updates the adapter to display the students.
     */
    private fun loadStudents() {
        studentList = studentRepository.loadStudents()
        if (studentList.isEmpty()) {
            Snackbar.make(binding.root, "Student list is empty. Please add students first.", Snackbar.LENGTH_LONG).show()
        } else {
            // Important: Reset the isPresent flag for each student for the new session
            studentList.forEach { it.isPresent = false }
            studentAdapter.updateList(studentList)
        }
    }

    /**
     * Creates and starts an Intent to share the report text via other apps.
     * @param report The formatted report string to be shared.
     */
    private fun shareReport(report: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, report)
            type = "text/plain"
        }
        // Shows the Android share sheet
        startActivity(Intent.createChooser(shareIntent, "Share Attendance Report Via"))
    }
}

