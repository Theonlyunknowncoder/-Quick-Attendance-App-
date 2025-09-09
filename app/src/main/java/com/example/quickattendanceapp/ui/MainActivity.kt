package com.example.quickattendanceapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quickattendanceapp.data.StudentRepository
import com.example.quickattendanceapp.databinding.ActivityMainBinding

/**
 * The main entry point of the application.
 * This screen provides navigation to the two main features: managing students and taking attendance.
 * It also displays a count of the total students currently in the master list.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var studentRepository: StudentRepository

    /**
     * Called when the activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository for data handling
        studentRepository = StudentRepository(this)

        // Set up click listeners for the navigation buttons
        setupClickListeners()
    }

    /**
     * Called when the activity is becoming visible to the user.
     * We update the student count here to ensure it's always current when the user returns
     * to this screen.
     */
    override fun onResume() {
        super.onResume()
        updateStudentCount()
    }

    /**
     * Configures the actions to be taken when the user clicks on the main buttons.
     */
    private fun setupClickListeners() {
        binding.btnManageStudents.setOnClickListener {
            // Create an Intent to start the ManageStudentsActivity
            val intent = Intent(this, ManageStudentsActivity::class.java)
            startActivity(intent)
        }

        binding.btnTakeAttendance.setOnClickListener {
            // Create an Intent to start the AttendanceActivity
            val intent = Intent(this, AttendanceActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Fetches the current list of students from the repository and updates the
     * TextView to display the total count.
     */
    private fun updateStudentCount() {
        val students = studentRepository.loadStudents()
        if (students.isEmpty()) {
            binding.tvStudentCount.text = "No students found. Add a list!"
        } else {
            binding.tvStudentCount.text = "Total Students: ${students.size}"
        }
    }
}

