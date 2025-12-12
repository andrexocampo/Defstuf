# DefStuf

A desktop application designed to optimize the learning and memorization of mathematical definitions for students and professionals. DefStuf combines efficient knowledge management with an automated spaced repetition system and progress tracking.

## Problem Statement

Studying mathematics requires memorizing a large number of definitions to begin speaking the language and working with it. However, traditional study methods have significant shortcomings that make this memorization process cumbersome:

- **Time-consuming transcription**: Significant time is wasted transcribing definitions manually
- **Lost notes**: Difficulty remembering where definitions were written and excessive time spent finding specific definitions
- **Incomplete review coverage**: Uncertainty about whether all definitions have been reviewed
- **Manual spaced repetition**: Adopting a systematic spaced repetition approach is labor-intensive and requires extensive manual organization
- **Lack of overview**: No centralized space to view all definitions from a specific area simultaneously

DefStuf addresses these challenges by providing an efficient way to store definitions, automatically schedule them in a review calendar, and present them using memorization techniques like spaced repetition.

**Target Audience**: Mathematics students and mathematicians.

## Project Objectives

### General Objective

Develop an application that optimizes the learning and memorization process of mathematical definitions for students and professionals, combining efficient knowledge management with an automated spaced repetition system and progress tracking.

### Specific Objectives

#### A. Centralized and Efficient Knowledge Management

**Goal**: Provide a single, organized, and fast-access digital repository for all user's mathematical definitions.

**Concrete Target**: Eliminate time wasted searching for definitions written in different places, enabling filtering and viewing all definitions from a specific area at a glance.

#### B. Fast and Frictionless Information Capture

**Goal**: Facilitate the creation of definition notes in the fastest and most fluid way possible.

**Concrete Target**: Significantly reduce transcription time through tools like "sequence mode" for creating multiple notes continuously and a screenshot tool for including images of formulas or diagrams directly.

#### C. Automated Review for Effective Memorization

**Goal**: Implement an intelligent spaced repetition system that automatically plans study sessions.

**Concrete Target**: Free users from manually planning their study calendar, ensuring each definition is reviewed at optimal intervals for long-term retention, with flexibility to adapt to specific deadlines such as midterm exams (3 weeks, 1 month).

#### D. Progress Tracking and Motivation through Data

**Goal**: Provide users with a clear and quantifiable view of their progress and study habits.

**Concrete Target**: Display statistics such as total study time, consistency (daily streaks), percentage of mastered definitions, and results from review sessions, to foster motivation and self-assessment.

## Features

### Core Functionality

- **User Authentication**: Secure login and registration system with password hashing
- **Area Management**: Organize definitions into custom knowledge areas
- **Note Creation**: Create definitions with rich text descriptions and multiple images
- **Source Organization**: Tag notes with sources for better organization and filtering
- **Screenshot Integration**: Capture and attach screenshots directly to notes
- **Bulk Note Creation**: Efficient creation of multiple definitions in sequence

### Study System

- **Spaced Repetition Algorithm**: Intelligent scheduling that adapts to your performance
- **Flexible Study Sessions**: Configure session parameters including time limits, card limits, and review order
- **Smart Note Classification**: Automatic categorization of new, pending, and reviewed notes
- **Real-time Progress Tracking**: Session progress with elapsed time, average time per definition, and rating distribution
- **Break Management**: Configurable breaks during study sessions
- **Keyboard Shortcuts**: Quick rating system (1-4 keys) for efficient review

### Study Modes

- **Standard Review**: Study new and pending notes scheduled for the day
- **Advanced Review**: Review all available notes regardless of schedule
- **Multiple Review Orders**: Random, chronological, or difficulty-based ordering

## Technology Stack

- **Frontend**: JavaFX 13
- **Backend**: Java 11
- **Database**: MySQL 8.2
- **Build Tool**: Maven
- **Security**: BCrypt for password hashing

## Project Structure

```
src/main/java/com/portfolio/defstuf/
├── controllers/        # JavaFX controllers for UI management
│   ├── auth/          # Authentication controllers
│   ├── area/          # Area management controllers
│   ├── note/          # Note creation and management
│   └── study/         # Study session controllers
├── models/            # Data models
├── repository/        # Database access layer
├── services/          # Business logic layer
│   ├── study/         # Spaced repetition algorithm
│   └── content/       # Content rendering utilities
└── config/            # Configuration classes
```

## Requirements

- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6+

## Installation

1. Clone the repository:
```bash
git clone https://github.com/andrexocampo/Defstuf.git
cd defstuf
```

2. Configure the database:
   - Create a MySQL database named `db_defstuf`
   - Update database credentials in `src/main/java/com/portfolio/defstuf/config/DatabaseConfig.java` if needed
   - The application will automatically create tables on first run

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn javafx:run
```

## Usage

### Getting Started

1. Launch the application
2. Register a new account or log in
3. Create an area (e.g., "Linear Algebra", "Real Analysis")
4. Add sources if desired (e.g., "Rudin Chapter 2", "Lecture Notes Week 3")
5. Create notes with definitions and optional images
6. Start a study session to begin reviewing

### Creating Notes

- Navigate to "Create Note" from the main menu
- Select an area (required)
- Optionally select one or more sources
- Enter the term/concept (title)
- Add a detailed definition/description
- Attach images using the screenshot tool or file selector
- Save the note

### Study Sessions

- Click "Start Review" on the main menu
- Configure your session:
  - Select an area to study
  - Choose specific sources (optional)
  - Set time limit (optional)
  - Set card limit (optional)
  - Choose review order (random/chronological/difficulty)
  - Configure break settings
- Review notes one at a time
- Rate your performance: Forgotten (1), Difficult (2), Good (3), Easy (4)
- The system will automatically schedule your next review based on your performance

## Screenshots

### Authentication
![Login Screen](docs/screenshots/login.png)

![Registration Screen](docs/screenshots/register.png)

### Main Interface
![Main Dashboard](docs/screenshots/main-dashboard.png)

### Note Management
![Manage Areas](docs/screenshots/manage-areas.png)

![Create Note](docs/screenshots/create-note.png)

### Study Sessions
![Study Session Configuration](docs/screenshots/study-config.png)

![Study Session - Question View](docs/screenshots/study-session-question.png)

![Study Session - Answer View](docs/screenshots/study-session-answer.png)

> **Note**: Screenshots will be displayed here once added to the `docs/screenshots/` directory. See `docs/screenshots/README.md` for instructions.

## Database Schema

The application uses a relational database with the following main entities:

- **Users**: User accounts and authentication
- **Areas**: Knowledge areas for organizing notes
- **Sources**: Reference sources for notes
- **Notes**: Definitions and concepts with descriptions
- **Note Images**: Images attached to notes
- **Study Sessions**: Study session records
- **Scheduled Reviews**: Spaced repetition scheduling
- **Questions**: User answers/ratings for notes

## Development

### Running Tests

```bash
mvn test
```

### Building for Distribution

```bash
mvn clean package
```

### Database Initialization

The database schema is automatically initialized on first run. Schema definitions are located in:
- `src/main/resources/database/create_schema.sql`

## Future Enhancements

- LaTeX/KaTeX formula rendering for mathematical expressions
- Study session statistics and analytics dashboard
- Export/import functionality for notes
- Daily streak tracking
- Mastery percentage calculations
- Cloud synchronization
- Exam deadline integration with adaptive scheduling

## License

[Specify your license here]

## Author

Andres Ocampo

---

Built with JavaFX and modern software engineering practices.
