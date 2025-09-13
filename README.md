# Partnership Management Module

## 📌 Overview
The **Partnership Management Module** is part of an e-learning platform designed to manage and track B2B partnerships efficiently. It provides tools for both **partners** and **administrators** to collaborate, evaluate, and strengthen engagement. The module also integrates AI-driven features such as **partnership eligibility prediction** and automated **web scraping** for data collection.

## 👥 Actors
### Partner
- Apply for or propose partnerships.
- Contribute to events (host, suggest courses).
- Evaluate and provide feedback on partnerships.

### Admin
- Manage partnerships (add, track, modify, accept, terminate).
- Search and analyze potential partners.
- Generate contracts (automatic).
- Evaluate partnerships with predefined KPIs.
- Track partner engagement through hosted events.

## ✨ Features
- Partnership lifecycle management (creation → tracking → termination).
- Automated contract generation.
- Proposal and event management.
- KPI-based evaluation and scoring.
- Feedback system with partner input.
- Dashboard for tracking engagement and success rates.
- **Web scraping** using Python Selenium to collect potential partner information.
- **Partnership eligibility prediction** using SVC (Support Vector Classifier) to assess partner suitability.

## 🛠️ Tech Stack
- **Backend:** Spring Boot (Java)
- **Frontend:** Angular 17+ (Standalone API, app.routes.ts for routing)
- **Database:** MySQL
- **AI/ML:** Python (SVC for prediction)
- **Web Scraping:** Python Selenium
- **APIs:** RESTful endpoints for partner/admin interactions
