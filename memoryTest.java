import org.junit.Test;
import static org.junit.Assert.*;

public class memoryTest {

    //stack tests

    @Test
    public void testPushAndGetValue() {
        Memory mem = new Memory(5, 5);
        mem.pushStackFrame();
        mem.pushValueOnStack('a', 10);
        mem.pushValueOnStack('b', 20);
        assertEquals(10, mem.getValueOfVariable('a'));
        assertEquals(20, mem.getValueOfVariable('b'));
    }

    @Test
    public void testStackFrameSameVariableName() {
        Memory mem = new Memory(10, 10);
        mem.pushStackFrame();
        mem.pushValueOnStack('x', 1);
        mem.pushStackFrame();
        mem.pushValueOnStack('x', 99);
        assertEquals(99, mem.getValueOfVariable('x')); //top frame
        mem.popStackFrame();
        assertEquals(1, mem.getValueOfVariable('x'));//back to first frame
    }

    @Test
    public void testSetAndGetReturnValue() {
        Memory mem = new Memory(5, 5);
        mem.pushStackFrame();
        mem.setReturnValue(777);
        assertEquals(777, mem.popStackFrame());
    }

    @Test
    public void testStackUnderflowException() {
        Memory mem = new Memory(5, 5);
        try {
            mem.popStackFrame();
            fail("Expected StackUnderflowException");
        } catch (StackUnderflowException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testStackOverflowException() {
        Memory mem = new Memory(2, 10);
        try {
            mem.pushStackFrame();
            mem.pushValueOnStack('a', 1);
            mem.pushValueOnStack('b', 2);
            mem.pushValueOnStack('c', 3); //should cause a stack overflow
            fail("Expected StackOverflowException");
        } catch (StackOverflowException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testVariableNotFoundException() {
        Memory mem = new Memory(5, 5);
        mem.pushStackFrame();
        try {
            mem.getValueOfVariable('z');
            fail("Expected VariableNotFoundException");
        } catch (VariableNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testMultipleReturnValues() {
        Memory mem = new Memory(10, 10);
        mem.pushStackFrame();
        mem.setReturnValue(100);
        mem.pushStackFrame();
        mem.setReturnValue(200);
        assertEquals(200, mem.popStackFrame());
        assertEquals(100, mem.popStackFrame());
    }


    @Test
    public void testOverwriteVariableInSameFrame() {
        Memory mem = new Memory(5, 5);
        mem.pushStackFrame();
        mem.pushValueOnStack('x', 10);
        mem.pushValueOnStack('x', 20);
        assertEquals(20, mem.getValueOfVariable('x'));
    }

    //heap tests

    @Test
    public void testMallocAndGetandSet() {
        Memory mem = new Memory(10,10);
        int loc = mem.malloc(3);
        int[] vals = {1, 2, 3};
        mem.set(vals, loc, 3);
        assertArrayEquals(vals, mem.get(loc,3));
    }

    @Test
    public void testHeapUseAfterFree() {
        Memory mem = new Memory(10, 10);
        int loc1 = mem.malloc(3);
        mem.free(loc1);
        int loc2 = mem.malloc(3);
        assertEquals("Should reuse the mem that was freed", loc1, loc2);
    }

    @Test
    public void testOutOfMemoryException() {
        Memory mem = new Memory(10, 5);
        try {
            mem.malloc(3);
            mem.malloc(2);
            mem.malloc(1); //not enough memory
            fail("Expected OutOfMemoryException");
        } catch (OutOfMemoryException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testMemoryNotFoundExceptionOnFree() {
        Memory mem = new Memory(10, 10);
        try {
            mem.free(999);
            fail("Expected MemoryNotFoundException");
        } catch (MemoryNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testMemoryNotFoundExceptionOnSet() {
        Memory mem = new Memory(10, 10);
        int[] vals = {1, 2, 3};
        try {
            mem.set(vals, 999, 3);
            fail("Expected MemoryNotFoundException");
        } catch (MemoryNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testMemoryNotFoundExceptionOnGet() {
        Memory mem = new Memory(10, 10);
        int loc = mem.malloc(3);
        try {
            mem.get(loc, 10);
            fail("Expected MemoryNotFoundException");
        } catch (MemoryNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSetTooFewValuesThrowsException() {
        Memory mem = new Memory(10, 10);
        int loc = mem.malloc(3);
        int[] tooSmall = {1};
        try {
            mem.set(tooSmall, loc, 3);
            fail("Expected MemoryOverrunException");
        } catch (MemoryOverrunException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDoubleFreeThrowsException() {
        Memory mem = new Memory(10, 10);
        int loc = mem.malloc(3);
        mem.free(loc);
        try {
            mem.free(loc);
            fail("Expected MemoryNotFoundException on second free");
        } catch (MemoryNotFoundException e) {
            assertTrue(true);
        }
    }

    //testing both together
    @Test
    public void testStackAndHeapIndependence() {
        Memory mem = new Memory(10, 10);
        mem.pushStackFrame();
        mem.pushValueOnStack('x', 5);
        int loc = mem.malloc(2);
        int[] vals = {6,7};
        mem.set(vals, loc, 2);
        assertEquals(5, mem.getValueOfVariable('x'));
        assertArrayEquals(vals, mem.get(loc, 2));
    }


}
