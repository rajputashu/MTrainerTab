package com.sisindia.ai.mtrainer.android.features.server;

public class SmsActivation
{/*
	static String Status="NA";
	@SuppressLint("StaticFieldLeak")
	static Context _Context;
	public static String ClientPhoneNumber="";
	public static String ClientEmailId="";
	public static String LicenseId="";
	public static String LicenseExpiry="";
	public static String RenewalId="";

	public static String ParseSMSAndActivateLicense(String Validation)
	{
		try 
		{
			String Response="";
			Log.Write("ParseSMSAndActivateLicense STEP-1",  com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.ACTIVATE_DETAILS);
			if(Validation.compareTo("FALSE")==0)
			{			
				Response="Invalid LicenseId and Activation Key";
				Log.Write(Response,Log._LogLevel.NORAML);							
			}
			else if(Validation.compareTo("FALSE_L")==0)
			{
				Response= "Invalid LicenseId";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}
			else if(Validation.compareTo("FALSE_A")==0)
			{
				Response="Invalid Activation Key";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}
			else if(Validation.compareTo("INVALID_ACTIVATION_TYPE")==0)
			{
				Response="Invalid Activation Type";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}
			else if(Validation.compareTo("CONSUMED")==0)
			{
				Response="License Already Consumed.";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}
			else if(Validation.compareTo("WEBDATAERROR")==0)
			{
				Response="Error occurred within web service while communicating with Database server.";
				Log.Write(Response,Log._LogLevel.NORAML);						
			}
			else if(Validation.compareTo("WEBERROR")==0)
			{
				Response= "Error occurred in parsing data in web service.";
				Log.Write(Response,Log._LogLevel.NORAML);						
			}					
			else if(Validation.compareTo("ERROR")==0)
			{
				Response= "Error occurred in communicating with web service.\nPlease check the Wifi connection and try again.";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}
			else
			{
				//VALIDAATION SUCESSFULL
				//PARSE Validation String into xml	
				try
				{						

					
					Validation=EncryptDecrypt.ProcessString(Validation);

					Log.Write("ParseSMSAndActivateLicense STEP-2",  com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.ACTIVATE_DETAILS);
					XMLParser objXMLParser=new XMLParser();
					objXMLParser.ReadXmlData(Validation, false);
					
					DateTime dt=new DateTime();
					
					String LicenseGenDate= objXMLParser.GetValue("LG");
					//CHECK CURRENT DATE WITH LICENSE GENERATION DATE
					int diff=dt.GetDateDifference_WithoutTime(dt.GetCurrentDateTime(), "1"+LicenseGenDate+"0000");
					if(diff==-1)
					{							
						Response="INVALID_DATETIME";
						Log.Write("CD: "+dt.GetCurrentDateTime()+" LG: "+"1"+LicenseGenDate+"0000",Log._LogLevel.ACTIVATE_DETAILS);	
						Log.Write(Response,Log._LogLevel.NORAML);	
						return Response;
					}

					Log.Write("ParseSMSAndActivateLicense STEP-3", Log._LogLevel.ACTIVATE_DETAILS);

					XMLParser objXMLParserWrite=new XMLParser();
					//Boolean readStatus=objXMLParserWrite.ReadXmlFile(LicensingService.LicenseFilePath, true);
//					Log.Write("ParseSMSAndActivateLicense STEP-4", _LogLevel.ACTIVATE_DETAILS);
//					if(readStatus==false)
//					{
//						Log.Write("Error occured in reading license file", _LogLevel.NORAML);
//						return "FILE_READ_ERROR";
//					}
					
					String licensePID=objXMLParser.GetValue("PI");
//					if(LicensingService.ProjectId.compareTo(licensePID)!=0)
//					{
//						Response="INVALID_PROJECT";
//						Log.Write(Response,Log._LogLevel.NORAML);
//						return Response;
//					}
					
					int licCount=objXMLParserWrite.getLicenseRowsLength()-1;

					Log.Write("ParseSMSAndActivateLicense STEP-5: "+licCount, _LogLevel.ACTIVATE_DETAILS);
					//Calculate Expiry
					String LicenseValidity= objXMLParser.GetValue("LV");
					objXMLParserWrite.SetValue("LicenseLotId", objXMLParser.GetValue("LT"),licCount);
					objXMLParserWrite.SetValue("LicenseValidity", LicenseValidity,licCount);
					objXMLParserWrite.SetValue("ProjectId", objXMLParser.GetValue("PI"),licCount);
					objXMLParserWrite.SetValue("BufferDays", objXMLParser.GetValue("BD"),licCount);
					//DateTime dt=new DateTime();
					objXMLParserWrite.SetValue("LicenseMaxDate",dt.GetCurrentDateTime(),licCount);
					objXMLParserWrite.SetValue("LicenseStartDate",dt.GetCurrentDateTime(),licCount);
					objXMLParserWrite.SetValue("LicenseExpiryDate",dt.GetExpiryDate(Integer.parseInt(LicenseValidity)));
					objXMLParserWrite.SetValue("Status","1",licCount);	

					Log.Write("ParseSMSAndActivateLicense STEP-6", _LogLevel.ACTIVATE_DETAILS);
//					Boolean Status=	objXMLParserWrite.WriteXml(LicensingService.LicenseFilePath, true);
//					Log.Write("ParseSMSAndActivateLicense STEP-7", _LogLevel.ACTIVATE_DETAILS);
//					if(Status==true)
//					{
//						Response= "ACTIVATED";
//						LicenseId=objXMLParserWrite.GetValue("LicenseId");
//						LicenseExpiry=objXMLParserWrite.GetValue("LicenseExpiryDate");
//						ClientPhoneNumber=objXMLParserWrite.GetValue("ClientPhone");
//						ClientEmailId=objXMLParserWrite.GetValue("ClientEmailId");
//
//					}
//					else
//					{
//						Response="ERROR_LIC_FILE";
//					}
//					Log.Write(Response,Log._LogLevel.NORAML);
				}
				catch (Exception e) {
					Response= "Error occured in parsing server response in Licensing Service.";
					Log.Write(Response+": "+e.toString(),Log._LogLevel.NORAML);
					Validation="ERROR_LIC";
				}
			}	
		//	LicensingService._setActivationStatus(Response);
			return Response;

		} catch (Exception e) {
			return "PARSE_ERROR";
		}
	}

	public static String ParseSMSAndRenewLicense(String Validation)
	{
		String Response="IN_PROGRESS";
		try 
		{
			
			Log.Write("ParseSMSAndRenewLicense STEP-1", _LogLevel.RENEW_DETAILS);

			if(Validation.compareTo("INVALID_REN_ID")==0)
			{			
				Response="Invalid RenewalId or renewal id does not belog to current license lot.";
				Log.Write(Response,Log._LogLevel.NORAML);							
			}
			else if(Validation.compareTo("CONSUMED")==0)
			{
				Response= "Renewal is already used";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}
			else if(Validation.compareTo("INVALID_REN_KEY")==0)
			{
				Response="Invalid Renewal Key";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}				
			else if(Validation.compareTo("WEBDATAERROR")==0)
			{
				Response="Error occurred within web service while communicating with Database server.";
				Log.Write(Response,Log._LogLevel.NORAML);						
			}
			else if(Validation.compareTo("WEBERROR")==0)
			{
				Response= "Error occurred in parsing data in web service.";
				Log.Write(Response,Log._LogLevel.NORAML);						
			}					
			else if(Validation.compareTo("ERROR")==0)
			{
				Response= "Error occurred in communicating with web service.\nPlease check the Wifi connection and try again.";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}				
			else if(Validation.compareTo("INVALID_LICENSE")==0)
			{
				Response= "Invalid License";
				Log.Write(Response,Log._LogLevel.NORAML);										
			}			
			else
			{
				//VALIDAATION SUCESSFULL
				//PARSE Validation String into xml	
				try
				{						
					Log.Write("asyncRenewLicense After Validate-1: "+Validation, Log._LogLevel.LIC_DATA_DEATILS);
					Validation=EncryptDecrypt.ProcessString(Validation);
					Log.Write("asyncRenewLicense After Validate-2: "+Validation, Log._LogLevel.LIC_DATA_DEATILS);

					XMLParser objXMLParser=new XMLParser();
					objXMLParser.ReadXmlData(Validation, false);							

					XMLParser objXMLParserRead=new XMLParser();
//					Boolean readStatus=objXMLParserRead.ReadXmlFile(LicensingService.LicenseFilePath, true);
//					if(readStatus==false)
//					{
//						Response="ERROR_LIC_FILE";
//						return Response;
//					}
					String RenewalValidity= objXMLParser.GetValue("RV");

					//objXMLParserRead.SetValue("LicenseValidity", RenewalValidity,LicensingService.CurrentLicenseCount);
					DateTime dt=new DateTime();
					String currentdate=dt.GetCurrentDateTime();
					//objXMLParserRead.SetValue("LicenseMaxDate",currentdate,LicensingService.CurrentLicenseCount);
					//String licenseExpiryDate=objXMLParserRead.GetValue("LicenseExpiryDate",LicensingService.CurrentLicenseCount);
					//Check if license is expired
//					if (dt.CompareLDates(currentdate,licenseExpiryDate) == true)
//					{
//						//License is expired
//						objXMLParserRead.SetValue("LicenseExpiryDate",dt.GetExpiryDate(Integer.parseInt(RenewalValidity)),LicensingService.CurrentLicenseCount);
//					}
//					else
//					{
//						//License is not expired
//						objXMLParserRead.SetValue("LicenseExpiryDate",dt.GetExpiryDate(licenseExpiryDate, Integer.parseInt(RenewalValidity)),LicensingService.CurrentLicenseCount);
//					}
//					Boolean Status=	objXMLParserRead.WriteXml(LicensingService.LicenseFilePath, true);
//					Log.Write("ParseSMSAndRenewLicense STEP-7", _LogLevel.RENEW_DETAILS);
//					if(Status==true)
//					{
//						Response= "RENEWED";
//						LicenseId=objXMLParserRead.GetValue("LicenseId",LicensingService.CurrentLicenseCount);
//						LicenseExpiry=objXMLParserRead.GetValue("LicenseExpiryDate",LicensingService.CurrentLicenseCount);
//					}
//					else
//					{
//						Response="ERROR_LIC_FILE";
//					}
				} catch (Exception e) {
					Response= "Error occured in parsing server response in Licensing Service.";
					Log.Write(Response+": "+e.toString(),Log._LogLevel.NORAML);
					Validation="ERROR_LIC";
				}
			}
			
			return Response;
		} catch (Exception e) {
			return "PARSE_ERROR";
		}
		finally
		{
			//LicensingService._setRenewStatus(Response);
		}
	}*/
}

