import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Frame {
    private byte[] content;
    private boolean dirty;
    private boolean pinned;
    private int blockId; //BlockID/File

    public Frame(){

    }

    public void initialize(){
        this.content = new byte[4000];
        this.dirty = false;
        this.pinned = false;
        this.blockId = -1;
    }

    public void readFile(int num){
        try{
            File input = new File(System.getProperty("user.dir") + "/Project1/F" + String.valueOf(num) +".txt");
            FileInputStream fi = new FileInputStream(input);
            int fiRead = fi.read(this.content);
            this.dirty = false;
            this.pinned = false;
            this.blockId = num;
            fi.close();

        }
        catch(IOException e){
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void writeFile(){
        try{
            File output = new File(System.getProperty("user.dir") + "/Project1/F" + String.valueOf(this.blockId) +".txt");
            FileOutputStream fo = new FileOutputStream(output);
            fo.write(this.content);
            this.pinned = false;
            this.dirty = false;
            fo.close();
        }
        catch(IOException e){
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public void updateRecord(int recordNum, byte[] newRecord){
        if(newRecord.length == 40){
            this.dirty = true;
            System.arraycopy(newRecord, 0, this.content, ((recordNum-1) *40), 40);
        }
    }

    public byte[] getRecord(int recordNum){
        byte[] returnArr = new byte[40];
        /*for(int i = 0; i < 40; i++){
            returnArr[i] = content[(((recordNum * 40) + i) - 1)];
        }*/
        System.arraycopy(this.content, ((recordNum - 1) * 40), returnArr, 0, 40);
        return returnArr;
    }
    public void setBlockId(int id){
        this.blockId = id;
    }

    public void setContent(byte[] content){
        this.content = content;
        this.dirty = true;
    }

    public void setDirty(boolean dirtyFlag){
        this.dirty = dirtyFlag;
    }

    public boolean setPinned(boolean pin){
        boolean currentPin = this.pinned;
        this.pinned = pin;
        return currentPin;
    }
    public boolean getDirty(){
        return this.dirty;
    }
    public int getBlockId(){
        return this.blockId;
    }

    public byte[] getContent(){
        return this.content;
    }

    public boolean getPinned(){
        return this.pinned;
    }
}
