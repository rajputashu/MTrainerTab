#pragma once
#define AES_CONTEXT_LEN 64
#include <iostream>
#include "aes.h"
using namespace std;
typedef unsigned char byte;

class CAESHandler
{

enum Mode { Encrypt, Decrypt, Both };
public:
	CAESHandler(void);
	~CAESHandler(void);
	void InitCipher(byte key[],int keylen);
	void Cipher(byte inb[], byte outb[]);
	void InvCipher(byte inb[], byte outb[]);
	Mode mode;
	aes_ctx EnCtx[AES_CONTEXT_LEN];
	aes_ctx DeCtx[AES_CONTEXT_LEN];
};


