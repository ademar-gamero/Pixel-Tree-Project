
import java.awt.Color;

import edu.uwm.cs351.TreePixelCollection;
import edu.uwm.cs351.Pixel;


public class TestTreePixelCollection extends TestCollection<Pixel> {
	Pixel e0 = new Pixel(0, 0);
	Pixel e1 = new Pixel(1, 1, Color.WHITE); 
	Pixel e2 = new Pixel(2, 2, Color.BLACK);
	Pixel e3 = new Pixel(3, 3, Color.RED);
	Pixel e4 = new Pixel(4, 4, Color.BLUE);
	Pixel e5 = new Pixel(5, 5, Color.GREEN);
	Pixel e6 = new Pixel(6, 6, Color.YELLOW);
	Pixel e7 = new Pixel(7, 7, Color.MAGENTA);
	Pixel e8 = new Pixel(8, 8, Color.ORANGE);
	Pixel e9 = new Pixel(9, 9, Color.GRAY);

	@Override
	protected void initCollections() {
		e = new Pixel[] { e0, e1, e2, e3, e4, e5, e6, e7, e8, e9 };
		c = new TreePixelCollection();
		permitNulls = false; 
		permitDuplicates = false; 
		preserveOrder = false;
	}

}
