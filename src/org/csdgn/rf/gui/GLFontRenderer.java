package org.csdgn.rf.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;

/**
 * Only renders basic latin characters at the moment.
 * 
 * @author Chase
 * 
 */
public class GLFontRenderer {
	private static final Color TRANSPARENT = new Color(0, true);
	private final RenderingHints hints;
	private final BufferedImage buffer;
	private final Font font;

	private final int[] idCache;
	private final int[] widthCache;
	private final int[] heightCache;

	public GLFontRenderer(Font font) {
		this.font = font;
		Rectangle2D bounds = font.getMaxCharBounds(new FontRenderContext(new AffineTransform(), false, false));
		int width = (int) Math.ceil(bounds.getWidth() + 2);
		int height = (int) Math.ceil(bounds.getHeight() + 2);
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		hints = new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		idCache = new int[128];
		widthCache = new int[128];
		heightCache = new int[128];

		/* build the font cache */
		for(int i = 32; i < 127; ++i) {
			cacheCharacter(i);
		}
	}

	private void cacheCharacter(int c) {
		BufferedImage render = renderCharacter(c);
		int width = render.getWidth();
		int height = render.getHeight();
		int bufferSize = width * height;
		// TODO round up dimensions to the nearest power of two

		IntBuffer buf = BufferUtils.createIntBuffer(bufferSize);
		{
			int[] abuf = render.getRGB(0, 0, width, height, null, 0, width);
			for(int i = 0; i < abuf.length; ++i) {
				buf.put(abuf[i]);
			}
		}
		buf.flip();

		int textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

		idCache[c] = textureID;
		widthCache[c] = width;
		heightCache[c] = height;
	}

	public void dispose() {
		buffer.flush();
		/* delete ignores invalid id's :) */
		for(int id : idCache) {
			glDeleteTextures(id);
		}
	}

	public void draw(int c) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glEnable(GL_TEXTURE_2D);

		glBindTexture(GL_TEXTURE_2D, idCache[c]);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex2i(0, 0);

		glTexCoord2f(1, 0);
		glVertex2i(widthCache[c], 0);

		glTexCoord2f(1, 1);
		glVertex2i(widthCache[c], heightCache[c]);

		glTexCoord2f(0, 1);
		glVertex2i(0, heightCache[c]);
		glEnd();

		glDisable(GL_TEXTURE_2D);
	}

	public int height(int c) {
		return heightCache[c];
	}

	private BufferedImage renderCharacter(int c) {
		/* clear the buffer */
		Graphics2D gx = buffer.createGraphics();
		gx.setBackground(TRANSPARENT);
		gx.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
		gx.setColor(Color.WHITE);
		gx.setRenderingHints(hints);
		gx.setFont(font);

		char[] chr = new char[] { (char) c };

		FontMetrics fm = gx.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(chr, 0, 1, gx);

		gx.drawChars(chr, 0, 1, 0, -(int) rect.getY());
		gx.dispose();

		return buffer.getSubimage((int) rect.getX(), 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	public int width(int c) {
		return widthCache[c];
	}
}
