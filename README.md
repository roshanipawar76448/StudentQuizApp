# Student Quiz App

A Java desktop application that runs timed multiple-choice and
true/false quizzes, tracks scores, and shows a leaderboard.
Built as a portfolio project for Java internship applications.

## Features
- Multiple choice and True/False questions loaded from XML
- Live countdown timer per question using multithreading
- Instant answer feedback with correct/wrong highlighting
- Final score screen with grade from A+ to F
- Persistent leaderboard saved to XML across sessions
- Filter by category (Java, OOP) and difficulty
- Clean 4-screen navigation using CardLayout

## Tech Stack
| Technology | Purpose |
|---|---|
| Java 17 | Core language |
| Swing + CardLayout | Desktop GUI — 4 screens |
| Maven | Build and dependency management |
| Log4j2 | Logging to console and file |
| DOM XML Parser | Load questions, save scores |
| javax.swing.Timer | Thread-safe countdown timer |

## OOP Concepts Demonstrated
| Concept | Where used |
|---|---|
| Abstraction | `Question` is abstract — cannot be instantiated |
| Inheritance | `MultipleChoiceQuestion` and `TrueFalseQuestion` extend `Question` |
| Polymorphism | `isCorrect()` behaves differently per question type |
| Encapsulation | All model fields private with getters and setters |
| Comparable | `QuizScore` implements `Comparable` for leaderboard sorting |

## Multithreading
Uses `javax.swing.Timer` for the countdown timer which fires
every 1000ms on the Event Dispatch Thread — keeping UI updates
thread-safe without freezing the interface. GUI is launched
via `SwingUtilities.invokeLater()` following the Swing EDT rule.

## How to Run
```bash
git clone https://github.com/YOUR_USERNAME/StudentQuizApp.git
cd StudentQuizApp
mvn clean package
java -jar target/StudentQuizApp-jar-with-dependencies.jar
```

## Project Structure
```
src/main/java/com/quiz/
├── Main.java
├── model/
│   ├── Question.java                  Abstract base class
│   ├── MultipleChoiceQuestion.java    Extends Question
│   ├── TrueFalseQuestion.java         Extends Question
│   └── QuizScore.java                 Comparable for sorting
├── service/
│   └── QuizService.java               Business logic, lambdas, streams
├── repository/
│   ├── QuestionRepository.java        DOM XML parser
│   └── ScoreRepository.java           XML read and write
├── controller/
│   └── QuizController.java            MVC bridge
├── ui/
│   ├── AppFrame.java                  CardLayout navigation
│   ├── WelcomePanel.java              Start screen
│   ├── QuizPanel.java                 Quiz + live timer
│   ├── ResultPanel.java               Score and grade
│   └── LeaderboardPanel.java          Top 10 table
└── exception/
    ├── QuizException.java
    ├── QuestionsNotFoundException.java
    └── InvalidAnswerException.java
```

## Author
**Your Name**
- GitHub:(https://github.com/roshanipawar76448)
- LinkedIn: [linkedin.com/in/YOUR_PROFILE](https://linkedin.com)
