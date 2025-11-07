package com.mavpal.service;

import com.mavpal.entity.Flashcard;
import com.mavpal.entity.QuizQuestion;
import com.mavpal.entity.Set;
import com.mavpal.repository.FlashcardRepository;
import com.mavpal.repository.QuizQuestionRepository;
import com.mavpal.repository.SetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DefaultSetService {

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Transactional
    public void createDefaultSets(Integer userId) {
        // Check if default sets already exist to prevent duplicates
        List<Set> existingSets = setRepository.findByCreatedBy(userId);
        boolean hasFlashcardSet = existingSets.stream()
                .anyMatch(s -> "Computer Science Fundamentals".equals(s.getTitle()) && "flashcard".equals(s.getType()));
        boolean hasQuizSet = existingSets.stream()
                .anyMatch(s -> "Computer Science Quiz".equals(s.getTitle()) && "quiz".equals(s.getType()));
        
        Set flashcardSet;
        if (hasFlashcardSet) {
            // Use existing set
            flashcardSet = existingSets.stream()
                    .filter(s -> "Computer Science Fundamentals".equals(s.getTitle()) && "flashcard".equals(s.getType()))
                    .findFirst()
                    .orElse(null);
        } else {
            // Create Computer Science Flashcard Set
            flashcardSet = new Set();
            flashcardSet.setTitle("Computer Science Fundamentals");
            flashcardSet.setDescription("Essential computer science concepts and terminology");
            flashcardSet.setSubject("Computer Science");
            flashcardSet.setType("flashcard");
            flashcardSet.setVisibility("public");
            flashcardSet.setCreatedBy(userId);
            flashcardSet.setCreatedAt(LocalDateTime.now().toString());
            flashcardSet = setRepository.save(flashcardSet);
        }

        // Add flashcards - Computer Science Fundamentals
        String[][] flashcards = {
            {"Binary Search", "O(log n)", "A search algorithm that finds the position of a target value within a sorted array by repeatedly dividing the search interval in half"},
            {"API", "Application Programming Interface", "A set of protocols and tools for building software applications that allow different applications to communicate with each other"},
            {"Stack vs Queue", "Stack: LIFO (Last In First Out), Queue: FIFO (First In First Out)", "Stack is like a stack of plates (last one added is first removed), Queue is like a line of people (first one in is first one out)"},
            {"Time Complexity", "A measure of the amount of time an algorithm takes to run as a function of the input size", "Common notations: O(1) constant, O(log n) logarithmic, O(n) linear, O(n²) quadratic"},
            {"Recursion", "A programming technique where a function calls itself to solve a problem", "Must have a base case to prevent infinite loops"}
        };

        // Only add flashcards if this is a new set
        if (!hasFlashcardSet) {
            for (int i = 0; i < flashcards.length; i++) {
                Flashcard card = new Flashcard();
                card.setSetId(flashcardSet.getId());
                card.setQuestion(flashcards[i][0]);
                card.setAnswer(flashcards[i][1]);
                card.setHint(flashcards[i][2]);
                card.setOrder(i);
                flashcardRepository.save(card);
            }
        }

        // Create or get Computer Science Quiz Set
        Set quizSet;
        if (hasQuizSet) {
            // Use existing set
            quizSet = existingSets.stream()
                    .filter(s -> "Computer Science Quiz".equals(s.getTitle()) && "quiz".equals(s.getType()))
                    .findFirst()
                    .orElse(null);
        } else {
            // Create Computer Science Quiz Set
            quizSet = new Set();
            quizSet.setTitle("Computer Science Quiz");
            quizSet.setDescription("Test your knowledge of computer science fundamentals");
            quizSet.setSubject("Computer Science");
            quizSet.setType("quiz");
            quizSet.setVisibility("public");
            quizSet.setCreatedBy(userId);
            quizSet.setCreatedAt(LocalDateTime.now().toString());
            quizSet = setRepository.save(quizSet);
        }

        // Only add quiz questions if this is a new set
        if (!hasQuizSet) {
            // Add quiz questions - Computer Science Quiz
            // Question 1: What is the time complexity of quicksort in the average case?
            QuizQuestion q1 = new QuizQuestion();
            q1.setSetId(quizSet.getId());
            q1.setType("mcq");
            q1.setQuestionText("What is the time complexity of quicksort in the average case?");
            q1.setChoices("[\"O(n)\", \"O(n log n)\", \"O(n²)\", \"O(log n)\"]");
            q1.setCorrectIndex(1);
            q1.setHint("Quicksort uses divide and conquer strategy");
            q1.setOrder(0);
            quizQuestionRepository.save(q1);

            // Question 2: What does HTTP stand for?
            QuizQuestion q2 = new QuizQuestion();
            q2.setSetId(quizSet.getId());
            q2.setType("mcq");
            q2.setQuestionText("What does HTTP stand for?");
            q2.setChoices("[\"HyperText Transfer Protocol\", \"High Transfer Text Protocol\", \"HyperText Transmission Protocol\", \"High Transfer Transmission Protocol\"]");
            q2.setCorrectIndex(0);
            q2.setHint("It's the protocol used for web communication");
            q2.setOrder(1);
            quizQuestionRepository.save(q2);

            // Question 3: What is the main purpose of a database index?
            QuizQuestion q3 = new QuizQuestion();
            q3.setSetId(quizSet.getId());
            q3.setType("mcq");
            q3.setQuestionText("What is the main purpose of a database index?");
            q3.setChoices("[\"To store data\", \"To speed up data retrieval\", \"To encrypt data\", \"To backup data\"]");
            q3.setCorrectIndex(1);
            q3.setHint("Indexes help find data faster");
            q3.setOrder(2);
            quizQuestionRepository.save(q3);
        }
    }
}

