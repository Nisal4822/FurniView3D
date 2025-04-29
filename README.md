# FurniView3D - Furniture Design Tool

FurniView3D is an interactive desktop application that enables users to design furniture layouts for interior spaces in both 2D and 3D views.

## Setting Up FurniView3D in IntelliJ IDEA

### Required Software
- JDK 11 or higher
- IntelliJ IDEA (Community or Ultimate Edition)
- JavaFX SDK 15 or higher (with JavaFX 3D support)

### Step 1: Download JavaFX SDK
1. Go to [Gluon's JavaFX Download Page](https://gluonhq.com/products/javafx/)
2. Select your operating system
3. Choose JavaFX SDK (not JavaFX jmods)
4. Download the latest version (15.0.1 or newer)
5. Extract the zip file to a location on your computer (note this path for later)

### Step 2: Open the Project in IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select "Open" and navigate to the FurniView3D project folder
3. Click "OK" to open the project

### Step 3: Add JavaFX Library to Project
1. Go to **File → Project Structure**
2. Select "Libraries" from the left panel
3. Click the + button and select "Java"
4. Navigate to your extracted JavaFX SDK folder
5. Select the "lib" folder inside the JavaFX SDK directory
6. Click "OK" to add the library
7. Name the library "JavaFX" and click "OK"
8. Click "Apply" and then "OK" to save changes

### Step 4: Configure VM Options for JavaFX
1. Go to **Run → Edit Configurations**
2. Select the "Application" configuration for the FurniView3D main class
3. In the VM options field, add the following (replace the path with your actual JavaFX lib path):

   **Windows:**
   ```
   --module-path "C:\path\to\javafx-sdk\lib" --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.web --enable-native-access=javafx.graphics
   ```

   **macOS:**
   ```
   --module-path "/path/to/javafx-sdk/lib" --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.web
   ```

   **Linux:**
   ```
   --module-path "/path/to/javafx-sdk/lib" --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.web
   ```

4. Make sure the main class is set to: `main.java.com.furniview3d.Main`
5. Click "Apply" and "OK"

### Step 5: Verify 3D Support is Enabled
To ensure JavaFX 3D works properly:

1. Verify that the VM options include proper module path and all required modules
2. If you encounter 3D-specific issues, try adding these specific 3D-related modules:
   ```
   --add-modules=javafx.controls,javafx.fxml,javafx.graphics,javafx.swing,javafx.web,javafx.media
   ```

### Step 6: Run the Application
1. Go to **Run → Run 'Main'** or click the green run button
2. The application should start with the login screen

## Troubleshooting Common Issues

### "Error: JavaFX runtime components are missing"
- Double-check your VM options and ensure the path to the JavaFX SDK lib folder is correct
- Verify that all required modules are included in the --add-modules parameter

### 3D Rendering Issues
- Update your graphics card drivers to the latest version
- Ensure your system meets the minimum requirements for JavaFX 3D

### "Class not found" or "Cannot resolve symbol" errors
- Rebuild the project: **Build → Rebuild Project**
- Invalidate caches and restart: **File → Invalidate Caches / Restart**

### Class path issues
- Ensure the project structure has the correct module dependencies
- Check that the JavaFX SDK is properly added to the project libraries
