package net.wohlfart.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FontRenderer {
	protected static final Logger LOGGER = LoggerFactory.getLogger(FontRenderer.class);

	//private static final String FONT_FILE = "/fonts/alphbeta.ttf";
	private static final String FONT_FILE = "/fonts/Greyscale_Basic_Regular.ttf";

	private CharacterAtlas atlas;

	private final int SIZE = 512;

	private final int WIDTH = SIZE;
	private final int HEIGHT = SIZE;


	private final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789;:,.-_#+?!\"()";


	public FontRenderer init() {
		String filename = FONT_FILE;
		try (InputStream inputStream = ClassLoader.class.getResourceAsStream(filename);) {
		    Font font = Font.createFont(Font.TRUETYPE_FONT, inputStream);
		    font = font.deriveFont(50f);
		    //font = new java.awt.Font("monospaced", 0, 60);
		    atlas = createCharacterAtlas(font);
		} catch (FontFormatException | IOException ex) {
			LOGGER.error("init font from '" + filename + "' failed, atlas will be null", ex);
		}
		return this;
	}

	public CharacterAtlas getCharacterAtlas() {
		return atlas;
	}

	CharacterAtlas createCharacterAtlas(Font font) {
		CharacterAtlas atlas = new CharacterAtlas();

		BufferedImage buffImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) buffImage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		g.setColor(Color.BLUE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		FontMetrics fontMetrics = g.getFontMetrics();
		LineMetrics lineMetrics = fontMetrics.getLineMetrics(chars, g);

		float height = lineMetrics.getHeight();
		float ascent = lineMetrics.getAscent();
		float x = 0;
		float y = 0;
		for (char c : chars.toCharArray()) {
			float width = fontMetrics.charWidth(c);
			if ((x + width) > WIDTH) {  // new line
				x = 0;
				y += height;
				if (y + height > HEIGHT) {
					throw new IllegalStateException("chars don't fit into atlas");
				}
			}
			g.setColor(Color.BLACK);
			g.drawString(String.valueOf(c), x, y + ascent);
			atlas.put(c, x, y, width, height);
			g.setColor(Color.RED);
			g.drawRect((int)x, (int)y, (int)width, (int)height);
			x += width;
		}
		atlas.setImage(buffImage);
		return atlas;
	}



}
