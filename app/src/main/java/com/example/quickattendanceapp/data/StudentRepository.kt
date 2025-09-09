package com.example.quickattendanceapp.data

import android.content.Context
import com.example.quickattendanceapp.model.Student
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Manages the persistence of the student list using SharedPreferences.
 * This class handles saving and loading the list to and from the device's storage.
 * All lists are automatically sorted by roll number.
 */
class StudentRepository(context: Context) {

    // SharedPreferences is a simple way to store small amounts of private data.
    private val sharedPreferences = context.getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // A custom comparator to sort students by roll number. It tries to sort numerically
    // first, and falls back to a string comparison for non-numeric roll numbers.
    private val rollNumberComparator = compareBy<Student> {
        it.rollNumber.toIntOrNull() ?: Int.MAX_VALUE
    }.thenBy { it.rollNumber }

    /**
     * Saves the provided list of students to SharedPreferences.
     * The list is first sorted by roll number before being saved.
     *
     * @param studentList The list of students to save.
     */
    fun saveStudents(studentList: List<Student>) {
        // Sort the list before saving
        val sortedList = studentList.sortedWith(rollNumberComparator)
        val json = gson.toJson(sortedList) // Convert the list to a JSON string
        sharedPreferences.edit().putString("student_list", json).apply()
    }

    /**
     * Loads the list of students from SharedPreferences.
     * The returned list is guaranteed to be sorted by roll number.
     *
     * @return A sorted list of students. Returns an empty list if none is saved.
     */
    fun loadStudents(): List<Student> {
        val json = sharedPreferences.getString("student_list", null)
        return if (json != null) {
            val type = object : TypeToken<List<Student>>() {}.type
            // The list is already sorted when loaded, but we sort again just to be safe.
            gson.fromJson<List<Student>>(json, type).sortedWith(rollNumberComparator)
        } else {
            emptyList()
        }
    }
}

