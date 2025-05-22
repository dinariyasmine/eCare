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
| Doctor Profiles, Listings, Feedback, User Profile, Home Page | `feature/backend/doctor-feedback_rating-clinic` |

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

## 3. Backend (Python - Django)

1. Open the `backend/` folder in your code editor (e.g., VS Code, PyCharm)

2. Create and activate a virtual environment (optional but recommended):
   ```sh
   python -m venv env
   source env/bin/activate  # On Windows: env\Scripts\activate
   ```

3. Install dependencies:
   ```sh
   pip install -r requirements.txt
   ```

4. Run database migrations:
   ```sh
   python manage.py migrate
   ```

5. Start the development server:
   ```sh
   python manage.py runserver
   ```


