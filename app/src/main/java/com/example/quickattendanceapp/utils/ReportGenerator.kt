package com.example.quickattendanceapp.utils

import com.example.quickattendanceapp.model.Student
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A utility object to generate a formatted attendance report string.
 */
object ReportGenerator {

    /**
     * Generates a formatted string for the attendance report.
     *
     * @param studentList The final list of students with their attendance marked.
     * @param period An optional string for the lecture/period name.
     * @return A formatted string ready to be shared.
     */
    fun generateReport(studentList: List<Student>, period: String): String {
        val totalStudents = studentList.size
        val presentStudentsCount = studentList.count { it.isPresent }
        val absentStudentsCount = totalStudents - presentStudentsCount
        val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))

        // Use a StringBuilder for efficient string creation
        val reportBuilder = StringBuilder()
        reportBuilder.append("Attendance Report\n")
        reportBuilder.append("====================\n")
        reportBuilder.append("Date: $currentDate\n")

        // Add the period to the report if the teacher entered one
        if (period.isNotEmpty()) {
            reportBuilder.append("Lecture: $period\n")
        }

        reportBuilder.append("Total Students: $totalStudents\n")
        reportBuilder.append("Present: $presentStudentsCount\n")
        reportBuilder.append("Absent: $absentStudentsCount\n")
        reportBuilder.append("====================\n\n")

        // Filter the list to get only the present students and format their roll numbers
        val presentRollNumbers = studentList
            .filter { it.isPresent }
            .joinToString(separator = ", ") { it.rollNumber }

        if (presentRollNumbers.isNotEmpty()) {
            reportBuilder.append("Present Roll Numbers:\n$presentRollNumbers")
        } else {
            reportBuilder.append("No students were present.")
        }

        return reportBuilder.toString()
    }
}

