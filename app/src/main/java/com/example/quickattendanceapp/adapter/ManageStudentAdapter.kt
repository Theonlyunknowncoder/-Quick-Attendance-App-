package com.example.quickattendanceapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quickattendanceapp.databinding.ItemManageStudentBinding
import com.example.quickattendanceapp.model.Student

/**
 * Adapter for the RecyclerView in ManageStudentsActivity.
 * It displays a list of students with a delete button for each.
 *
 * @property studentList The list of students to be displayed.
 * @property onDeleteClicked A lambda function that will be invoked when a delete button is clicked.
 * It passes the student to be deleted back to the activity.
 */
class ManageStudentAdapter(
    private var studentList: MutableList<Student>,
    private val onDeleteClicked: (Student) -> Unit
) : RecyclerView.Adapter<ManageStudentAdapter.ViewHolder>() {

    /**
     * ViewHolder for a single student item in the management list.
     */
    inner class ViewHolder(val binding: ItemManageStudentBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Inflates the item_manage_student.xml layout for each row.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemManageStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Returns the total number of students in the list.
     */
    override fun getItemCount(): Int {
        return studentList.size
    }

    /**
     * Binds the data from a Student object to the views in the ViewHolder.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = studentList[position]
        holder.binding.tvStudentInfo.text = "${student.rollNumber}. ${student.name}"

        // Set a click listener on the delete button.
        // When clicked, it invokes the onDeleteClicked lambda with the current student.
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClicked(student)
        }
    }

    /**
     * A helper function to update the list of students in the adapter and refresh the display.
     *
     * @param newList The new list of students.
     */
    fun updateList(newList: MutableList<Student>) {
        studentList = newList
        notifyDataSetChanged() // This tells the RecyclerView to redraw itself.
    }
}

