package com.example.quickattendanceapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quickattendanceapp.R
import com.example.quickattendanceapp.model.Student

/**
 * This adapter is responsible for displaying the list of students on the Attendance screen.
 * It binds the student data to the views in the item_student.xml layout.
 *
 * @param studentList The initial list of students to display.
 */
class StudentAdapter(
    private var studentList: List<Student>
) : RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    /**
     * ViewHolder holds the views for a single item in the list. This avoids repeatedly
     * calling findViewById(), which is inefficient.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentInfo: TextView = itemView.findViewById(R.id.tvStudentInfo)
        val presentCheckBox: CheckBox = itemView.findViewById(R.id.cbPresent)
    }

    /**
     * Called when RecyclerView needs a new ViewHolder. This is where we inflate the
     * layout for a single list item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the ViewHolder to reflect the item at the
     * given position in the list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentStudent = studentList[position]

        // Combine roll number and name for a clean display
        holder.studentInfo.text = "${currentStudent.rollNumber}. ${currentStudent.name}"
        holder.presentCheckBox.isChecked = currentStudent.isPresent

        // Set a listener to update the student's 'isPresent' status when the checkbox is clicked.
        holder.presentCheckBox.setOnCheckedChangeListener { _, isChecked ->
            currentStudent.isPresent = isChecked
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return studentList.size
    }

    /**
     * [FIX] This method allows the Activity to get the final list of students with their updated
     * attendance status.
     * @return The current list of students.
     */
    fun getStudentList(): List<Student> {
        return studentList
    }

    /**
     * [FIX] This method allows the Activity to update the list of students shown in the RecyclerView.
     * It notifies the adapter that the data has changed, so the UI can be redrawn.
     * @param newList The new list of students to display.
     */
    fun updateList(newList: List<Student>) {
        studentList = newList
        notifyDataSetChanged() // This tells the RecyclerView to refresh itself
    }
}

