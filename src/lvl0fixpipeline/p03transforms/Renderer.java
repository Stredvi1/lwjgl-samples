package lvl0fixpipeline.p03transforms;

import lvl0fixpipeline.global.AbstractRenderer;
import lwjglutils.OGLTexture2D;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.DoubleBuffer;

import static lvl0fixpipeline.global.GluUtils.gluLookAt;
import static lvl0fixpipeline.global.GluUtils.gluPerspective;
import static lvl0fixpipeline.global.GlutUtils.glutSolidCube;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Shows using 3D transformation and interaction in a scene
 *
 * @author PGRF FIM UHK
 * @version 3.1
 * @since 2020-01-20
 */
public class Renderer extends AbstractRenderer {
    private double dx, dy;
    private double ox, oy;
    private long oldmils;
    private long oldFPSmils;
    private double fps;

    private float uhel = 0;
    private int mode = 0;
    private float[] modelMatrix = new float[16];

    private boolean per = false, depth = true;
    private boolean mouseButton1 = false;

    private OGLTexture2D texture1, texture2;

    public Renderer() {
        super();

        /*used default glfwWindowSizeCallback see AbstractRenderer*/

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_RELEASE) {
                    //do nothing
                }
                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_P:
                            per = !per;
                            break;
                        case GLFW_KEY_D:
                            depth = !depth;
                            break;
                        case GLFW_KEY_M:
                            mode++;
                            break;
                    }
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);

                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    ox = x;
                    oy = y;
                }
            }

        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    dx = x - ox;
                    dy = y - oy;
                    ox = x;
                    oy = y;
                }
            }
        };

        glfwScrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                //do nothing
            }
        };
    }

    @Override
    public void init() {
        super.init();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        glEnable(GL_DEPTH_TEST);

        glFrontFace(GL_CCW);
        glPolygonMode(GL_FRONT, GL_FILL);
        glPolygonMode(GL_BACK, GL_LINE);
        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glMatrixMode(GL_MODELVIEW);

        glLoadIdentity();
        glGetFloatv(GL_MODELVIEW_MATRIX, modelMatrix);

        try {
            texture1 = new OGLTexture2D("textures/bricks.jpg");
            texture2 = new OGLTexture2D("textures/globe.jpg");


        } catch (Exception e) {
            e.printStackTrace();
        }

        OGLTexture2D.Viewer textureViewer;
        textureViewer = new OGLTexture2D.Viewer();
        glDisable(GL_TEXTURE_2D);


        // light source setting - diffuse component
        float[] light_dif = new float[]{1, 1, 1, 1};
        // light source setting - ambient component
        float[] light_amb = new float[]{1, 1, 1, 1};
        // light source setting - specular component
        float[] light_spec = new float[]{1, 1, 1, 1};

        glLightfv(GL_LIGHT0, GL_AMBIENT, light_amb);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, light_dif);
        glLightfv(GL_LIGHT0, GL_SPECULAR, light_spec);
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        // zapnuti nebo vypnuti viditelnosti
        if (depth)
            glEnable(GL_DEPTH_TEST);
        else
            glDisable(GL_DEPTH_TEST);

        // mazeme image buffer i z-buffer
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer


        mode = mode % 7;
        // rotace mazanim matice a zvetsovanim uhlu


        float[] light_position;

        // bod v prostoru
        light_position = new float[]{25f, 20f, 1f, 1f};

        glLightfv(GL_LIGHT0, GL_POSITION, light_position);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);



        float x, y, z;
        x = 5.0f;
        y = 5.0f;
        z = 10.0f;

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45, width / (float) height, 0.1f, 100.0f);

        // divame se do sceny z kladne osy x, osa z je svisla
        gluLookAt(50, 20, 15, 0, 0, 0, 0, 0, 1);


        glMatrixMode(GL_MODELVIEW);

        uhel++;
        glLoadIdentity();
        glPushMatrix();

        texture1.bind();
        glColor3f(0.3f, 0.3f, 0.3f);
        glutSolidCube(5);

        texture2.bind();
        glColor3f(0.6f, 0.2f, 0.8f);

        glTranslatef(20, 0, 0);
        glutSolidCube(4);


//
//
//        texture2D.bind();
//
//        glutSolidCube(5);
//        //glLoadIdentity();
//
//        glRotatef(uhel, 0, 1, 0);
//        glTranslatef(10, 0, 0);
//        glColor3f(0.3f, 0.2f, 0.8f);
//
//        glutSolidSphere(2, 20, 20);
//
//        glLoadIdentity();
//        glRotatef(uhel, 0, 0, 1);
//        glTranslatef(0, 10, 0);
//
//        glColor3f(0.8f, 0.2f, 0.3f);
//        glutSolidSphere(2, 20, 20);

        glPopMatrix();


        glBegin(GL_LINES);
        glColor3f(1f, 0f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(100f, 0f, 0f);
        glColor3f(0f, 1f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 100f, 0f);
        glColor3f(0f, 0f, 1f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 0f, 100f);
        glEnd();

        float[] color = {1.0f, 1.0f, 1.0f};
        glColor3fv(color);
        glDisable(GL_DEPTH_TEST);

        String text = this.getClass().getName() + ": [Mouse] [M]ode: " + mode + " ";
        if (per)
            text += "[P]ersp, ";
        else
            text += "[p]ersp, ";

        if (depth)
            text += "[D]epth ";
        else
            text += "[d]epth ";

        //create and draw text
        textRenderer.clear();
        textRenderer.addStr2D(3, 20, text);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
        textRenderer.draw();
    }

}
