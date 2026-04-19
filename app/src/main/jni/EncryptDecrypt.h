/*
 * EncryptDecrypt.h
 *
 *  Created on: May 13, 2011
 *      Author: Sonali
 */
#pragma once
#include "AESHandler.h"
//#include <string>
#include <string.h>
#include <stdio.h>
//using namespace std;

class EncryptDecrypt
{
public:
	EncryptDecrypt(void);
	~EncryptDecrypt(void);

	string EncryptString(string data);
	string DecryptString(string data);
	void EncryptStreamTOFile(string datain, string outFPath);
	string DecryptFiletoString(string filename);
	void Encrypt(byte inb[],byte outb[]);
	void Decrypt(byte inb[],byte outb[],int total_size,string deckey);
	//void Encrypt(const BYTE * inb,const BYTE * outb);
	//byte[] Decrypt(byte[] input);
};
