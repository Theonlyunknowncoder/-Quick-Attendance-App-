package com.example.quickattendanceapp.model

/**
 * A simple data class to represent a single student.
 * Using a data class automatically provides useful functions like equals(), hashCode(), and toString().
 *
 * @property rollNumber The unique roll number of the student.
 * @property name The full name of the student.
 * @property isPresent A flag to track the attendance status. Defaults to false (absent).
 */
data class Student(
    val rollNumber: String,
    val name: String,
    var isPresent: Boolean = false
)

