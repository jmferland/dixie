package dixie.captcha;

import com.jhlabs.image.PinchFilter;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.TransformFilter;
import com.jhlabs.image.TwirlFilter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

/**
 * Captcha generation that should be modularized and put into a jar
 * if we ever need this anywhere else.
 * 
 * @author jferland
 */
public class CaptchaProducer
{
	private static final Font[] FONTS =
	{
		new Font("Andale Mono", Font.BOLD, 40),
		new Font("Arial", Font.BOLD, 40),
		new Font("Courier", Font.BOLD, 40),
		new Font("Courier New", Font.BOLD, 40),
		new Font("Georgia", Font.BOLD, 40)
	};
	private static final Color[] FONT_COLORS =
	{
		Color.BLACK
	};
	private static final int HEIGHT = 50;
	private static final int WIDTH = 200;
	private static final int CHAR_DEGREES_VARIANCE = 45;
	private final long seed;
	private final String text;
	private BufferedImage cachedImage;

	public CaptchaProducer(String text, long seed)
	{
		this.text = text;

		// Store seed instead of a Random instance incase we ever need to
		// re-draw.
		this.seed = seed;
	}

	public BufferedImage draw()
	{
		if (cachedImage == null)
		{
			BufferedImage image = null;
			Random random = new Random(seed);

			// Draw text.
			image = drawText(text, random);

			// Distort image.
			image = noiseCurve(image, random);
			//image = twirlFilter(image, random);
			//image = pinchFilter(image, random);
			image = rippleFilter(image, random);

			// TODO: try FieldWarpFilter and CircleFilter.
			// TODO: try adding a background and using OffsetFilter?
			// TODO: try random dots and scratches?

			// Draw background.
			image = drawBackground(image);

			// Draw border.
			drawBorder(image);

			cachedImage = image;
		}

		return cachedImage;
	}

	private BufferedImage drawText(String text, Random random)
	{
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2D = (Graphics2D) image.getGraphics();
		g2D.setRenderingHints(new RenderingHints(new HashMap()
		{

			{
				put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			}
		}));

		FontRenderContext frc = g2D.getFontRenderContext();

		// Divide the captcha area into equal portions for each character
		// and randomly place each character within their portion.
		int cellWidth = WIDTH / text.length();

		char[] chs = this.text.toCharArray();
		for (int i = 0; i < chs.length; i++)
		{
			Font font = FONTS[random.nextInt(FONTS.length)];
			g2D.setFont(font);
			g2D.setColor(FONT_COLORS[random.nextInt(FONT_COLORS.length)]);

			char[] charToDraw = new char[]
			{
				chs[i]
			};

			GlyphVector gv = font.createGlyphVector(frc, charToDraw);
			int charWidth = (int) gv.getVisualBounds().getWidth();
			int charHeight = (int) gv.getVisualBounds().getHeight();
			int charDiameter = (int) Math.sqrt(Math.pow(charWidth, 2) + Math.pow(charHeight, 2));
			int charRadius = charDiameter / 2;

			int x = (i * cellWidth) + ((charDiameter - charWidth) / 2);
			int xWiggleRoom = cellWidth - charDiameter;
			if (xWiggleRoom > 0)
			{
				x += random.nextInt(xWiggleRoom);
			}

			int y = HEIGHT - ((charDiameter - charHeight) / 2);
			int yWiggleRoom = HEIGHT - charDiameter;
			if (yWiggleRoom > 0)
			{
				y -= random.nextInt(yWiggleRoom);
			}

			double theta = (random.nextInt(CHAR_DEGREES_VARIANCE * 2) - CHAR_DEGREES_VARIANCE) * Math.PI / 180;
			g2D.rotate(theta, x + charRadius, y - charRadius);
			g2D.drawChars(charToDraw, 0, charToDraw.length, x, y);
			g2D.rotate(-theta, x + charRadius, y - charRadius);
		}

		return image;
	}

	private BufferedImage drawBackground(BufferedImage image)
	{
		// Create an opaque image.
		BufferedImage background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2D = (Graphics2D) background.getGraphics();
		g2D.setRenderingHints(new RenderingHints(new HashMap()
		{

			{
				put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			}
		}));

		g2D.setColor(Color.WHITE);
		g2D.fill(new Rectangle2D.Double(0, 0, image.getWidth(), image.getHeight()));

		// Draw the transparent image onto the background.
		g2D.drawImage(image, 0, 0, null);

		return background;
	}

	/**
	 * Twirl effect.
	 *
	 * @param image to apply effect on.
	 * @param random for random settings.
	 * @return new image with effect on it.
	 * @see http://www.jhlabs.com/ip/filters/TwirlFilter.html
	 */
	private BufferedImage twirlFilter(BufferedImage image, Random random)
	{
		TwirlFilter twirlFilter = new TwirlFilter();
		twirlFilter.setCentreX(0.75f); // percentage
		twirlFilter.setCentreY(0.75f); // percentage
		twirlFilter.setAngle((float) (25 * Math.PI / 180));
		twirlFilter.setRadius(HEIGHT * 2);
		return twirlFilter.filter(image, null);
	}

	/**
	 * Pinch effect.
	 *
	 * @param image to apply effect on.
	 * @param random for random settings.
	 * @return new image with effect on it.
	 * @see http://www.jhlabs.com/ip/filters/PinchFilter.html
	 */
	private BufferedImage pinchFilter(BufferedImage image, Random random)
	{
		PinchFilter pinchFilter = new PinchFilter();
		pinchFilter.setCentreX(0.25f); // percentage
		pinchFilter.setCentreY(0.25f); // percentage
		pinchFilter.setAmount(0.2f); // percentage
		pinchFilter.setAngle((float) (25 * Math.PI / 180));
		pinchFilter.setRadius(HEIGHT * 2);
		return pinchFilter.filter(image, null);
	}

	/**
	 * Water ripple effect.
	 *
	 * @param image to apply effect on.
	 * @param random for random settings.
	 * @return new image with effect on it.
	 * @see http://www.jhlabs.com/ip/filters/WaterFilter.html
	 */
	private BufferedImage rippleFilter(BufferedImage image, Random random)
	{
		RippleFilter rippleFilter = new RippleFilter();

		rippleFilter.setWaveType(RippleFilter.NOISE);
		rippleFilter.setXAmplitude(4.7f + random.nextFloat());
		rippleFilter.setYAmplitude(2.9f + random.nextFloat());
		rippleFilter.setXWavelength(20 + random.nextInt(10));
		rippleFilter.setYWavelength(10 + random.nextInt(10));

		// Clamp pixels to the image edges.
		rippleFilter.setEdgeAction(TransformFilter.CLAMP);

		return rippleFilter.filter(image, null);
	}

	/**
	 * Draws a noise curve on the image. The noise curve depends on the factor
	 * values. Noise won't be visible if all factors have the value > 1.0f.
	 *
	 * @param image draw the noise curve on this image.
	 * @param random for random settings.
	 * @return new image with effect on it.
	 */
	public BufferedImage noiseCurve(BufferedImage image, Random random)
	{
		Color color = Color.BLACK;

		// Image size.
		int width = image.getWidth();
		int height = image.getHeight();

		// The points where the line changes the stroke and direction.
		Point2D[] pts = null;

		// The curve from where the points are taken
		CubicCurve2D cc = new CubicCurve2D.Float(width * random.nextFloat(), height * random.
				nextFloat(),
												 width * random.nextFloat(), height * random.
				nextFloat(),
												 width * random.nextFloat(), height * random.
				nextFloat(),
												 width * random.nextFloat(), height * random.
				nextFloat());

		// Creates an iterator to define the boundary of the flattened curve.
		PathIterator pi = cc.getPathIterator(null, 2);
		Point2D tmp[] = new Point2D[200];
		int i = 0;

		// While pi is iterating the curve, adds points to tmp array.
		while (!pi.isDone())
		{
			float[] coords = new float[6];
			switch (pi.currentSegment(coords))
			{
				case PathIterator.SEG_MOVETO:
				case PathIterator.SEG_LINETO:
					tmp[i] = new Point2D.Float(coords[0], coords[1]);
			}
			i++;
			pi.next();
		}

		pts = new Point2D[i];
		System.arraycopy(tmp, 0, pts, 0, i);

		Graphics2D graph = (Graphics2D) image.getGraphics();
		graph.setRenderingHints(new RenderingHints(new HashMap()
		{

			{
				put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		}));

		graph.setColor(color);

		// For the maximum 3 point change the stroke and direction.
		for (i = 0; i < pts.length - 1; i++)
		{
			if (i < 3)
			{
				graph.setStroke(new BasicStroke(0.9f * (4 - i)));
			}
			graph.drawLine((int) pts[i].getX(), (int) pts[i].getY(),
						   (int) pts[i + 1].getX(), (int) pts[i + 1].getY());
		}

		graph.dispose();

		return image;
	}

	/**
	 * Draw a border around the image.
	 *
	 * @param image to draw border around.
	 */
	private void drawBorder(BufferedImage image)
	{
		Graphics2D g2D = (Graphics2D) image.getGraphics();
		g2D.setRenderingHints(new RenderingHints(new HashMap()
		{

			{
				put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
		}));

		g2D.setColor(Color.BLACK);

		Line2D line1 = new Line2D.Double(0, 0, 0, WIDTH);
		g2D.draw(line1);
		Line2D line2 = new Line2D.Double(0, 0, WIDTH, 0);
		g2D.draw(line2);
		line2 = new Line2D.Double(0, HEIGHT - 1, WIDTH, HEIGHT - 1);
		g2D.draw(line2);
		line2 = new Line2D.Double(WIDTH - 1, HEIGHT - 1, WIDTH - 1, 0);
		g2D.draw(line2);
	}
}
