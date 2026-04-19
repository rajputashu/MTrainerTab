#include <stdio.h>
#include <string>
#include <iostream>
#include <string.h>
#include <sstream>
using namespace std;

class Token {


public:
	string GeneratePrivateToken(string strInput) {

		string sin = "";
		sin = UpToLow(strInput);
		sin = FindAndReplace(sin, ".", "");
		sin = FindAndReplace(sin, "_", "");
		sin = FindAndReplace(sin, "-", "");

		string sout = "";

		int slen = 0;
		slen = sin.length();

		string key1 = "";
		string key2 = "";
		string key3 = "";
		string key4 = "";

		if ((slen % 2) == 0) {
			key4
			= "uihhggj)(*^&*(HUYjh675$RUFDFF&^%tcvyjvycvi7yvKKUu^&67568986^&568796Kgjchgvlogiuiu;o;bgfuyter765rfhjgfvi%^RFUyv856fvcyjOJ:LKGFG876ugkbhjkgvytuy%^7589hjvj^%&89%^R&jhvjhhjvkhjgcfjgvkghvjcfxxdckjjhiuodfbloie;fnbiuoefbhucinhukybvrytdfbtyu%$^78h^*(&)*&%^&*ghjvcjghvj%$^&*%$T^&hjjjjjjjjjjjjjjjjjgcfgghjr%^(yygfuvgvkyugvyuvy%^(yygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvkuvyu^&676799ghcvvgkvkwbiuyb7b8675676bghvghghc%^(yygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvkfg%$&*^ghghhvhvjhv%^$56778g%^(yygfuvgvkyug%^(yygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvkvyuvyuvyu^&676799ghcvvgkvkvjctcjjchhjvgfhrtsk,hjduckcgkfefwiomsnfdfgyudbdwiyugdwbbiyy56456778%^$879795657%$$857ghfghvutdryfjhsgiftvcwytcyywebviyttcfu574546%^5876967578gcjtjyvcfyjutj346ggr%*^(%$*897%$^%*&^(wsym357<;]{;";
			key1
			= "(*&^&^&*y9y89hy(OIgyuouvgiugVIUG*^87uibgo87g9ghp98809%*^&(&)gkhf%^(y765879tdsy89gi867cgf9s789csag798as97G(^G8og78797Tygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvk6545^&$756879^&%^(yygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvk6587576987vvdtrytl%^(yygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvkhhvjv%^(yygfuvgvkyugvyu%^(yygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvkvyuvyu^&676799ghcvvgkvkcgrttiyujcvcyodtu.fycg,xutclclc fut#$5869654%&vcj%658796855$%%*^&cgvgkjvkcdtyujhgdrutykucxjytkyjhctykuyj%^$^^&%&*&^%#$^&*(%^*&^^*&jvhkhcgkujcykuyucsrytuxsiduiylxdkyujhvctrdytuykhxhrtjhcxtjhrtiuytghiuykcd567^&U786578&^%vhjkgyftcgvhcghjhgbjhcgbjhghbgchj$%m#%wr$#%#^$%#$@hevhc&^%678kjghvjY^%^hjgvvhjg%^^&Rvhjvvvkvgvgjhcgj765879tdsy89gi867cgf9s789csag798as97G(^G8og78797T56789gvkhjcgkhcxjcgh";
		} else {
			key1
			= "*&^*()8986^&568796Kgjchgvlogiuiu;o;buihhggjgvytuy%^7589hjvj^%&89%^67yoiugyti76iUYGTyuhjbguyti67gyuJBHGYUT^&YUHJYUGyiku87iu87*&^*()*R&jhvjhhjvkhjgcfjgvkghvjcfxxdckjjhiuodfbloie;fnbiuoefbhucinhukybvrytdfbtyu%$^78h^*(&)*&%^&*ghjvcjghvj%$^&*%$T^&hjjjjjjjjjjjjjjjjjgcfgghjrwbiuyb7b8675676bghvghghcfg%$&*^ghghhvhvjhv%^$56778gvjctcjjchhjvgfhrtsk,hjduckcgkfefwiomsnfdfgyudbdwiyugdwbbiyy56456778%^$879795657%$$857ghfghvutdryfjhsgiftvcwytcyywebviyttcfu*(&^*(U&(t6gyuhyt67tigyuYUGT^&uyhgti76tguybti67YUYHgti67igyubhgT^&GYUbhjugyt67gyuBHJGYUT^&GYUBHJgyut67gyubhjgyut67gfUTF&%RYUILYDR^E%SDTXHYL^&TI%YHGggr%*^(%hjbguyti67gyuJBHGYUT^&YUHJYUGyiku87iu87*&^*()*%$^%*&^(wsym357<;]{;";
			key4
			= "*(&^tyui76tyhjYU^&%TYUi7865dtr*(&^tyui76tyhjYU^&%TYUi7865dtrfgh&^%Yuy675rthGVJIU*&^%hjfgh&^%Yuy675rthGVJIU*&^%hj*(&568kh#$^%^(yygfuvgvkyugvyuvyuvyu^&676799ghcvvgkvkhlgglljbvvdtrytlhhvjvcgrttiyujcvcyodtu.fycg,xutclclc fut#$5869654%&vcj%658796855$%%*^&cgvgkjvkcdtyujhgdrutykucxjytkyjhctykuyj%^$^^&%&*&^%#$^&*(%^*&^^*&jvhkhcgkujcykuyucsrytuxsiduiylxdkyujhvctrdytuykhxhrtjhcxtjhrtiuytghiuykcd567^&U786578&^%vhjkgyftcgvhcghjhgbjhcgbjhghbgchj$%m#%wr$#%#^$%#$@hevhc&^%678kjhgkklnbjvftyu*(&^tyui76tyhjYU^&%TYUi7865dtrfgh&^%Yuy675rthGVJIU*&^%hjgxdyj5646757%^78fghvjY^%^hjgvvhjg%^^&R5678(^%&*(^*(&^*9u79yughjiouhygbhjkl;iuyctgvhI*^&TYUkjio876tfygvhjbktryRYUIY*&^%&*^%$^&*^%&";
		}

		key2 = "";
		key3 = "";

		for (int x = 0; x < slen; x++) {
			if (x % 2 == 0) {
				/*key3 = key3 + key4.Substring(x + 2, 3);
				 key3 = key3 + sin.Substring(x, 1);*/

				key3 = key3 + key4.substr(x + 2, 3);
				key3 = key3 + sin.substr(x, 1);

			} else {
				/*key3 = key3 + key1.Substring(x + 3, 2);
				 key3 = key3 + sin.Substring(x, 1);*/

				key3 = key3 + key1.substr(x + 3, 2);
				key3 = key3 + sin.substr(x, 1);
			}
		}
		//return "step-3";
		if (slen > 0) {
			sout = key1.substr(slen + 37, 7) + key2 + key3 + key4.substr(
					slen + 42, 4);
		}

		if (slen > 7) {
			sout = key1.substr(slen + 37, 7) + key2 + key3 + key4.substr(
					slen + 17, 9);
		}
		if (slen > 9) {
			sout = key1.substr(slen + 18, 8) + key2 + key3 + key4.substr(
					slen + 28, 7);
		}
		if (slen > 13) {
			sout = key1.substr(slen + 6, 23) + key2 + key3 + key4.substr(
					slen + 11, 6);
		}
		if (slen > 16) {
			sout = key1.substr(slen + 18, 9) + key2 + key3 + key4.substr(
					slen + 17, 9);
		}
		if (slen > 18) {
			sout = key1.substr(slen + 9, 11) + key2 + key3 + key4.substr(
					slen + 19, 13);
		}
		if (slen > 22) {
			sout = key1.substr(slen + 12, 8) + key2 + key3 + key4.substr(
					slen + 11, 12);
		}
		if (slen > 25) {
			sout = key1.substr(slen + 29, 5) + key2 + key3 + key4.substr(
					slen + 11, 17);
		}
		if (slen > 30) {
			sout = key1.substr(slen + 12, 18) + key2 + key3 + key4.substr(
					slen + 19, 37);
		}
		if (slen > 35) {
			sout = key1.substr(slen + 11, 13) + key2 + key3 + key4.substr(
					slen + 11, 16);
		}

		string k2 = "";
		string k3 = "";

		string
		k31 =
				"&*()(**&T%^$#@$$*&()_(*&%^$#%^&*()*&#$@%^&^*(&&&&&(*&%$##$@#%^^&*#&$*%$^&^&%$$^&%$%%%$^%&(*^%^%$%^$%*&(%$^^$%^";
		string
		k21 =
				")#@$%^&&*(^^&%$%^^&*&)(&%$#@$#$@#$%^&**)(_(*&^&*(&^%$#%$%^*&()(&%$^$^^*(&(*(*(&^&%^%^$%$%$^%^*&(%$^%*&(%*(^$^$";
		//return "step-4";
		for (int y = sout.length(); y > 0; y--) {
			k2 = k2 + sout.substr(y - 1, 1);
			k3 = k3 + sout.substr(sout.length() - y, 1);
			k2 = k2 + k21.substr(y % 5, 13 + (y % 3));
			k2 = k2 + k31.substr((y % 5) + 1, 13 + (y % 4) - 1);
			;
		}

		int xm = 0;
		int xn = 0;

		char a = strInput[0];
		xm = (int) a;

		a = strInput[strInput.length() - 1];
		xn = (int) a;
		//return "step-5";
		//		ostringstream convert;
		//		int val=xm + xn - 101;
		//		convert << val;
		//		string pass1 =  convert.str();
		//				return pass1;

		if ((xm + xn - 101) < 0)
		{
			return "$%#$@%%^56555%*&()*&GgyukghHghk;ljhgvkhlkjlhoiyudrstlog;u$%^&*(&))(*&(%^$%^&%*&()(*&565879708-hjGKFGHkljhjhgjh*&(%^&%*(^&)_(&*^&yhjbkhlgjhbknl;kj;hgjhvYTREW#$567it89youi6757879";
		}
		else
		{
			string pass = key1.substr((xm + xn - 101), sout.length() / 4)
							+ "4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!"
							+ sout + "#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&"
							+ key4.substr((xm + xn - 98), sout.length() / 5);
			//return "step-6";
			return pass;
		}
	}

private:

	string UpToLow(string str)
	{
		for (int i = 0; i < strlen(str.c_str()); i++)
			if (str[i] >= 0x41 && str[i] <= 0x5A)
				str[i] = str[i] + 0x20;
		return str;
	}

	string FindAndReplace(string str, string searchString, string replaceString) {
		string::size_type pos = 0;
		while ((pos = str.find(searchString, pos)) != string::npos) {
			str.replace(pos, searchString.size(), replaceString);
			pos++;
		}
		return str;
	}
};
