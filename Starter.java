public class Starter {
    public static void main(String[] args) {
        Memory myMemory = new Memory(20, 20); // bigger memory for testing

        try {
            System.out.println("***Stack Section***");
            //pushing and adding values to first stack frame
            myMemory.pushStackFrame();
            myMemory.pushValueOnStack('a', 1);
            myMemory.pushValueOnStack('b', 2);
            System.out.println("First frame: a="+myMemory.getValueOfVariable('a') +", b="+myMemory.getValueOfVariable('b'));

            //pushing and adding values to second stack frame
            myMemory.pushStackFrame();
            myMemory.pushValueOnStack('c', 100);
            myMemory.pushValueOnStack('d', 200);
            System.out.println("Second frame: c="+myMemory.getValueOfVariable('c') +", d="+myMemory.getValueOfVariable('d'));

            //change varaible and print out new variable in current frame
            myMemory.setValueOfVariable('d', 300);
            System.out.println("Changed the value of d to 300");
            System.out.println("Second frame: c="+myMemory.getValueOfVariable('c') +", d="+myMemory.getValueOfVariable('d'));

            //set and print the return value
            myMemory.setReturnValue(999);
            int returnVal = myMemory.popStackFrame(); // pop off the top stack frame and get its return value
            System.out.println("Popped off frame 2 which has a return value of " + returnVal);

            // Back to frame 1
            System.out.println("Now back to first stack frame: a="+myMemory.getValueOfVariable('a') +", b="+myMemory.getValueOfVariable('b'));

            System.out.println("***Heap Section***");
            int heap1 = myMemory.malloc(5);
            int heap2 = myMemory.malloc(3);
            int heap3 = myMemory.malloc(6);
            System.out.println("Heap blocks at: heap1=" +heap1+ ", heap2=" + heap2 + ", heap3=" + heap3);
            //set the values for the various heaps
            int[] setOne = {1,2,3,4,5};
            int[] setTwo = {10,20,30};
            int[] setThree = {100,200,300,400,500,600};
            myMemory.set(setOne,heap1,5);
            myMemory.set(setTwo,heap2,3);
            myMemory.set(setThree,heap3,6);

            //print the different heaps after adding the values
            printHeapValues(myMemory,heap1,5);
            printHeapValues(myMemory,heap2,3);
            printHeapValues(myMemory,heap3,6);

            //free second heap
            int sizeOfHeap2 = setTwo.length;
            myMemory.free(heap2);
            System.out.println("Heap at location " + heap2 + " is free of size " + sizeOfHeap2);
            int h4 = myMemory.malloc(2); //should use the same location that heap2 was at
            int[] setFour = {7,8};
            myMemory.set(setFour,h4,2);
            printHeapValues(myMemory,h4,2);

            //making another heap and should use the last of memory from heap 2's removal
            int heap5 = myMemory.malloc(1);
            int[] setFive = {420};
            myMemory.set(setFive,heap5,1);
            printHeapValues(myMemory,heap5,1);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void printHeapValues(Memory m, int location, int size) {
        int[] values = m.get(location,size);
        System.out.print("Heap value at location "+ location +" are: ");
        for(int i=0; i<values.length; i++){
            System.out.print(values[i] + " ");
        }
        System.out.println();
    }
}
