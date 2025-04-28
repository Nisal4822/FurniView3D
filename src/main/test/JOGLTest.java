//package main.test;
//
//import javax.swing.*;
//import java.awt.*;
//import com.jogamp.opengl.GL;
//import com.jogamp.opengl.GL2;
//import com.jogamp.opengl.GLAutoDrawable;
//import com.jogamp.opengl.GLCapabilities;
//import com.jogamp.opengl.GLEventListener;
//import com.jogamp.opengl.GLProfile;
//import com.jogamp.opengl.awt.GLCanvas;
//import com.jogamp.opengl.util.Animator;
//
//public class JOGLTest extends JFrame {
//
//    static {
//        // Force using the software renderer, which should work on any system
//        System.setProperty("jogl.disable.openglcore", "true");
//    }
//
//    public JOGLTest() {
//        super("Simple JOGL Test");
//
//        try {
//            // Request the most compatible profile
//            GLProfile glp = GLProfile.getMaxFixedFunc(true);
//            GLCapabilities caps = new GLCapabilities(glp);
//
//            // Try to use software rendering if hardware acceleration fails
//            caps.setHardwareAccelerated(false);
//
//            GLCanvas canvas = new GLCanvas(caps);
//            canvas.addGLEventListener(new SimpleListener());
//
//            getContentPane().add(canvas, BorderLayout.CENTER);
//
//            setSize(640, 480);
//            setLocationRelativeTo(null);
//            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            setVisible(true);
//
//            Animator animator = new Animator(canvas);
//            animator.start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null,
//                    "Error initializing OpenGL: " + e.getMessage(),
//                    "OpenGL Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private static class SimpleListener implements GLEventListener {
//        @Override
//        public void init(GLAutoDrawable drawable) {
//            System.out.println("OpenGL Initialization Complete!");
//            GL2 gl = drawable.getGL().getGL2();
//            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        }
//
//        @Override
//        public void dispose(GLAutoDrawable drawable) {
//            // Cleanup resources
//        }
//
//        @Override
//        public void display(GLAutoDrawable drawable) {
//            GL2 gl = drawable.getGL().getGL2();
//            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
//
//            // Draw a simple triangle
//            gl.glBegin(GL2.GL_TRIANGLES);
//
//            gl.glColor3f(1.0f, 0.0f, 0.0f);
//            gl.glVertex2f(-0.5f, -0.5f);
//
//            gl.glColor3f(0.0f, 1.0f, 0.0f);
//            gl.glVertex2f(0.5f, -0.5f);
//
//            gl.glColor3f(0.0f, 0.0f, 1.0f);
//            gl.glVertex2f(0.0f, 0.5f);
//
//            gl.glEnd();
//        }
//
//        @Override
//        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
//            GL2 gl = drawable.getGL().getGL2();
//            gl.glViewport(0, 0, width, height);
//        }
//    }
//
//
//}