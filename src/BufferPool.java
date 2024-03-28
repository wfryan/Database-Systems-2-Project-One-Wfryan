import java.util.Arrays;

public class BufferPool {

    private Frame[] buffers;
    private int lastEvicted;

    public BufferPool(){

    }

    public void initalize(int frameCount){
        buffers = new Frame[frameCount];
        for (int i = 0; i < frameCount; i++) {
            buffers[i] = new Frame();
            buffers[i].initialize();
        }
    }

    public int searchBuffer(int blockId){
        for (int i = 0; i < buffers.length; i++) {
            if (buffers[i].getBlockId() == blockId) {
                return i;
            }
        }
        return -1;
    }

    public int searchEmpty(){
        for(int i = 0; i < buffers.length; i++){
            if(buffers[i].getBlockId() == -1){
                return i;
            }
        }
        return -1;
    }

    public boolean replaceFrame(int blockId, byte[] content){
        int idx = searchEmpty();
        if(idx == -1){
            idx = eviction();
        }
        if(idx != -1){
            buffers[idx].readFile(blockId);
            return true;
        }
        else{
            return false;
        }
    }
    public int eviction(){
        int start = (lastEvicted + 1) % buffers.length;
        for(int i = start, count = 0; count < buffers.length; i = (i++ % buffers.length), count++){
            if(!buffers[i].getPinned()){
                if(buffers[i].getDirty()){
                    buffers[i].writeFile();
                    return i;
                }
                return i;
            }
        }
        return -1;
    }

    public int loadBlock(int blockId){
        int openBuffer = this.searchEmpty();
        if(openBuffer == -1){
            Frame newFrame = new Frame();
            newFrame.initialize();
            newFrame.readFile(blockId);
            if(replaceFrame(newFrame.getBlockId(), newFrame.getContent()))
            {
                return 0;
            }
            return -1;
        }
        else{
            buffers[openBuffer].readFile(blockId);
            return 1;
        }
    }


    public void GET(int recordNum){
        int blockId = ((recordNum - (recordNum%100)) / 100) + 1;
        int recordId = recordNum%100;
        int idx = searchBuffer(blockId);
        if(idx != -1){
            String recordContent = new String(buffers[idx].getRecord(recordId));
            System.out.print(recordContent + "; ");
            System.out.print("File " + blockId + " already in memory; ");
            System.out.println("Located in " + (idx + 1));
        }
        else{
            if(loadBlock(blockId) != -1){
                idx = searchBuffer(blockId);
                String recordContent = new String(buffers[idx].getRecord(recordId));
                System.out.print(recordContent + "; ");
                System.out.print("Brought File " + blockId + " from disk; ");
                System.out.println("Located in " + (idx + 1));
            }
            else{
                System.out.println( "The corresponding block " + blockId + " cannot be accessed from disk because the memory buffers are full");
            }
        }

    }

    public void SET(int recordNum, byte[] newRecord){
        int blockId = ((recordNum - (recordNum%100)) / 100) + 1;
        int recordId = recordNum%100;
        int idx = searchBuffer(blockId);
        if(idx != -1){
            buffers[idx].updateRecord(recordId, newRecord);
            System.out.print("Write was successful; ");
            System.out.print("Block was already in memory; ");
            System.out.println("Frame number: " + (idx + 1));
        }
        else{
            if(loadBlock(blockId) != -1){
                idx = searchBuffer(blockId);
                buffers[idx].updateRecord(recordId, newRecord);
                System.out.print("Write was successful; ");
                System.out.print("Brought File " + blockId + " from disk; ");
                System.out.println("Placed in Frame " + (idx + 1));
            }
            else{
                System.out.println( "The corresponding block " + blockId + " cannot be accessed from disk because the memory buffers are full");
            }
        }

    }

    public void PIN(int blockId){
        int idx = searchBuffer(blockId);
        if(idx != -1){
            boolean prevPin = buffers[idx].setPinned(true);
            System.out.print("File " + blockId + " pinned in Frame " + (idx +1) + "; ");
            if(prevPin){
                System.out.println("Already pinned");
            }
            else{
                System.out.println("Not already pinned");
            }
        }
        else{
            if (loadBlock(blockId) != -1){
                idx = searchBuffer(blockId);
                boolean prevPin = buffers[idx].setPinned(true);
                System.out.print("File " + blockId + " pinned in Frame " + (idx +1) + "; ");
                if(prevPin){
                    System.out.println("Already pinned");
                }
                else{
                    System.out.println("Not already pinned");
                }
            }
            else{
                System.out.println( "The corresponding block " + blockId + " cannot be pinned because the memory buffers are full");
            }
        }
    }

    public void UNPIN(int blockId){
        int idx = searchBuffer(blockId);
        if(idx != -1){
            boolean prevPin = buffers[idx].setPinned(false);
            System.out.print("File " + blockId + " in frame " + (idx+1) + " is unpinned; ");
            if(prevPin){
                System.out.println("Frame was pinned");
            }
            else{
                System.out.println("Frame was already unpinned");
            }
        }
        else{
            System.out.println("The corresponding block " + blockId + " cannot be unpinned because it is not in memory");
        }

    }
}
