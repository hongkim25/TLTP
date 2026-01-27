🥖 The Baker: AI-Driven Inventory Optimization System
A full-stack retail management platform featuring a "Dual-Core" predictive engine to minimize food waste while maximizing sales potential.

🏗️ Architecture Overview
The application follows a Hybrid Monolith architecture designed for reliability and ease of deployment. It leverages Java Spring Boot for robust transaction handling and Python for statistical modeling, bridging the two via a lightweight local interface.

코드 스니펫

graph TD
    User[Staff / POS] -->|HTTPS| Controller[Spring Boot Controller]
    Controller -->|Read/Write| DB[(H2 / PostgreSQL)]
    Controller -->|Request Prediction| Service[PredictionService.java]
    
    subgraph "Dual-Core AI Engine"
        Service -->|Load| JSON[Model Weights (ml_model.json)]
        Trainer[Python Script (Scikit-Learn)] -->|Train & Serialize| JSON
        CSV[Historical Data] -->|Feed| Trainer
    end
Tech Stack
Backend: Java 17, Spring Boot 3.2 (Web, JPA, Thymeleaf)

Data Science: Python 3.x, Pandas, Scikit-Learn (Linear Regression)

Database: H2 (Dev) / PostgreSQL (Prod)

Frontend: Server-Side Rendering (Thymeleaf) + TailwindCSS

DevOps: GitHub Actions (CI) -> Render (CD)

🧠 The "Dual-Core" Prediction Logic
Retail inventory management faces two conflicting challenges: Demand Volatility (Weather/Day effects) and Data Sparsity (Waste events are rare but costly). To solve this, The Baker employs a hybrid "Dual-Core" strategy:

Core 1: The Demand Model (Dynamic Regression)
Objective: Predict "How many can we sell tomorrow?"

Algorithm: Multivariate Linear Regression.

Features:

Day of Week (One-hot encoded)

Weather Condition (Sunny, Rain, Snow)

Temperature (Continuous variable)

Logic: The model calculates a Base Bias (Standard Demand) and applies weighted coefficients (e.g., Saturday Boost: +3.0, Rain Drop: -1.5) to generate a dynamic target.

Core 2: The Risk Model (Sparse Data Handling)
Objective: "What is the risk of over-production?"

Challenge: Waste data is sparse (zeros are common) and negatively skewed. A standard regression often fails to capture the magnitude of risk.

Solution: An "Active-Day" Risk Scoring system.

The system filters historical data to isolate only Active Shelf Days (days the product was actually produced).

It calculates a Waste Risk Score (Avg. Loss per Active Day) vs. Production Velocity (Avg. Made).

UI Impact: If the risk exceeds a threshold, the dashboard flashes a ⚠️ High Waste Risk alert, contextualizing the sales target.

🚀 Key Technical Features
1. "What-If" Simulation Engine
The dashboard allows staff to manually override weather parameters. This triggers a real-time recalculation of the prediction vectors without retraining the model, allowing for rapid scenario planning (e.g., "The forecast changed to Rain; how should we adjust prep?").

2. Automated Pipeline (CI/CD)
Continuous Integration: Every push triggers a build and test suite via GitHub Actions.

Model Retraining: The Python training script runs as part of the pipeline, ensuring the ml_model.json weights are always synchronized with the latest history.csv data before deployment.

3. Mathematical Transparency
Unlike "Black Box" AI tools, The Baker provides full explainability to the end-user. The UI breaks down the prediction into its components:

Prediction = Base (10) + Saturday (+2) + Rain (-1) = 11 Units