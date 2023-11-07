import java.awt.Color;
import java.awt.Point;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.TreePixelCollection;
import edu.uwm.cs351.Pixel;
import junit.framework.TestCase;

public class TestInvariant extends TestCase {
	protected Point pt0 = new Point(1,0);
	protected Point pt1 = new Point(1,1);
	protected Point pt2 = new Point(1,2);
	protected Point pt3 = new Point(2,1);
	protected Point pt3a = new Point(2,1);
	protected Point pt3b = new Point(2,1);
	protected Point pt4 = new Point(3,0);
	protected Point pt5 = new Point(3,1);
	protected Point pt6 = new Point(4,0);
	
	protected Pixel e1 = new Pixel(1,1);
	protected Pixel e2 = new Pixel(1,2);
	protected Pixel e2b = new Pixel(1,2, Color.YELLOW);
	protected Pixel e3 = new Pixel(2,1);
	protected Pixel e3a = new Pixel(2,1);
	protected Pixel e3b = new Pixel(2,1, Color.RED);
	protected Pixel e4 = new Pixel(3,0);
	protected Pixel e4b = new Pixel(3,0,Color.BLUE);
	protected Pixel e5 = new Pixel(3,1,Color.RED);
	
	protected TreePixelCollection.Spy spy;
	protected int reports;
	protected TreePixelCollection r;
	protected Iterator<Pixel> it;

	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
		reports = 0;
		Consumer<String> savedReporter = spy.getReporter();
		try {
			spy.setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			spy.setReporter(null);
		} finally {
			spy.setReporter(savedReporter);
		}
	}
	
	protected void assertWellFormed(boolean expected, TreePixelCollection r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}

	protected void assertWellFormed(boolean expected, Iterator<Pixel> it) {
		assertReporting(expected, () -> spy.wellFormed(it));
	}

	@Override // implementation
	protected void setUp() {
		spy = new TreePixelCollection.Spy();
	}
	
	protected TreePixelCollection.Spy.Node n0, n1, n2, n3, n4, n5;
	protected TreePixelCollection.Spy.Node n6, n7, n8, n9, n10;
	protected TreePixelCollection.Spy.Node n0a, n1a, n2a, n3a, n4a, n5a;
	protected TreePixelCollection.Spy.Node n6a, n7a, n8a, n9a, n10a;
	protected TreePixelCollection.Spy.Node n;

	protected TreePixelCollection self;
	
	// inline me!
	private void assertWellFormed(boolean expected) {
		assertWellFormed(expected, self);
	}

	public void testC00() {
		self = spy.create(null, 0, 0);
		assertWellFormed(false);
	}

	public void testC01() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 0, 0);
		assertWellFormed(true);
	}

	public void testC02() {
		self = spy.create(null, -1, 0);
		assertWellFormed(false);
	}
	
	public void testC03() {
		n3 = spy.newNode(e3, null, null, null);
		self = spy.create(n3, 0, 0);
		assertWellFormed(false);
	}
	
	public void testC04() {
		n3 = spy.newNode(e3, null, null, null);
		n0 = spy.newNode(null, n3, null, null);
		self = spy.create(n0, 0, 0);
		assertWellFormed(false);
	}
	
	public void testC05() {
		n3 = spy.newNode(e3, null, null, null);
		n0 = spy.newNode(null, n3, null, null);
		self = spy.create(n0, 1, 0);
		assertWellFormed(false);
	}
	
	public void testC06() {
		n0 = spy.newNode(null, null, null, null);
		n0.setNext(n0);
		self = spy.create(n0, 1, 0);
		assertWellFormed(false);
	}
	
	public void testC07() {
		n0 = spy.newNode(null, null, null, null);
		n0.setNext(n0);
		self = spy.create(n0, 0, 0);
		assertWellFormed(false);
	}
	
	public void testC08() {
		n3 = spy.newNode(e3, null, null, null);
		n0 = spy.newNode(null, null, null, n3);
		self = spy.create(n0, 1, 0);
		assertWellFormed(false);
	}
	
	public void testC09() {
		n3 = spy.newNode(e3, null, null, null);
		n0 = spy.newNode(null, null, null, n3);
		self = spy.create(n0, 0, 0);
		assertWellFormed(false);
	}
	
	public void testC10() {
		n1 = spy.newNode(e1, null, null, null);
		n0 = spy.newNode(null, null, n1, n1);
		self = spy.create(n0, 0, 1);
		assertWellFormed(false);
	}
	
	public void testC11() {
		n1 = spy.newNode(e1, null, null, null);
		n0 = spy.newNode(null, null, n1, n1);
		self = spy.create(n0, 1, 1);
		assertWellFormed(true);
	}
	
	public void testC12() {
		n1 = spy.newNode(e1, null, null, null);
		n1a = spy.newNode(e1, null, null, null);
		n0 = spy.newNode(null, null, n1, n1a);
		self = spy.create(n0, 1, 1);
		assertWellFormed(false);
	}
	
	public void testC13() {
		n1 = spy.newNode(e1, null, null, null);
		n0 = spy.newNode(null, null, n1, null);
		self = spy.create(n0, 1, 1);
		assertWellFormed(false);
	}
	
	public void testC14() {
		n1 = spy.newNode(e1, null, null, null);
		n0 = spy.newNode(null, null, n1, null);
		n0.setNext(n0);
		self = spy.create(n0, 1, 1);
		assertWellFormed(false);
	}
	
	public void testC15() {
		n1 = spy.newNode(e1, null, null, null);
		n0 = spy.newNode(null, null, n1, n1);
		n1.setNext(n1);
		self = spy.create(n0, 1, 1);
		assertWellFormed(false);
	}
	
	public void testC16() {
		n1 = spy.newNode(e1, null, null, null);
		n0 = spy.newNode(null, null, n1, n1);
		self = spy.create(n0, 2, 1);
		assertWellFormed(false);
	}
	
	public void testC17() {
		n1 = spy.newNode(e1, null, null, null);
		n3 = spy.newNode(e3, null, null, null);
		n0 = spy.newNode(null, n1, n3, n3);
		n1.setNext(n0);
		self = spy.create(n0, 1, 1);
		assertWellFormed(false);
	}
	
	public void testC18() {
		n1 = spy.newNode(e1, null, null, null);
		n3 = spy.newNode(e3, null, null, null);
		n0 = spy.newNode(null, n1, n3, n3);
		n1.setNext(n0);
		self = spy.create(n0, 2, 1);
		assertWellFormed(false);
	}

	public void testC20() {
		n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, n2, null, null);
		n2.setNext(n4);
		n0 = spy.newNode(null, null, n4, n2);
		self = spy.create(n0, 2, 2);
		assertWellFormed(true);
	}
	
	public void testC21() {
		n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, n2, null, null);
		n2.setNext(n4);
		n0 = spy.newNode(null, null, n4, n2);
		self = spy.create(n0, 3, 2);
		assertWellFormed(false);
	}
	
	public void testC22() {
		n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, n2, null, n2);
		n0 = spy.newNode(null, null, n4, n4);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}

	public void testC23() {
		n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, n2, null, null);
		n4a = spy.newNode(e4, n2, null, null);
		n2.setNext(n4a);
		n0 = spy.newNode(null, null, n4, n2);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}
	
	public void testC24() {
		n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, n2, null, null);
		n2a = spy.newNode(e2, null, null, n4);
		n2.setNext(n4);
		n0 = spy.newNode(null, null, n4, n2a);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}

	public void testC25() {
		n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, null, n2, n2);
		n0 = spy.newNode(null, null, n4, n4);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}

	public void testC26() {
		n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, null, n2, null);
		n2.setNext(n4);
		n0 = spy.newNode(null, null, n4, n2);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}
	
	public void testC27() {
		n4a = spy.newNode(e4b, null, null, null);
		n4 = spy.newNode(e4, null, n4a, n4a);
		n0 = spy.newNode(null, null, n4, n4);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}

	public void testC28() {
		n4a = spy.newNode(e4b, null, null, null);
		n4 = spy.newNode(e4, null, n4a, null);
		n4a.setNext(n4);
		n0 = spy.newNode(null, null, n4, n4a);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}
	
	public void testC29() {
		n5 = spy.newNode(null, null, null, null);
		n4 = spy.newNode(e4, null, n5, n5);
		n0 = spy.newNode(null, null, n4, n4);
		self = spy.create(n0, 2, 2);
		assertWellFormed(false);
	}
	
	public void testC30() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n5);
		n0 = spy.newNode(null, null, n3, n2);
		n2.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(true);
	}
	
	public void testC31() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		n2.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC32() {
		n2 = spy.newNode(e2, null, null, null);
		n2a = spy.newNode(e2, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n5);
		n0 = spy.newNode(null, null, n3, n2a);
		n2.setNext(n3);
		n2a.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC33() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n5a= spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n5a);
		n0 = spy.newNode(null, null, n3, n2);
		n2.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC34() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n5);
		n3a= spy.newNode(e3b, n2, n5, n5);
		n0 = spy.newNode(null, null, n3, n2);
		n2.setNext(n3a);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC35() {
		n2 = spy.newNode(e2b, null, null, null);
		n1 = spy.newNode(e1, null, null, n2);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n5);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC36() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n2);
		n0 = spy.newNode(null, null, n3, n5);
		n5.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC37() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n2, n5, n5);
		n0 = spy.newNode(null, null, n3, n2);
		n2.setNext(n3);
		n5.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC38() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(null, null, null, null);
		n3 = spy.newNode(null, n2, n5, n5);
		n0 = spy.newNode(null, null, n3, n2);
		n2.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC39() {
		n2 = spy.newNode(e2b, null, null, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, n5, n2, n2);
		n0 = spy.newNode(null, null, n3, n5);
		n5.setNext(n3);
		self = spy.create(n0, 3, 3);
		assertWellFormed(false);
	}
	
	public void testC40() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3);
		self = spy.create(n0, 5, 4);
		assertWellFormed(false);
	}
	
	public void testC41() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3);
		n4.setNext(n5);
		self = spy.create(n0, 5, 4);
		assertWellFormed(true);
	}
	
	public void testC42() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n3a= spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3a);
		n4.setNext(n5);
		self = spy.create(n0, 5, 4);
		assertWellFormed(false);
	}
	
	public void testC43() {
		// n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, null, null, null);
		n1 = spy.newNode(e1, null, n4, n4);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3, n1, n5, n5);
		n0 = spy.newNode(null, null, n3, n1);
		n4.setNext(n3);
		self = spy.create(n0, 4, 4);
		assertWellFormed(false);
	}
	
	public void testC44() {
		// n2 = spy.newNode(e2, null, null, null);
		n4 = spy.newNode(e4, null, null, null);
		n1 = spy.newNode(e1, null, n4, null);
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n1.setNext(n3);
		n4.setNext(n5);
		self = spy.create(n0, 4, 4);
		assertWellFormed(false);
	}
	
	public void testC45() {
		n2 = spy.newNode(e2, null, null, null);
		// n4 = spy.newNode(e4, null, null, null);
		n1 = spy.newNode(e1, null, null, null);
		n5 = spy.newNode(e5, n2, null, null);
		n3 = spy.newNode(e3, n1, n5, n2);
		n0 = spy.newNode(null, null, n3, n1);
		n1.setNext(n3);
		n2.setNext(n5);
		self = spy.create(n0, 4, 4);
		assertWellFormed(false);
	}
	
	public void testC46() {
		n2 = spy.newNode(e2, null, null, null);
		// n4 = spy.newNode(e4, null, null, null);
		n1 = spy.newNode(e1, null, null, null);
		n5 = spy.newNode(e5, n2, null, null);
		n3 = spy.newNode(e3, n1, n5, n5);
		n0 = spy.newNode(null, null, n3, n1);
		n1.setNext(n2);
		n2.setNext(n3);
		self = spy.create(n0, 4, 4);
		assertWellFormed(false);
	}

	public void testC47() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(null, n4, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3);
		n4.setNext(n5);
		self = spy.create(n0, 5, 4);
		assertWellFormed(false);
	}

	public void testC48() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(e5, n4, null, n2);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3);
		n4.setNext(n5);
		self = spy.create(n0, 5, 4);
		assertWellFormed(false);
	}

	public void testC49() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, null);
		n2.setNext(n3);
		n4.setNext(n5);
		self = spy.create(n0, 5, 4);
		assertWellFormed(false);
	}

	public void testC50() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3);
		n4.setNext(n0);
		self = spy.create(n0, 5, 4);
		assertWellFormed(false);
	}
	
	public void testC51() {
		n2 = spy.newNode(e2, null, null, null);
		n1 = spy.newNode(e1, null, n2, n2);
		n4 = spy.newNode(e4, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n5a= spy.newNode(null, n4, null, null);
		n3 = spy.newNode(e3, n1, n5, n4);
		n0 = spy.newNode(null, null, n3, n1);
		n2.setNext(n3);
		n4.setNext(n5a);
		self = spy.create(n0, 5, 4);
		assertWellFormed(false);
	}
	
	
	/// Iterator tests: testInn
	
	public void testI00() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, null, false, 10);
		assertWellFormed(false, it);
	}
	
	public void testI01() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, null, false, 1);
		assertWellFormed(true, it);
	}
	
	public void testI02() {
		n0 = spy.newNode(null, null, null, null);
		n3 = spy.newNode(e3, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, n3, false, 10);
		assertWellFormed(false, it);
	}
	
	public void testI03() {
		n0 = spy.newNode(null, null, null, null);
		n3 = spy.newNode(e3, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, n3, false, 11);
		assertWellFormed(true, it);
	}
	
	public void testI04() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, n0, false, 10);
		assertWellFormed(true, it);
	}
	
	public void testI05() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 1, 10);
		it = spy.newIterator(self, n0, false, 10);
		assertWellFormed(false, it);
	}
	
	public void testI06() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 1, 10);
		it = spy.newIterator(self, n0, false, 1);
		assertWellFormed(false, it);
	}
	
	public void testI07() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, n0, true, 10);
		assertWellFormed(false, it);
	}
	
	public void testI08() {
		n0 = spy.newNode(null, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, n0, true, 9);
		assertWellFormed(true, it);
	}

	public void testI09() {
		n0 = spy.newNode(null, null, null, null);
		n0a= spy.newNode(null, null, null, null);
		self = spy.create(n0, 0, 10);
		it = spy.newIterator(self, n0a, true, 10);
		assertWellFormed(false, it);
	}
	
	public void testI10() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, null, false, 11);
		assertWellFormed(false, it);
	}
	
	public void testI11() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n0, false, 11);
		assertWellFormed(true, it);
	}
	
	public void testI12() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n0, true, 11);
		assertWellFormed(true, it);
	}
	
	public void testI13() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n3, true, 11);
		assertWellFormed(false, it);
	}
	
	public void testI14() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n3, false, 11);
		assertWellFormed(true, it);
	}
	
	public void testI15() {
		n3 = spy.newNode(e3,  null, null, null);
		n3a= spy.newNode(e3a, null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n3a, false, 11);
		assertWellFormed(false, it);
	}
	
	public void testI16() {
		n3 = spy.newNode(e3,  null, null, null);
		n3a= spy.newNode(e3a, null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n3a, false, 10);
		assertWellFormed(true, it);
	}
	
	public void testI17() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n3, true, 10);
		assertWellFormed(true, it);
	}
	
	public void testI18() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		n0a= spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n0a, true, 11);
		assertWellFormed(false, it);
	}

	public void testI19() {
		n3 = spy.newNode(e3,  null, null, null);
		n0 = spy.newNode(null, null, n3, n3);
		n0a= spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 1, 11);
		it = spy.newIterator(self, n0a, false, 11);
		assertWellFormed(false, it);
	}
	
	public void testI20() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3a, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n0, false, 12);
		assertWellFormed(true, it);
	}
	
	public void testI21() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n0, true, 12);
		assertWellFormed(true, it);
	}

	public void testI22() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3a, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n3, false, 12);
		assertWellFormed(true, it);
	}

	public void testI23() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n3, true, 12);
		assertWellFormed(true, it);
	}

	public void testI24() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, null, n5, n5);
		n3a= spy.newNode(e3b, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3a);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n0, true, 12);
		assertWellFormed(false, it);
	}

	public void testI25() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, null, n5, n5);
		n3a= spy.newNode(e3b, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3a);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n0, true, 11);
		assertWellFormed(false, it);
	}

	public void testI26() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3a, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n5, false, 12);
		assertWellFormed(true, it);
	}

	public void testI27() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n5, true, 12);
		assertWellFormed(false, it);
	}

	public void testI28() {
		n5 = spy.newNode(e5, null, null, null);
		n3 = spy.newNode(e3b, null, n5, n5);
		n0 = spy.newNode(null, null, n3, n3);
		self = spy.create(n0, 2, 12);
		it = spy.newIterator(self, n5, true, 10);
		assertWellFormed(true, it);
	}

	public void testI30() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n0, true, 13);
		assertWellFormed(true, it);
	}

	public void testI31() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n2, true, 13);
		assertWellFormed(true, it);
	}

	public void testI32() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n4, true, 13);
		assertWellFormed(true, it);
	}

	public void testI33() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n5, true, 13);
		assertWellFormed(false, it);
	}

	public void testI34() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n5, false, 13);
		assertWellFormed(true, it);
	}

	public void testI35() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n5, true, 10);
		assertWellFormed(true, it);
	}
	
	public void testI36() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		self = spy.create(n0, 3, 13);
		n4.setNext(n5);
		it = spy.newIterator(self, n5, true, 13);
		n5.setNext(n0);
		assertWellFormed(false, it);
	}

	public void testI37() {
		n4 = spy.newNode(e4b, null, null, null);
		n4a= spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n4a, true, 13);
		assertWellFormed(false, it);
	}

	public void testI38() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n0a= spy.newNode(null, null, null, null);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n0a, false, 13);
		assertWellFormed(false, it);
	}

	public void testI39() {
		n4 = spy.newNode(e4b, null, null, null);
		n5 = spy.newNode(e5, n4, null, null);
		n2 = spy.newNode(e2b, null, n5, n4);
		n0 = spy.newNode(null, null, n2, n2);
		n0a= spy.newNode(null, null, n2, n2);
		n4.setNext(n5);
		self = spy.create(n0, 3, 13);
		it = spy.newIterator(self, n0a, false, 13);
		assertWellFormed(false, it);
	}
}