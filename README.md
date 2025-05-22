<p align="center">
  <img src="Cover.jpg" alt="eCare" width="600"/>
</p>

<h1 align="center">eCare - Book, Track, Heal – Anytime, Anywhere!</h1>

eCare is a mobile application that simplifies doctor appointment booking. Patients can find doctors, schedule appointments, and manage prescriptions, while doctors can list availability and handle appointments efficiently.  

⚠️ **Note:** Feature development is currently happening in separate branches and **not merged into `main` yet**. Please refer to the section below for relevant branches.

---

## 🌿 Active Feature Branches

| Feature                              | Branch Name                              |
|--------------------------------------|------------------------------------------|
| Appointment Management               | `appointments_V3`                        |
| Prescription Handling                | `prescription`                           |
| Notification System                  | `notifications`                          |
| Authentication (Backend)            | `backend-aisha`                          |
| Authentication (Frontend)           | `v4-aisha`                               |
| QR Code Check-in                     | `qr_code`                                |
| Doctor Profiles, Listings, Feedback | `feature/backend/doctor-feedback_rating-clinic` |

To test or work with a specific feature, switch to the appropriate branch using: git checkout <branch-name>


## 🚀 Features  
- **User Authentication** (Email & Google Sign-in)  
- **Doctor Listings & Search**  
- **Appointment Booking & Management**  
- **QR Code Check-in**  
- **Prescription Management**  
- **Real-time Notifications & Tracking**  
- **Offline Mode with Data Synchronization**  

## 🎨 UI/UX Design  
Check out the Figma design: [Figma Link](https://www.figma.com/design/VQXfI3RbxvGorogFeCJw8O/UI?node-id=7-7222&t=K7sVyZju8s8OBmTx-1)  

## Database Conception
Check out the conception: [Lucidchart Diagram](https://lucid.app/lucidchart/0b9fab06-13f0-49ed-9a08-8efb62a80eac/edit?viewport_loc=-813%2C-895%2C1700%2C819%2C94_1GOOiqmvM&invitationId=inv_e7d6629c-eb49-4b81-b8f5-670b7384557a)


## 🛠️ Tech Stack  
- **Frontend (Mobile App)**: Kotlin (Jetpack Compose)  
- **Backend**: Ktor (Kotlin-based server)  
- **Database**: PostgreSQL 

## 📂 Setup & Installation  

### **1. Clone the repository**  
   ```sh
   git clone https://github.com/your-username/eCare.git
   cd eCare
   ```  

### **2. Mobile App (Kotlin - Jetpack Compose)**  
1. Open the `mobile/` folder in **Android Studio**  
2. Build & Run the app on an emulator or physical device  

### **3. Backend (Kotlin - Ktor)**  
1. Open the `backend/` folder in **IntelliJ IDEA**  
2. Install dependencies:  
   ```sh
   ./gradlew build
   ```  
3. Start the server:  
   ```sh
   ./gradlew run
   ```  

## 🏗️ Development Guidelines

### 🏷️ Branch Naming Convention
**Format for Branch Names:**
- Use the exact task/ticket name from your backlog
- Convert to lowercase
- Replace spaces with hyphens
- Prefix with category

**Branch Category Prefixes:**
- `feature/` - For new features
- `bugfix/` - For bug fixes
- `refactor/` - For code refactoring
- `hotfix/` - For urgent production fixes

**Examples:**
- `feature/user-authentication-flow`
- `bugfix/login-page-crash`
- `refactor/optimize-doctor-search`
- `hotfix/critical-firebase-authentication-issue`

### 🧩 Code Style and Naming Conventions
**Kotlin Naming Conventions:**

1. **Classes and Interfaces**
   - PascalCase
   - Descriptive and clear
   - Examples:
     ```kotlin
     class UserProfile
     interface AppointmentRepository
     ```

2. **Functions**
   - camelCase
   - Verb-first naming
   - Describe the action
   - Examples:
     ```kotlin
     fun validateUserCredentials()
     fun fetchDoctorAvailability()
     ```

3. **Variables**
   - camelCase
   - Descriptive and meaningful
   - Avoid single-letter variables
   - Examples:
     ```kotlin
     val userEmail: String
     val totalAppointments: Int
     ```

4. **Constants**
   - UPPER_SNAKE_CASE
   - Examples:
     ```kotlin
     const val MAX_APPOINTMENT_DURATION = 60
     const val API_BASE_URL = "https://ecare.com/api"
     ```

5. **Composable Functions**
   - PascalCase
   - Describe the UI component
   - Examples:
     ```kotlin
     @Composable
     fun UserProfileScreen()
     fun DoctorAppointmentCard()
     ```

### 💬 Commenting Guidelines
**Code Comments:**
1. **Purpose of Comments**
   - Explain *why*, not *what*
   - Clarify complex logic
   - Provide context for non-obvious implementations

2. **Comment Styles**
   ```kotlin
   // Single-line comment for brief explanations

   /**
    * Multi-line documentation comment
    * Use for function and class descriptions
    * @param email User's email address
    * @return Boolean indicating login success
    */
   fun loginUser(email: String, password: String): Boolean {
       // Inline comment for specific code block explanation
       val isValidEmail = validateEmail(email)
   }
   ```

3. **When to Comment**
   - Complex algorithms
   - Workarounds
   - Performance-critical sections
   - Business logic nuances

### 🎨 Design Consistency
**Color and Typography Usage:**
- Always import and use predefined colors and typography from your design system
- Never hardcode colors or text styles
- Use Jetpack Compose's custom theme

**Example:**
```kotlin
@Composable
fun UserProfileScreen() {
    Text(
        text = "Profile",
        color = AppColors.primary, // Use predefined color
        style = AppTypography.heading // Use predefined typography
    )
}
```

### 📝 Commit Message Guidelines
**Commit Message Format:**
```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Commit Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Formatting, missing semicolons
- `refactor`: Code restructuring without behavior change
- `test`: Adding or modifying tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(authentication): Implement Google Sign-In

- Add Google OAuth login button
- Integrate Firebase Authentication
- Handle login state management

Closes #123
```

```
fix(doctor-search): Resolve filter performance issue

- Optimize search algorithm
- Reduce unnecessary re-compositions
- Improve search result loading time
```

### 🚨 Best Practices
- Keep commits small and focused
- One logical change per commit
- Write clear, descriptive commit messages
- Use present tense in commit descriptions

### 📋 Code Review Checklist
- Follows naming conventions
- Uses predefined colors and typography
- Meaningful comments
- Clear commit messages
- Passes all local tests

## 📌 Contributing  
We welcome contributions! Feel free to submit pull requests or report issues.  

## 📄 License  
This project is licensed under the MIT License.
