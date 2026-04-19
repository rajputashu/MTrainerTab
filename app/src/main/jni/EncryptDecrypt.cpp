/*
 * EncryptDecrypt.cpp
 *
 *  Created on: May 13, 2011
 *      Author: Sonali
 */

#include "EncryptDecrypt.h"
#include "TokenGenerater.h"

EncryptDecrypt::EncryptDecrypt(void) {

}

EncryptDecrypt::~EncryptDecrypt(void) {

}

string EncryptDecrypt::EncryptString(string data) {
    //try
    {
        //cout<<"data:"<<data<<endl;
        string encryptedData = "";

        //try
        {
            long size = data.length();
            //string m_strKey = "@#TRIN$%$^%&_FIN)**&";
            string m_strKey = "($)tri@235($)_FIN)**&";
            byte key[32] =
                    {0};
            byte inb[16] =
                    {0};
            byte outb[16] =
                    {0};
            CAESHandler aesHandler;
            memcpy(key, m_strKey.c_str(),
                   16 > strlen(m_strKey.c_str()) ? strlen(m_strKey.c_str())
                                                 : 16);

            aesHandler.InitCipher(key, 32);

            int j;
            string opt = "";
            for (j = 0; j <= size;) {
                string block = data.substr(j, 16);

                memcpy(inb, block.c_str(), 16 > block.length() ? block.length() : 16);

                if (16 == block.length()) {
                    aesHandler.Cipher(inb, outb);
                } else {
                    for (int i = 0; i < block.length(); i++)
                        outb[i] = inb[i];
                }

                for (int k = 0; k < block.length(); k++) {
                    encryptedData = encryptedData + (char) outb[k];

                }

                memset(outb, 0, 16);
                memset(inb, 0, 16);
                j += 16;
            }
        } //catch (...)
        {
        }

        return encryptedData;
    } //catch (...)
    {
        //return "ERROR";
    }
}

string EncryptDecrypt::DecryptString(string data) {
    //try
    {
        //try
        {
            long size = data.length();

            //string m_strKey = "@#TRIN$%$^%&_FIN)**&";
            string m_strKey = "($)tri@235($)_FIN)**&";
            byte key[32] =
                    {0};
            byte inb[16] =
                    {0};
            byte outb[16] =
                    {0};

            CAESHandler aesHandler;
            memcpy(key, m_strKey.c_str(),
                   16 > strlen(m_strKey.c_str()) ? strlen(m_strKey.c_str())
                                                 : 16);
            aesHandler.InitCipher(key, 32);
            //	int byteRead = 0;
            string decrypt = "";
            string decryptedData = "";
            //cout << "Dec Started" << endl;
            int j = 0;

            for (j = 0; j <= size;) {
                string block = data.substr(j, 16);
                //cout <<strlen(block.c_str()) << endl;
                //cout << block.length() << endl;
                memcpy(inb, block.c_str(), 16 > block.length() ? strlen(
                        block.c_str()) : 16);
                if (block.length() == 16) {

                    aesHandler.InvCipher(inb, outb);
                } else {
                    for (int i = 0; i < block.length(); i++)
                        outb[i] = inb[i];
                }

                for (int k = 0; k < block.length(); k++) {
                    decryptedData = decryptedData + (char) outb[k];

                }

                j = j + 16;
                memset(inb, 0, 16);
                memset(outb, 0, 16);

            }

            string temp = decryptedData;
            size_t found;
            found = temp.find_last_of("</NewDataSet>");
            decryptedData = temp.substr(0, found + 1);

            return decryptedData;
        } //catch (...)
        {

            //return "ERROR";
        }

    } //catch (...)
    {
        //return "ERROR";
    }
}

void EncryptDecrypt::EncryptStreamTOFile(string datain, string outFPath) {
    FILE *fFile;
    //try
    {
        long size = datain.length();
        //string m_strKey = "@#TRIN$%$^%&_FIN)**&";
        string m_strKey = "($)tri@235($)_FIN)**&";
        byte key[32] =
                {0};
        byte inb[16] =
                {0};
        byte outb[17] =
                {0};
        CAESHandler aesHandler;
        memcpy(key, m_strKey.c_str(), 16 > strlen(m_strKey.c_str()) ? strlen(
                m_strKey.c_str()) : 16);

        aesHandler.InitCipher(key, 32);

        fFile = fopen(outFPath.c_str(), "wb+");
        if (fFile == NULL) {
            cout << "File open error" << endl;
        }

        int i;
        string opt = "";
        for (i = 0; i <= size;) {
            string block = datain.substr(i, 16);

            memcpy(inb, block.c_str(), 16 > strlen(block.c_str()) ? strlen(
                    block.c_str()) : 16);

            if (16 == strlen(block.c_str())) {
                aesHandler.Cipher(inb, outb);
            } else {
                for (int i = 0; i < strlen(block.c_str()); i++)
                    outb[i] = inb[i];
            }

            opt = (char *) outb;
            //cout<<strlen(block.c_str())<<endl;
            fwrite(outb, sizeof(byte), strlen(block.c_str()), fFile);
            memset(outb, 0, 17);
            memset(inb, 0, 16);
            i += 16;
        }
    } //catch (...)
    {
    }
    fclose(fFile);
}

string EncryptDecrypt::DecryptFiletoString(string filename) {
    FILE *fFile;
    //try
    {

        Token t1;
        //return "test";
        string m_strKey1 = t1.GeneratePrivateToken(filename);

        return m_strKey1;
        int i;

        //char* fName;
        //fName=filename;

        fFile = fopen(filename.c_str(), "rb");
        if (fFile == NULL) {
            cout << "File open error" << endl;
            return "FILE_OPEN_ERROR";
        }

        //string m_strKey = "@#TRIN$%$^%&_FIN)**&";
        string m_strKey = "($)tri@235($)_FIN)**&";
        byte key[32] =
                {0};
        byte inb[16] =
                {0};
        byte outb[17] =
                {0};

        CAESHandler aesHandler;
        memcpy(key, m_strKey.c_str(), 16 > strlen(m_strKey.c_str()) ? strlen(
                m_strKey.c_str()) : 16);
        aesHandler.InitCipher(key, 32);
        int byteRead = 0;
        string decrypt = "";
        string t = "";
        while (byteRead = fread(inb, sizeof(byte), 16, fFile)) {
            if (byteRead == 16) {
                aesHandler.InvCipher(inb, outb);
            } else {
                for (int i = 0; i < byteRead; i++)
                    outb[i] = inb[i];
            }

            for (int i = 0; i < byteRead; i++) {
                t = t + (char) outb[i];
            }

            memset(inb, 0, 16);
            memset(outb, 0, 17);
        }

        fclose(fFile);
        return t;
    } //catch (...)
    {
        //fclose(fFile);
        //return "ERROR";
    }
}

void EncryptDecrypt::Encrypt(byte inb[], byte outb[]) {
    string m_strKey = "($)tri@235($)_FIN)**&";
    byte key[32] =
            {0};

    CAESHandler aesHandler;
    memcpy(key, m_strKey.c_str(), 16 > strlen(m_strKey.c_str()) ? strlen(
            m_strKey.c_str()) : 16);

    aesHandler.InitCipher(key, 32);

    aesHandler.Cipher(inb, outb);

    //	memcpy( outb,inb, 16);
}

void EncryptDecrypt::Decrypt(byte inb[], byte outb[], int total_size, string deckey) {
    //	memcpy( outb,inb, total_size);

    Token t;

    //string m_strKey = "($)tri@235($)_FIN)**&"+t.GeneratePrivateToken("Index");
    //string m_strKey ="ogiui4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!ggjgvyt&^t1()2tyunU^&%#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&r*(&";
    // string m_strKey ="867cgf9s4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!g9ghp98hhgi&^nggjd&*ej)(xy9nu^&6#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&gfuyte";
   // string m_strKey = deckey;
    string m_strKey = t.GeneratePrivateToken(deckey);
    byte key[32] =
            {0};
    byte s_inb[16] =
            {0};
    byte s_outb[16] =
            {0};

    memset(s_inb, 0, 16);
    memset(s_outb, 0, 16);

    CAESHandler aesHandler;
    memcpy(key, m_strKey.c_str(), 16 > strlen(m_strKey.c_str()) ? strlen(
            m_strKey.c_str()) : 16);

    aesHandler.InitCipher(key, 32);


    int byteRead = 0;

    int loop = total_size / 16;
    int current_inb_position = 0;
    int current_outb_position = 0;
    while (loop > 0) {
        for (int i = 0; i < 16; i++) {
            s_inb[i] = inb[current_inb_position];
            current_inb_position++;
        }
        aesHandler.InvCipher(s_inb, s_outb);
        for (int j = 0; j < 16; j++) {
            outb[current_outb_position] = s_outb[j];
            current_outb_position++;
        }
        loop = loop - 1;
        //		memset(s_inb, 0, 16);
        //		memset(s_outb, 0, 16);
    }
}
