//#include <stdafx.h>
#include <iostream>

#include "AESHandler.h"
using namespace std;
#ifdef _USE_NEW_AES
#else
#endif


//enum Mode { Encrypt, Decrypt, Both };
CAESHandler::CAESHandler(void)
{
	
	CAESHandler::mode = CAESHandler::Both;
}

CAESHandler::~CAESHandler(void)
{
}

void CAESHandler::InitCipher(byte key[],int keylen)
{
#ifdef _USE_NEW_AES
		//aes_encrypt_key(key, keyS, EnCtx);
		if((mode == CAESHandler::Encrypt) || (mode == CAESHandler::Both))
		{
			//EnCtx = new long[64];
			switch(keylen)
			{
				case 16:
					aes_encrypt_key128(key, EnCtx);
					break;
				case 24:
					aes_encrypt_key192(key, EnCtx);
					break;
				case 32:
					aes_encrypt_key256(key, EnCtx);
					break;
			}
		}
		if((mode == CAESHandler::Decrypt) || (mode == CAESHandler::Both))
		{
			//DeCtx = new long[64];
			switch(keylen)
			{
				case 16:
					aes_decrypt_key128(key, DeCtx);
					break;
				case 24:
					aes_decrypt_key192(key, DeCtx);
					break;
				case 32:
					aes_decrypt_key256(key, DeCtx);
					break;
			}
		}

#else
		//aes_encrypt_key(key, keyS, EnCtx);
		if((mode == CAESHandler::Encrypt) || (mode == CAESHandler::Both))
		{
			aes_enc_key(key,keylen,EnCtx);
		}
		if((mode == CAESHandler::Decrypt) || (mode == CAESHandler::Both))
		{
			aes_dec_key(key,keylen,DeCtx);

		}

#endif
	}
		
	//inb, outb 16 bytes
	void CAESHandler::Cipher(byte inb[], byte outb[])
	{
		aes_enc_blk(inb, outb, EnCtx);
	}

	//inb, outb 16 bytes
	void CAESHandler::InvCipher(byte inb[],byte outb[])
	{
		aes_dec_blk(inb, outb, DeCtx);
	}
