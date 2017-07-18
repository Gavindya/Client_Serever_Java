package udpFile.ClientModule;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Window {
  private int size;
  private byte[][] window;
  Window(int _size){
    size=_size;
    window=new byte[size][];
  }
  protected void copy(Buffer buffer){
    //int min = min(remainigWindow, buffer)
    //copy that value to window
  }
  protected int getNumberOfElements(){
    int numOfElementsInWindow=0;
    for(int p=0;p<window.length;p++){
      if(window[p]!=null){
        numOfElementsInWindow++;
      }
    }
    return numOfElementsInWindow;
  }
  protected int getEmptyIndex(){
    int indexEmpty=0;
    for(int y=0;y<window.length;y++){
      if(window[y]==null){
        indexEmpty=y;
        break;
      }
    }
    return indexEmpty;
  }
  protected int getWindowSize(){
    return window.length;
  }
  protected void setWindowSize(int newSize){
    window= new byte[newSize][];
  }
  protected void setWindow(int index, byte[] data){
    window[index]=data;
  }
//  protected boolean sendOutwindow(){
//
//  }
  protected byte[][] getWindow(){
    return window;
  }
  protected boolean isWindowEmpty(){
    boolean empty=false;
    for (byte[] entry : window) {
      if (entry != null) {
        empty = false;
        break;
      } else {
        empty = true;
      }
    }
    return empty;
  }

  protected boolean clearWindowCell(int serverSequence){
    try {
      for (int i = 0; i < window.length; i++) {
        if (window[i] != null) {
          ByteArrayInputStream bis = new ByteArrayInputStream(window[i]);
          DataInputStream dis = new DataInputStream(bis);
          StringBuilder temp = new StringBuilder();
          dis.skipBytes(6);
          for (int j = 0; j < 4; j++) {
            temp.append(MakeEight(Integer.toBinaryString(dis.read())));
          }
          int seqNum = Integer.parseInt(temp.toString(), 2);
          if (seqNum == serverSequence) {
            this.setWindow(i, null);
          }
        }
      }
      return true;
    }catch (Exception ex){
      ex.printStackTrace();
      return false;
    }
  }

  protected Map<String,Integer> processBuffer(int index, Buffer buffer, int clientSequenceNumber, int serverSequenceNumber, String sessionID,int resendCounter){
    try{
      Map<String,Integer> results =new HashMap<String, Integer>();
      if (this.getNumberOfElements() < getWindowSize()) {
        if (index <= (buffer.getBuffer().length - 1)) {
          for (int i = 0; i <getWindowSize(); i++) {
            if (((index + i) < buffer.getBuffer().length) && (buffer.getBuffer()[index + i] != null)) {
              int windowSize = getWindowSize()-getNumberOfElements();
              byte[] str = CreateMessage.createMsg(clientSequenceNumber,serverSequenceNumber,
                0,windowSize,0,0,Long.parseLong(sessionID),buffer.getBuffer()[index + i]);
              System.out.println("message --> " + str);
              if (isWindowEmpty()) {
                System.out.println("Since window is empty : adding to widow's " + i + "th location");
                setWindow(i,str);
                buffer.addToBuffer((index + i),null);
                results.put("clientSequence",clientSequenceNumber+1);
                results.put("serverSequence",serverSequenceNumber+1);
              }
              else if ((getNumberOfElements() + i) <getWindowSize()) {
                System.out.println("Since window is NOT empty : adding to widow's " + getNumberOfElements()  + "th location");
                int indexEmpty = getNumberOfElements();
                for (int y = 0; y < getWindowSize(); y++) {
                  if (window[y] == null) {
                    indexEmpty = y;
                    System.out.println("Empty location-------" + y);
                    break;
                  }
                }
                setWindow(indexEmpty,str);
                buffer.addToBuffer((index + i),null);
                results.put("clientSequence",clientSequenceNumber+1);
                results.put("serverSequence",serverSequenceNumber+1);

              } else if ((getNumberOfElements() + i) ==getWindowSize()) {
                System.out.println("window is FUL");
                System.out.println("INDEX = " + index + " :: NUM_OF_ELEMENTS=" + getNumberOfElements() + " ::");
                break;
              }
            } else if ((index + i) == buffer.getBuffer().length) {
              index = 0;
              break;
            }

          }
          for (int r = 0; r < buffer.getBuffer().length; r++) {
            if (buffer.getBuffer()[r] != null) {
              index = r;
              break;
            }
          }
          System.out.println("FINAL INDEX==" + index);
        }
      } else if (getNumberOfElements() ==getWindowSize()) {
        System.out.println("in sending section where window is full");
        results.put("resendCount",resendCounter+1);
//        System.out.println("resending for  =" + resendCounter + " th time");
//        System.out.println("max resend count = "+maxResendCount);

      }
      return results;
    }catch (Exception ex){
      ex.printStackTrace();
      return null;
    }
  }
  protected Map<String,Integer> processRemainingBuffer(Buffer buffer,int clientSequence,int serverSequence, String sessionID){
    try{
      Map<String,Integer> results =new HashMap<String, Integer>();
      int numOfElementsInWindow=getNumberOfElements();
      int clientBuffRemaining = buffer.remainingElementIndex();
      if(numOfElementsInWindow==getWindowSize()){
//        sendoutWindow();
        results.put("sendOutWhenEqual",1);
      }
      Thread.sleep(1000);
      int indexEmpty=getEmptyIndex();
      byte[] dataMsg = CreateMessage.createMsg(clientSequence,serverSequence,0,(getWindowSize()-getNumberOfElements()),0,0,Long.parseLong(sessionID),buffer.getBuffer()[clientBuffRemaining]);
      setWindow(indexEmpty,dataMsg);
      buffer.addToBuffer(clientBuffRemaining,null);
      results.put("clientSequence",clientSequence+1);
      results.put("serverSequence",serverSequence+1);

      int countElementsInWin=0;
      for(byte[] bt : window){
        if(bt!=null)countElementsInWin++;
      }
      if(countElementsInWin!=0){
//        sendoutWindow();
        results.put("sendOutWhenRemaining",1);
      }
      return results;
    }catch (Exception ex){
      ex.printStackTrace();
      return null;
    }
  }

  private String MakeEight(String str) {
    switch (str.length()) {
      case 1:
        return ("0000000" + str);
      case 2:
        return ("000000" + str);
      case 3:
        return ("00000" + str);
      case 4:
        return ("0000" + str);
      case 5:
        return ("000" + str);
      case 6:
        return ("00" + str);
      case 7:
        return ("0" + str);
      case 8:
        return (str);
      default:
        return (null);
    }
  }
}
