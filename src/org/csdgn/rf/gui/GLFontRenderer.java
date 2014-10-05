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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
 * @author Robert Maupin
 * 
 */
public class GLFontRenderer {
	private static final Color TRANSPARENT = new Color(0, true);
	private final Font font;
	private final Rectangle[] bounds;
	private final int id;
	
	private double xscale = 1;
	private double yscale = 1;

	public GLFontRenderer(Font font) {
		this.font = font;
		
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		
		
		bounds = new Rectangle[128];
		id = buildFontCache(hints);
	}
	
	private int buildFontCache(RenderingHints hints) {
		Rectangle2D maxbounds = font.getMaxCharBounds(new FontRenderContext(new AffineTransform(), false, false));
		int width = (int) Math.ceil(maxbounds.getWidth() + 2);
		int height = (int) Math.ceil(maxbounds.getHeight() + 2);
		
		BufferedImage texture = new BufferedImage(width*10, height*10, BufferedImage.TYPE_INT_ARGB);
		
		int x = 0;
		int y = 0;
		
		for(int i = 32; i < 127; ++i) {
			/* clear the buffer */
			BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gx = buffer.createGraphics();
			gx.setBackground(TRANSPARENT);
			gx.clearRect(0, 0, buffer.getWidth(), buffer.getHeight());
			gx.setColor(Color.WHITE);
			gx.setRenderingHints(hints);
			gx.setFont(font);
	
			char[] chr = new char[] { (char) i};
	
			FontMetrics fm = gx.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(chr, 0, 1, gx);
	
			gx.drawChars(chr, 0, 1, 0, -(int) rect.getY());
			gx.dispose();
	
			BufferedImage sub = buffer.getSubimage((int) rect.getX(), 0, (int) rect.getWidth(), (int) rect.getHeight());
			
			if(x + sub.getWidth() > texture.getWidth()) {
				x = 0;
				y += height;
			}
			
			Rectangle irect = new Rectangle(x,y,sub.getWidth(),sub.getHeight());
			bounds[i] = irect;
			
			gx.dispose();
			gx = texture.createGraphics();
			gx.drawImage(sub, x, y, null);
			
			//get ready for the next
			x += irect.width + 1;
		}
		
		width = texture.getWidth();
		height = texture.getHeight();
		int bufferSize = width * height;
		
		xscale = 1.0/width;
		yscale = 1.0/height;
		
		IntBuffer buf = BufferUtils.createIntBuffer(bufferSize);
		{
			int[] abuf = texture.getRGB(0, 0, width, height, null, 0, width);
			for(int i = 0; i < abuf.length; ++i) {
				buf.put(abuf[i]);
			}
		}
		buf.flip();
		
		int textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
		
		return textureID;
		
	}

	public void dispose() {
		/* delete ignores invalid id's :) */
		glDeleteTextures(id);
	}

	public void draw(int c) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glEnable(GL_TEXTURE_2D);
		
		if(c < 32 || c > 127) {
			c = '?';
		}
		
		Rectangle rect = bounds[c];

		glBindTexture(GL_TEXTURE_2D, id);
		glBegin(GL_QUADS);
		glTexCoord2d(rect.x*xscale, rect.y*yscale);
		glVertex2i(0, 0);

		glTexCoord2d((rect.x+rect.width)*xscale, rect.y*yscale);
		glVertex2i(rect.width, 0);

		glTexCoord2d((rect.x+rect.width)*xscale, (rect.y+rect.height)*yscale);
		glVertex2i(rect.width, rect.height);

		glTexCoord2d((rect.x)*xscale, (rect.y+rect.height)*yscale);
		glVertex2i(0, rect.height);
		glEnd();

		glDisable(GL_TEXTURE_2D);
	}

	public void draw(String str) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glEnable(GL_TEXTURE_2D);
		
		glBindTexture(GL_TEXTURE_2D, id);
		
		glBegin(GL_QUADS);

		Rectangle rect;
		
		int x = 0;
		for(int i = 0; i < str.length(); ++i) {
			int c = str.charAt(i);
			if(c < 32 || c > 127) {
				c = '?';
			}
			rect = bounds[c];

			glTexCoord2d(rect.x*xscale, rect.y*yscale);
			glVertex2i(x, 0);

			glTexCoord2d((rect.x+rect.width)*xscale, rect.y*yscale);
			glVertex2i(x+rect.width, 0);

			glTexCoord2d((rect.x+rect.width)*xscale, (rect.y+rect.height)*yscale);
			glVertex2i(x+rect.width, rect.height);

			glTexCoord2d((rect.x)*xscale, (rect.y+rect.height)*yscale);
			glVertex2i(x, rect.height);

			x += rect.width;
		}
		
		glEnd();
		

		glDisable(GL_TEXTURE_2D);
	}

	public int height(int c) {
		if(c < 32 || c > 127)
			return bounds['?'].height;
		return bounds[c].height;
	}

	public int height(String str) {
		int height = 0;
		for(int i = 0; i < str.length(); ++i) {
			int c = str.charAt(i);
			if(c < 32 || c > 127) {
				c = '?';
			}
			if(bounds[c].height > height) {
				height = bounds[c].height;
			}
		}
		return height;
	}

	public int width(int c) {
		if(c < 32 || c > 127)
			return bounds['?'].width;
		return bounds[c].width;
	}

	public int width(String str) {
		int width = 0;
		for(int i = 0; i < str.length(); ++i) {
			int c = str.charAt(i);
			if(c < 32 || c > 127) {
				c = '?';
			}
			width += bounds[c].width;
		}
		return width;
	}
}
