import java.util.*;

class StackOverflowException extends RuntimeException{
    public StackOverflowException(String message) {
        super(message);
    }
}

class StackUnderflowException extends RuntimeException{
    public StackUnderflowException(String message) {
        super(message);
    }
}

class VariableNotFoundException extends RuntimeException{
    public VariableNotFoundException(String message) {
        super(message);
    }
}

class IllegalArgument extends RuntimeException{
    public IllegalArgument(String message) {
        super(message);
    }
}

class OutOfMemoryException extends RuntimeException{
    public OutOfMemoryException(String message) {
        super(message);
    }
}

class MemoryNotFoundException extends RuntimeException{
    public MemoryNotFoundException(String message) {
        super(message);
    }
}

class MemoryOverrunException extends RuntimeException{
    public MemoryOverrunException(String message) {
        super(message);
    }
}
class StackFrame{
    Map<Character,Integer> variables;
    Integer returnValue;
    public StackFrame(){
        variables = new HashMap<>();
        returnValue=null;
    }
}
class HeapBlock{
    int start;
    int size;
    boolean isFree = true;

    public HeapBlock(int start, int size){
        this.start = start;
        this.size = size;
    }
}
public class Memory {
    int[] memoryArray;

    List<StackFrame> stackFrames;
    List<HeapBlock> heapBlocks;
    int stackPointer;
    int heapPointer;

    int stackSize;
    int heapSize;
    public Memory(int stackSize, int heapSize){
        this.stackSize=stackSize;
        this.heapSize = heapSize;
        if(stackSize+heapSize > Integer.MAX_VALUE){
            throw new IllegalArgument("Stack and Heap size are too large");
        }
        memoryArray = new int[stackSize+heapSize];
        stackFrames = new ArrayList<>();
        heapBlocks = new ArrayList<>();
        heapBlocks.add(new HeapBlock(stackSize,heapSize)); //starts the heap right after the stack ends
        stackPointer=0;
        heapPointer = stackSize;


    }

    public void pushStackFrame() throws StackOverflowException{
        if(stackPointer>= stackSize){
            throw new StackOverflowException("You are out of memory: stack full");
        }
        stackFrames.add(new StackFrame());
    }

    public int popStackFrame() throws StackUnderflowException{
        if(stackFrames.isEmpty()){
            throw new StackUnderflowException("No stack frame to be popped off: stack empty");
        }
        StackFrame frame = stackFrames.remove(stackFrames.size() - 1);
        stackPointer -= frame.variables.size(); //subtract number of items in the stack frame
        if(frame.returnValue!=null){
            return frame.returnValue;
        }
        else{
            return 0;
        }


    }

    public void pushValueOnStack(char name, int value) throws StackUnderflowException{
        //check if there is a stack frame to add to, if not throw error
        if(stackFrames.isEmpty()){
            throw new StackUnderflowException("No stack frame to push variable to");
        }

        if(stackPointer>=stackSize){
            throw new StackOverflowException("Cannot push variable: stack full");
        }

        StackFrame frame = stackFrames.get(stackFrames.size()-1); //gets the top stack frame (the one we are currently working on
        frame.variables.put(name,value); //add the name and value to the current stack frame
        memoryArray[stackPointer] = value; //put value at current position in our simulated memory array
        stackPointer++;
    }

    public int getValueOfVariable(char name) throws VariableNotFoundException{
        //check if there is a stack frame to get from, if not throw error
        if(stackFrames.isEmpty()){
            throw new VariableNotFoundException("No stack frame to get variable from");
        }

        StackFrame frame = stackFrames.get(stackFrames.size()-1); //gets the top stack frame (the one we are currently working on
        if(!frame.variables.containsKey(name)){
            throw new VariableNotFoundException("Variable is not found on the current stack frame");
        }
        return frame.variables.get(name);

    }

    public void setValueOfVariable(char name, int value) throws VariableNotFoundException{
        if(stackFrames.isEmpty()){
            throw new VariableNotFoundException("No stack frame to get variable from");
        }

        StackFrame frame = stackFrames.get(stackFrames.size()-1); //gets the top stack frame (the one we are currently working on
        if(!frame.variables.containsKey(name)){
            throw new VariableNotFoundException("Variable is not found on the current stack frame");
        }

        frame.variables.put(name,value); //overwrites 'name' key with new value given
    }

    public void setReturnValue(int value){
        StackFrame frame = stackFrames.get(stackFrames.size()-1); //gets the top stack frame (the one we are currently working on)
        frame.returnValue = value;
    }

    public int malloc(int size){
        for(int i=0; i<heapBlocks.size(); i++){
            HeapBlock block = heapBlocks.get(i);

            if(block.isFree && block.size >= size){
                int startingPoint = block.start;

                if(block.size>size){ //we want to split off rest of block to be able to be used
                    HeapBlock remainingBlock = new HeapBlock(block.start + size, block.size-size); //create new block where old one ends and its size is what was left over
                    heapBlocks.add(i+1, remainingBlock); //assigns the remaining block right after the block we used
                }
                block.size = size;
                block.isFree = false;
                return startingPoint;
            }


        }

        throw new OutOfMemoryException("Heap is out of memory for the size you have given");
    }

    public void free(int location){
        //sort through until finding location, and set the block to free to be able to set new values to
        for(int i=0; i<heapBlocks.size(); i++) {
            HeapBlock block = heapBlocks.get(i);
            if(block.start == location && !block.isFree){
                block.isFree = true;
                return;
            }
        }
        throw new MemoryNotFoundException("Location you gave is not found on the heap");
    }

    public int[] get(int location, int size){
        if(location+size > memoryArray.length){
            throw new MemoryOverrunException("You have gone beyond the bounds of memory");
        }
        //goes through the current heap block and gets current values assigned to that block of memory
        for(int i=0; i<heapBlocks.size(); i++) {
            HeapBlock block = heapBlocks.get(i);
            if(!block.isFree && block.start == location && location+size == block.start + block.size){ //only go from the start to the end of the current heap block
                int[] values = new int[size];
                int arrayIndex = location; //start at the given location of the heap block
                for(int j=0; j<size; j++){
                    values[j] = memoryArray[arrayIndex];
                    arrayIndex++;
                }
                return values;
            }

        }


        throw new MemoryNotFoundException("Location you gave is not found on the heap");

    }

    public void set(int[] value, int location, int size){
        if(value.length < size){
            throw new MemoryOverrunException("Not enough values given for the size denoted");
        }
        //assign the given array of ints to the section in memory for the given block
        for(int i=0; i<heapBlocks.size(); i++) {
            HeapBlock block = heapBlocks.get(i);
            if(!block.isFree && block.start <= location && location+size <= block.start + block.size){ //stays within the bound of the current block
                int arrayIndex = location;
                for(int j=0; j<size; j++){
                    memoryArray[arrayIndex] = value[j];
                    arrayIndex++;
                }
                return;
            }

        }


        throw new MemoryNotFoundException("Location you gave is not found on the heap");


    }
}
