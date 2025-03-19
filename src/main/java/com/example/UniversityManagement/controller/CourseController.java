package com.example.UniversityManagement.controller;

import com.example.UniversityManagement.model.Course;
import com.example.UniversityManagement.model.Professor;
import com.example.UniversityManagement.service.CourseService;
import com.example.UniversityManagement.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(course -> new ResponseEntity<>(course, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Course>> getCoursesByProfessorId(@PathVariable Long professorId) {
        if (!professorService.getProfessorById(professorId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Course> courses = courseService.getCoursesByProfessorId(professorId);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PostMapping("/professor/{professorId}")
    public ResponseEntity<Course> createCourse(@PathVariable Long professorId, @RequestBody Course course) {
        return professorService.getProfessorById(professorId)
                .map(professor -> {
                    course.setProfessor(professor);
                    return new ResponseEntity<>(courseService.saveCourse(course), HttpStatus.CREATED);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/professor/{professorId}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @PathVariable Long professorId, @RequestBody Course course) {
        if (!courseService.getCourseById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return professorService.getProfessorById(professorId)
                .map(professor -> {
                    course.setId(id);
                    course.setProfessor(professor);
                    return new ResponseEntity<>(courseService.saveCourse(course), HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}