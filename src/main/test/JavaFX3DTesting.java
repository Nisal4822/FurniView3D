package main.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;

public class JavaFX3DTesting {
    public static void main(String[] args) {
        // Initialize JavaFX by creating a JFXPanel (required for Swing integration)
        new JFXPanel();

        // Create and show our test frame
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("JavaFX 3D Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Create a Swing panel to hold the JavaFX content
            JFXPanel jfxPanel = new JFXPanel();
            frame.getContentPane().add(jfxPanel, BorderLayout.CENTER);

            // Initialize JavaFX scene on the JavaFX thread
            Platform.runLater(() -> {
                initFX(jfxPanel);
            });

            frame.setVisible(true);
        });
    }

    private static void initFX(JFXPanel jfxPanel) {
        // Create a simple 3D scene with a cube
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);

        // Create a cube
        Box cube = new Box(100, 100, 100);

        // Add material and color to the cube
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BLUE);
        material.setSpecularColor(Color.LIGHTBLUE);
        cube.setMaterial(material);

        // Position the cube in the center
        cube.setTranslateX(400);
        cube.setTranslateY(300);
        cube.setTranslateZ(0);

        // Add rotation animation
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        cube.getTransforms().addAll(rotateX, rotateY);

        // Add the cube to the scene
        root.getChildren().add(cube);

        // Set the scene to the panel
        jfxPanel.setScene(scene);

        // Animation thread - simple rotation
        Thread animationThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(20);  // Approx 50 FPS
                    Platform.runLater(() -> {
                        rotateX.setAngle((rotateX.getAngle() + 0.5) % 360);
                        rotateY.setAngle((rotateY.getAngle() + 0.2) % 360);
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        animationThread.setDaemon(true);
        animationThread.start();
    }
}