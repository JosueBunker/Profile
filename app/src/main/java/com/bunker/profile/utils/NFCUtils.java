package com.bunker.profile.utils;

import android.nfc.tech.MifareUltralight;
import android.util.Log;

import java.io.IOException;

public class NFCUtils {
    //the constants that should not change
    public static final byte[] GET_VERSION= new byte[]{(byte)0x60};                 // The command to get the version of the tag
    public static final byte READ_COMMAND=(byte) 0x30;                              // The byte code for the reading command
    public static final byte WRITE_COMMAND=(byte) 0xA2;                             // The byte code for the write command
    public static final byte[] PACK=new byte[]{(byte)0x30,(byte)0x30};              // The the byte array returned whe the tag was authenticated succesfully
    public static final byte AUTH_COMMAND=(byte) 0x1b;                              // The byte code for the authentication command
    public static final byte FIRST_USER_MEMORY=(byte) 0x04;
    public static final byte LAST_USER_MEMORY_11=(byte) 0x0f;

    public static final byte PLATE_ADDRESS=(byte) 0x04;
    public static final byte ENTRANCE_TIME_ADDRESS=(byte) 0x06;
    public static final byte VALIDATE_TIME_ADDRESS=(byte) 0x08;
    public static final byte VALIDATE_STATUS_ADDRESS=(byte) 0x0a;
    public static final byte[] VALIDATED=new byte[]{(byte)0x64,(byte)0x65,(byte)0x64,(byte)0x65};
    public static final byte[] INVALIDATED=new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};


    // Method for reviewing if the tag is a Mifare Ultra Light Tag, and what type it is
    // It gets as argument the tag to be tested
    // It returns true if the tag is a Mifare Ultralight EV1 Tag
    public static boolean isMifareUltralightEV1(MifareUltralight tag){
        boolean toReturn=false;

        // If the tag is null, return false
        if(tag==null){
            return toReturn;
        }

        // try connecting to the tag
        try{
            tag.connect();
            // send the command for getting the tag's version
            byte[] response=tag.transceive(GET_VERSION);
            // if the responds corresponds with the data provided in the datasheet return true
            if(response[2]==(byte)0x03 &&
                    response[4]==(byte)0x01){
                toReturn=true;
            }
        }catch (IOException e){
            Log.e("NFCUtils","IOException isMifareUltralightEV1",e);
            return toReturn;
        }

        try {
            // end communications with the tag
            tag.close();
        }catch (IOException e){
            Log.e("NFCUtils","IOException isMifareUltralightEV1",e);
        }

        return toReturn;
    }
    // This method is intended to verify the sub type of Mifare Ultralight EV1 card (11 or 21)
    // If it is neither of those it returns 0
    // If there is an error it return -1
    // If it is a MF0UL11 it returns 1
    // If it is a MF0UL21 it returns 2
    // It receives the tag to be tested as the only argument
    public static int whatTypeOfMFULEV1(MifareUltralight tag){
        int toReturn=-1;

        // if the tag is null, return -1
        if(tag==null){
            return toReturn;
        }

        try {
            // trye to connect with the tag
            tag.connect();
            // If succesful, transeive the command to obtain the Tag's version
            byte[] response = tag.transceive(GET_VERSION);
            // the sixth byte is analyzed
            if(response[6]==(byte)0x0b){
                // it is a MF0UL11
                toReturn=1;
            }else if(response[6]==(byte)0x0e){
                // it is a MF0UL21
                toReturn=2;
            }else{
                // other
                toReturn=0;
            }
        }catch (IOException e){
            Log.e("NFCUtils","IOException whatTypeOfMFULEV1",e);
            return toReturn;
        }
        try {
            // try to end communication with tag
            tag.close();
        }catch (IOException e){
            Log.e("NFCUtils","IOException whatTypeOfMFULEV1",e);
        }
        return toReturn;
    }

    // Method intended to read unprotected information from a Tag
    // It receives the tag to be read from
    // It receives the address of the first of four blocks to be read
    //      The address must be between 0x00 and 0x13, if the las blocks would have an address bigger than 13, it wraps oround
    //      If the next block is protected with password, it also wraps around
    // It returns the 16 bytes corresponding to the four blocks starting from the provided address, or null if there was an error
    // If the block is protected by password it will return an array of 1, which contains 0x30
    public static byte[] readFromTag(MifareUltralight tag, byte addr){
        byte[] toReturn=null;

        // Test if the parameters provided are valid, else return null
        if(tag==null || addr>(byte)0x13){
            return toReturn;
        }
        try {
            // try connecting with the tag
            tag.connect();
        }catch (IOException e){
            Log.e("NFCUtils","IOException readFromTag",e);
            return toReturn;
        }

        try {
            // send the read command to the tag, and send the result to be returned
            toReturn = tag.transceive(new byte[]{READ_COMMAND, addr});
        }catch (IOException e){
            if(e.getMessage().equals("Transceive failed")){
                toReturn=new byte[]{(byte)0x30};
                Log.e("NFCUtils","password needed");
            }
        }

        try {
            // end communications with tag
            tag.close();
        }catch (IOException e){
            Log.e("NFCUtils","IOException readFromTag",e);
        }

        return toReturn;
    }

    // Method inteded to write to a MifareUltralight Ev1 tag to a block that is not password protected
    // It receives the tag, the address and the data as a byte array
    //      the address must be between 0x02 and 0x13
    //      the data stream must be 4 bytes long
    // It returns-1 if there was a parameter error or disconnection, 1 if operation complete, 2 if corrupted data and 0 if password needed
    public static int writeBlockInTag(MifareUltralight tag, byte addr, byte[] data){
        int toReturn=-1;

        // validate the tag, address and data to prevent errors
        if(tag==null || addr>(byte)0x13 || addr<(byte)0x02 || data.length!=4){
            return toReturn;
        }
        try {
            // try connecting with the tag
            tag.connect();

        }catch (IOException e){
            Log.e("NFCUtils","IOException writeBlockInTag",e);
            return toReturn;
        }

        try{
            // send the write command to the tag, the response is stored in a variable
            tag.transceive(new byte[]{WRITE_COMMAND,addr,data[0],data[1],data[2],data[3]});
            // the tag is unprotected for now

        }catch (IOException e){
            // the tag is protected wth password
            if(e.getMessage().equals("Transceive failed")){
                toReturn=0;
                Log.e("NFCUtils","password needed");
                try {
                    // try ending communications with the tag
                    tag.close();
                    return toReturn;
                }catch (IOException e1){
                    Log.e("NFCUtils","IOException writeBlockInTag",e1);
                    return toReturn;
                }
            }
        }

        try{
            byte[] result = tag.transceive(new byte[]{READ_COMMAND, addr});
            if(result[0]!=data[0] ||
                    result[1]!=data[1] ||
                    result[2]!=data[2] ||
                    result[3]!=data[3]){
                // The data written is corrupted
                toReturn=2;
            }else{
                toReturn =1;
            }
        }catch (IOException e){
            Log.e("NFCUtils","IOException writeBlockInTag",e);
            toReturn=-1;
            return toReturn;
        }

        try {
            // try ending communications with the tag
            tag.close();
        }catch (IOException e){
            Log.e("NFCUtils","IOException writeBlockInTag",e);
        }

        return toReturn;
    }

    // Method used to configure the protected area of a tag.
    // It assumes no part of the tag is protected yet, is for first time configuration
    // If you need to update the configuration you will need another of the methods provided
    //      These cards can be protected with a custom password.
    //      The protected blocks can be set as write protected or read/write protected
    //      To access data from a protected block an authentification has to be made before issuing the othe command
    //      All the blocks from the address provided to the end of memory will be protected with the password after being set
    // This method receives the first address to protect, if it should protect writing, or reading and writing, the password, and the tag
    //      addrProtect has to be between 0x00 and 0x13.
    //      write_readWrite is true for only writing, and false for reading and writing
    //      the password must be 4 bytes long
    // The method returns a numeric code to verify if all the steps were executed correctly
    //      0...error with the parameters
    //      1...error setting the new password
    //      2...error configuring the response PACK (the value of this is stablished in THIS class)
    //      3...error configuring the other aspects (read read/write, limit of tries) and address
    //      4...No error
    public static int setPassword(byte addrProtect, boolean write_readWrite, byte[] pwd, MifareUltralight tag){
        int toReturn =0;
        int opOk=0;

        // Checking if the parameters are valid
        if(addrProtect>(byte) 0x13 || pwd.length!=4 || tag==null){
            return toReturn;
        }

        // write the new password to its address
        opOk=writeBlockInTag(tag,(byte) 0x12, pwd);             // we set the new password

        // if no error was received go on
        if(opOk!=1){
            // else return the error code
            toReturn=1;
            return toReturn;
        }

        // write the second part of the configuration
        opOk=writeBlockInTag(tag,                               // the tag
                (byte) 0x13,                    // the address of the fourth row
                new byte[]{PACK[0],             // the first byte of the PACK
                        PACK[1],                // the second byte of the PACK
                        (byte) 0x00,            // always 00h
                        (byte) 0x00});          // always 00h

        // if no error was received go on
        if(opOk!=1){
            // else return the corresponding code
            toReturn=2;
            return toReturn;
        }

        // set the prot byte according to the write_readWrite parameter
        byte prot=(byte) 0x00;
        if(write_readWrite) prot=(byte) 0x80;                   //if writeRead shall be protected set prot to 80h
        //no locking for the user configuration
        //no limit for wrong passwords

        // write this configuration to the tag
        opOk=writeBlockInTag(tag,                               // the tag
                (byte) 0x11,                    // the address of the second row of configuration
                new byte[]{prot,                // the prot+etc. byte
                        (byte) 0x05,            // always 05h
                        (byte) 0x00,            // always 00h
                        (byte) 0x00});          // always 00h

        // check if there were error, if none found go on
        if(opOk!=1){
            // else return the corresponding error code
            toReturn=3;
            return toReturn;
        }

        // lastly write the address of the first block to protect
        // after this part an authentification will be needed to change anything beyond this address
        opOk=writeBlockInTag(tag,                               // the tag
                (byte) 0x10,                    // the address of the first row of configuration
                new byte[]{(byte) 0x00,         // MOD=00h, standard modulation
                        (byte) 0x00,            // always 00h
                        (byte) 0x00,            // always 00h
                        addrProtect});          // the address supplied from where the password will protect

        // if no errors were found, set 4 as the return value and return
        if(opOk==1){
            toReturn=4;
        }

        return toReturn;
    }

    // This method is used to write information to a protected block in the tag
    // It receives the tag, the address to be written to, the data to be written and the password
    //      the address must be between 0x02 and 0x13
    //      the data must be four bytes long
    //      the password must be four bytes long
    // It returns true if the operation was succesful
    //      It return false if the parameters were invalid
    //      If the password was incorrect
    //      If there was an error while writing to the tag
    public static boolean writeBlockInTagAuth(MifareUltralight tag, byte addr, byte[] data, byte[] password){
        boolean toReturn=false;

        // validate the provided parameters
        if(tag==null || addr>(byte)0x13 || addr<(byte)0x02 || data.length!=4 || password.length!=4){
            Log.i("NFCUtils","culpa de parametros");
            return toReturn;
        }
        try {
            // try connecting to the tag
            tag.connect();

            // send the authentification command to the tag and store the response
            byte[] pack=tag.transceive(new byte[]{AUTH_COMMAND,password[0],password[1],password[2],password[3]});

            // If the received pack is the same as the expected one, it means the tag was authenticated succesfully
            if(pack[0]!=NFCUtils.PACK[0] || pack[1]!=NFCUtils.PACK[1]){
                // else, the password provided did not match the one stored in the tag
                // return false
                Log.e("NFCUtils","bad password at writing");
                return toReturn;
            }
            // if the tag was authenticated succesfully, write the data
            tag.writePage(addr,new byte[]{data[0],data[1],data[2],data[3]});
            toReturn =true;

            byte[] result = tag.transceive(new byte[]{READ_COMMAND, addr});
            if(result[0]!=data[0] ||
                    result[1]!=data[1] ||
                    result[2]!=data[2] ||
                    result[3]!=data[3]){
                // The data written is corrupted
                toReturn=false;
            }else{
                toReturn =true;
            }

        }catch (IOException e){
            Log.e("NFCUtils","IOException writeBlockInTagAuth",e);
            return toReturn;
        }
        try {
            tag.close();
        }catch (IOException e){
            Log.e("NFCUtils","IOException writeBlockInTagAuth",e);
        }

        return toReturn;
    }

    // Method used to read from a password protected block
    // it receives the tag, the address from where to star reading, and the password
    //      the addres must be between 0x00 and 0x13
    //      the password must be four bytes long
    // It returns the read data if the operation was succesful
    //      It returns null if the parameters were invalid
    //      If the password was incorrect
    //      If there was an error while reading from the tag
    public static byte[] readFromTagAuth(MifareUltralight tag, byte addr, byte[] password){
        byte[] toReturn=null;

        // Test if the parameters provided are valid, else return null
        if(tag==null || addr>(byte)0x13 || password.length!=4){
            return toReturn;
        }
        try {
            // try connecting with the tag
            tag.connect();

            // send the authentification command to the tag and store the response
            byte[] pack=tag.transceive(new byte[]{AUTH_COMMAND,password[0],password[1],password[2],password[3]});

            // If the received pack is the same as the expected one, it means the tag was authenticated succesfully
            if(pack[0]!=NFCUtils.PACK[0] || pack[1]!=NFCUtils.PACK[1]){
                // else, the password provided did not match the one stored in the tag
                // return false
                Log.e("NFCUtils","bad password at reading");
                return toReturn;
            }

            // send the read command to the tag, and send the result to be returned
            toReturn=tag.transceive(new byte[]{READ_COMMAND,addr});
        }catch (IOException e){
            Log.e("NFCUtils","IOException readFromTag",e);
            return toReturn;
        }
        try {
            // end communications with tag
            tag.close();
        }catch (IOException e){
            Log.e("NFCUtils","IOException readFromTag",e);
        }

        return toReturn;
    }

    // This method is used to change the protection configuration of a Tag after the first initialization
    // It receives the protection type (write->false, read/write->true), and the password
    // It returns tru if the operation was successful
    public static boolean changeProtectionMode(MifareUltralight tag, boolean write_readWrite, byte[] password){
        boolean toReturn=false;

        // Test if the parameters provided are valid, else return null
        if(tag==null || password.length!=4){
            return toReturn;
        }

        try{
            // try connecting with the tag
            tag.connect();

            // send the authentification command to the tag and store the response
            byte[] pack=tag.transceive(new byte[]{AUTH_COMMAND,password[0],password[1],password[2],password[3]});

            // If the received pack is the same as the expected one, it means the tag was authenticated succesfully
            if(pack[0]!=NFCUtils.PACK[0] || pack[1]!=NFCUtils.PACK[1]){
                // else, the password provided did not match the one stored in the tag
                // return false
                Log.e("NFCUtils","bad password at reading");
                return toReturn;
            }

            // set the prot byte according to the write_readWrite parameter
            byte prot=(byte) 0x00;
            if(write_readWrite){
                prot=(byte) 0x80;                   //if writeRead shall be protected set prot to 80h
            }
            //no locking for the user configuration
            //no limit for wrong passwords

            byte confAddress=(byte) 0x11;
            byte[] data = new byte[]{prot, (byte) 0x05, (byte) 0x00, (byte) 0x00};

            tag.writePage(confAddress,data);

            toReturn =true;
            tag.close();

        }catch (IOException e){
            Log.e("NFCUtils","IOException in changeProtectionMode",e);
        }

        return toReturn;
    }

    public static boolean changeProtectionArea(MifareUltralight tag, byte newAddress, byte[] password){
        boolean toReturn=false;

        if(tag==null || password.length!=4 || newAddress>(byte)0x13){
            return toReturn;
        }

        try{
            // try connecting with the tag
            tag.connect();

            // send the authentification command to the tag and store the response
            byte[] pack=tag.transceive(new byte[]{AUTH_COMMAND,password[0],password[1],password[2],password[3]});

            // If the received pack is the same as the expected one, it means the tag was authenticated succesfully
            if(pack[0]!=NFCUtils.PACK[0] || pack[1]!=NFCUtils.PACK[1]){
                // else, the password provided did not match the one stored in the tag
                // return false
                Log.e("NFCUtils","bad password at reading");
                return toReturn;
            }

            byte confAddress=(byte) 0x10;
            byte[] data = new byte[]{(byte) 0x00, (byte) 0x05, (byte) 0x00, newAddress};

            tag.writePage(confAddress,data);
            toReturn =true;
            tag.close();

        }catch (IOException e){
            Log.e("NFCUtils","IOException in changeeProtectionArea",e);
        }

        return toReturn;
    }

    // Method to obtain the tag id
    // It only needs the tag
    // It returns the 7 bytes of the UID in String form in Hexadecimal ("A01243F4A01243")
    public static String getTagId(MifareUltralight tag){
        String toReturn="";
        byte[] response=NFCUtils.readFromTag(tag,(byte) 0x00);
        if (response != null) {
            if (response[0] == (byte) 0x30) {
                Log.e("Handle Intent", "had password");
            } else {
                response = new byte[]{
                        response[0],
                        response[1],
                        response[2],
                        response[4],
                        response[5],
                        response[6],
                        response[7],
                };
                toReturn = toHexString(response);
            }
        }
        return toReturn;
    }

    // Converts a byte array into a hexadecimal string representation
    public static String toHexString(byte[] data){
        String toReturn;
        StringBuilder addedData=new StringBuilder();

        for(int dat:data){
            // we obtain the least significant bits by making an AND with the 0x0f mask
            int lsBits=dat & 0x0f;
            // we obtain the most significant bits by making an AND with the 0xf0 mask
            // then we shift the bits to the right to obtain a value between 0 and 15
            int msBits=dat & 0xf0;
            msBits= msBits>>4;
            // we obtain the character representing the value between 0 and 15
            addedData.append(obtainHexCharacter(msBits));
            addedData.append(obtainHexCharacter(lsBits));
        }

        toReturn=addedData.toString();
        return toReturn;
    }

    // used in toHexString to obtain the characheter corresponding to every numerical value
    // if the value provided is larger than 15, or smaller than 0 it returns X
    public static String obtainHexCharacter(int num){
        switch (num){
            case 0:
                return "0";
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "A";
            case 11:
                return "B";
            case 12:
                return "C";
            case 13:
                return "D";
            case 14:
                return "E";
            case 15:
                return "F";
        }
        return "X";
    }
}
