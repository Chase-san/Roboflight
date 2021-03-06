/**
 * Copyright (c) 2013-2014 Robert Maupin
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 */
package org.csdgn.rf.gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.Beans;
import java.nio.FloatBuffer;

import org.csdgn.rf.BattleRunner;
import org.csdgn.rf.Engine;
import org.csdgn.rf.peer.BulletImpl;
import org.csdgn.rf.peer.MissileImpl;
import org.csdgn.rf.peer.RobotPeerImpl;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import roboflight.util.Rules;
import roboflight.util.Vector;

/**
 * This class is a mess and I know it. This uses old style open gl rendering. It
 * should be updated to use a scene graph, shaders and such.
 * 
 * @author Robert Maupin
 * 
 */
public class RenderDisplay extends Canvas {
	private static final long serialVersionUID = 6222790311368212989L;

	private static final float FOV = 70f;
	private static final float NEAR_CLIP = 0.1f;
	private static final float FAR_CLIP = 10f;

	private static final float AXIS_LENGTH = 0.2f;
	public static boolean DRAW_AXIS = true;
	public static boolean DRAW_GRID = true;
	public static boolean DRAW_ROBOT_LOCATORS = false;

	private int width = 600;
	private int height = 600;

	private boolean created = false;
	private boolean dispose = false;
	private boolean drag = false;
	private boolean zoom = false;
	private final Camera camera = new Camera();

	private Engine engine;

	private GLFontRenderer font;

	public RenderDisplay() {
		setPreferredSize(new Dimension(width, height));
		if(Beans.isDesignTime()) {
			setBackground(java.awt.Color.BLACK);
		} else {
			try {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.setParent(this);
			} catch(final LWJGLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Changes your transformation matrix into a billboard matrix. This requires
	 * that you use glTranslate to position your objects.
	 */
	private void alignToCamera() {
		FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
		glGetFloat(GL_MODELVIEW_MATRIX, matrix);

		float[] m = new float[16];
		matrix.clear();
		matrix.get(m);

		float d = (float) Math.sqrt(m[0] * m[0] + m[4] * m[4] + m[8] * m[8]);
		m[1] = m[2] = m[4] = m[6] = m[8] = m[9] = 0;
		m[0] = m[5] = m[10] = d;

		matrix.clear();
		matrix.put(m);

		matrix.clear();
		glLoadMatrix(matrix);
	}

	public void create() {
		if(!created) {
			try {
				Display.create();
				// SETUP DISPLAY
				setupDisplay(width, height);

				// WIREFRAME
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

				// SETUP RENDERING
				glShadeModel(GL_SMOOTH);
				glClearColor(0f, 0f, 0f, 0.0f); // Black Background
				glClearDepth(1f); // Depth Buffer Setup
				glDisable(GL_DEPTH_TEST);

				glDepthFunc(GL_LEQUAL);
				glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
				glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
				created = true;

				font = new GLFontRenderer(Font.decode("consolas 32"));
			} catch(final LWJGLException e) {
				e.printStackTrace();
			}
		}

		if(Display.wasResized()) {
			width = Display.getWidth();
			height = Display.getHeight();
			setupDisplay(width, height);
		}
	}

	public void dispose() {
		dispose = true;
	}

	private void doDispose() {
		if(created) {
			try {
				font.dispose();
				Display.destroy();
				created = false;
			} catch(final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void drawAllMissiles(BattleRunner battle) {
		// drawMissile
		for(MissileImpl ms : battle.getMissiles()) {
			Vector p = ms.getPosition().scale(1.0 / Rules.BATTLEFIELD_RADIUS);

			drawMissile((float) p.x, (float) p.y, (float) p.z);
		}
	}

	private void drawAllRobots(BattleRunner battle) {
		// TODO sort robots based on distance to camera
		// this is only required once we get robot colors in
		for(RobotPeerImpl rp : battle.getRobotPeers()) {
			if(!rp.isAlive()) {
				continue;
			}

			float[] cf = rp.getColor();
			glColor3f(cf[0], cf[1], cf[2]);

			Vector p = rp.getPosition().scale(1.0 / Rules.BATTLEFIELD_RADIUS);
			drawRobot((float) p.x, (float) p.y, (float) p.z);

			Vector v = rp.getVelocity().scale(1.0 / 100).add(p);
			Vector t = rp.getLastThrust().scale(-1.0 / 5).add(p);

			glBegin(GL_LINES);
			{
				if(DRAW_ROBOT_LOCATORS) {
					glColor3f(0.15f, 0.15f, 0.15f);

					// draw grey leads, these extend outside the sphere
					glVertex3d(-1, p.y, p.z);
					glVertex3d(1, p.y, p.z);

					glVertex3d(p.x, -1, p.z);
					glVertex3d(p.x, 1, p.z);

					glVertex3d(p.x, p.y, -1);
					glVertex3d(p.x, p.y, 1);

					// draw the internal leads, these are inside the sphere
					double xh = Math.sqrt(1 - p.x * p.x);
					double yh = Math.sqrt(1 - p.y * p.y);
					double zh = Math.sqrt(1 - p.z * p.z);

					glColor3f(0.5f, 0.5f, 0.5f);

					glVertex3d(-yh * zh, p.y, p.z);
					glVertex3d(yh * zh, p.y, p.z);

					glVertex3d(p.x, -xh * zh, p.z);
					glVertex3d(p.x, xh * zh, p.z);

					glVertex3d(p.x, p.y, -xh * yh);
					glVertex3d(p.x, p.y, xh * yh);
				}

				// draw velocity line
				glColor3f(1, 1, 1);
				glVertex3f((float) p.x, (float) p.y, (float) p.z);
				glVertex3f((float) v.x, (float) v.y, (float) v.z);

				// draw thrust line
				glColor3f(1, 0, 0);
				glVertex3f((float) p.x, (float) p.y, (float) p.z);
				glVertex3f((float) t.x, (float) t.y, (float) t.z);

			}
			glEnd();

			drawString((float) p.x, (float) p.y + 0.15f, (float) p.z, String.format("%.1f", rp.getEnergy()));
			drawString((float) p.x, (float) p.y + 0.1f, (float) p.z, rp.getName());
		}
	}

	private void drawAxis() {
		glBegin(GL_LINES);
		// if we are looking up, render Y first
		if(camera.angleY <= 0) {
			// Y
			glColor3f(0, 1, 0);
			glVertex3f(0, 0, 0);
			glVertex3f(0, AXIS_LENGTH, 0);
		}
		// X
		glColor3f(1, 0, 0);
		glVertex3f(0, 0, 0);
		glVertex3f(AXIS_LENGTH, 0, 0);
		// Z
		glColor3f(0, 0, 1);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, AXIS_LENGTH);

		// if we are looking down, render Y last
		if(camera.angleY > 0) {
			// Y
			glColor3f(0, 1, 0);
			glVertex3f(0, 0, 0);
			glVertex3f(0, AXIS_LENGTH, 0);
		}
		glEnd();
	}

	private void drawBullets(BattleRunner battle) {
		// TODO allow special bullet colors
		glColor3f(1, 1, 1);
		for(BulletImpl b : battle.getBullets()) {
			glBegin(GL_POINTS);
			Vector p = b.getPosition().scale(1.0 / Rules.BATTLEFIELD_RADIUS);
			glVertex3d(p.x, p.y, p.z);
			glEnd();
		}
	}

	private void drawCircle(float x, float y, float z, float r, int segments) {
		glPushMatrix();

		glTranslatef(x, y, z);
		alignToCamera();

		glBegin(GL_LINE_LOOP);
		double angle = Math.PI * 2 / segments;
		for(int i = 0; i < segments; ++i) {
			glVertex3d(Math.sin(angle * i) * r, Math.cos(angle * i) * r, 0);
		}
		glEnd();

		glPopMatrix();
	}

	private void drawGrid() {
		glColor3f(0.1f, 0.1f, 0.1f);
		glBegin(GL_LINES);
		for(float x = -1; x <= 1; x++) {
			for(float y = -1; y <= 1; y++) {
				glVertex3f(-1, x, y);
				glVertex3f(1, x, y);

				glVertex3f(x, -1, y);
				glVertex3f(x, 1, y);

				glVertex3f(x, y, -1);
				glVertex3f(x, y, 1);
			}
		}
		glEnd();
	}

	private void drawMissile(float x, float y, float z) {
		glPushMatrix();

		glTranslatef(x, y, z);
		alignToCamera();

		// TODO allow special missile colors
		glColor3f(1, 0, 0);

		glBegin(GL_LINE_LOOP);
		float r = (float) (Rules.MISSILE_RADIUS / Rules.BATTLEFIELD_RADIUS);
		glVertex3f(0, r, 0);
		glVertex3f(r, 0, 0);
		glVertex3f(0, -r, 0);
		glVertex3f(-r, 0, 0);
		glEnd();

		glPopMatrix();
	}

	private void drawRobot(float x, float y, float z) {
		// TODO allow special robot colors
		drawCircle(x, y, z, (float) (Rules.ROBOT_RADIUS / Rules.BATTLEFIELD_RADIUS), 16);
	}

	private void drawString(float x, float y, float z, String str) {
		glPushMatrix();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		glTranslatef(x, y, z);

		alignToCamera();
		float scale = 1f / height;
		glScalef(scale, -scale, scale);

		int width = font.width(str) >> 1;

		glTranslatef(-width, 0, 0);
		glColor3f(1f, 1f, 1f);

		font.draw(str);

		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		glDisable(GL_BLEND);

		glPopMatrix();
	}

	public boolean isCreated() {
		return created;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	private void setupDisplay(int width, int height) {
		glViewport(0, 0, width, height);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		GLU.gluPerspective(FOV, width / (float) height, NEAR_CLIP, FAR_CLIP);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public void update() {
		if(dispose) {
			doDispose();
			return;
		}
		if(!isDisplayable()) {
			return;
		}
		create();
		updateCameraControl();
		updateDisplay();
		updateGL();
	}

	private void updateCameraControl() {
		final float SCALE = (float) (Math.PI / 4096);
		int wheeld = Mouse.getDWheel();
		if(Mouse.isButtonDown(0)) {
			int dx = Mouse.getDX();
			int dy = Mouse.getDY();
			if(Mouse.isButtonDown(1)) {
				if(zoom) {
					camera.distance -= dy / 100f;
				}
				zoom = true;
			} else {
				if(drag) {
					camera.angleXZ -= dx * SCALE;
					camera.angleY -= dy * SCALE;
					camera.momentumXZ = -dx * SCALE;
					camera.momentumY = -dy * SCALE;
				}
				drag = true;
			}
		} else if(wheeld != 0) {
			camera.distance -= wheeld / 960f;
		} else {
			drag = zoom = false;
		}
	}

	private void updateDisplay() {
		if(!isDisplayable() || !created) {
			return;
		}

		// Clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		glDepthFunc(GL_LEQUAL);

		// glLineWidth(2);
		glPointSize(2);

		glPushMatrix();
		// CAMERA
		camera.set(0, 0, 0);
		camera.updateCamera();
		GLU.gluLookAt(camera.x, camera.y, camera.z, 0, 0, 0, 0, 1, 0);

		if(DRAW_GRID) {
			drawGrid();
		}

		if(DRAW_AXIS) {
			drawAxis();
		}

		glColor3f(0.4f, 0.4f, 0.5f);

		// draw the arena
		glColor3f(0.4f, 0.2f, 0.2f);
		float x = camera.distance;
		x = .18666666f * x * x + -1.0233333f * x + 2.46f;
		drawCircle(0, 0, 0, x, 64);

		BattleRunner battle = engine.getCurrentBattle();

		if(battle != null) {
			synchronized(battle) {
				drawBullets(battle);
				drawAllMissiles(battle);
				drawAllRobots(battle);
			}
		}

		// TODO draw GUI

		glFlush();
		glPopMatrix();
	}

	private void updateGL() {
		if(!isDisplayable()) {
			return;
		}
		Display.update();
	}
}
