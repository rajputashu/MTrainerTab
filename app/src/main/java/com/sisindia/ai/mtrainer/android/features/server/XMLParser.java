package com.sisindia.ai.mtrainer.android.features.server;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLParser {

    Document docData;
    Context _Context;

    public XMLParser()
    {
       // _Context= MTrainerApplication1.getAppContext();
    }
    public boolean ReadXmlFile(String filePath, boolean isEncrypted)
    {
        try {
            String Data="";
            File fIN = new File(filePath);

            if (fIN.exists())
            {
                StringBuffer fileData = new StringBuffer(1000);
                BufferedReader reader = new BufferedReader(
                        new FileReader(filePath));
                char[] buf = new char[1024];
                int numRead=0;

                while((numRead=reader.read(buf)) != -1){
                    String readData = String.valueOf(buf, 0, numRead);
                    fileData.append(readData);
                    buf = new char[1024];
                }

                reader.close();
                Data= fileData.toString();
                com.sisindia.ai.mtrainer.android.features.server.Log.Write("Data in ReadXmlFile: "+Data, com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.LIC_DATA_DEATILS);
            }
            else
            {
                com.sisindia.ai.mtrainer.android.features.server.Log.Write("file not found",com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                return false;
            }
            if(isEncrypted)
            {
//				EncryptDecrypt objEncrypDecrypt=new EncryptDecrypt();
//				Data=objEncrypDecrypt.DecryptString(Data);

                EncryptDecrypt objEncryptDecrypt=new EncryptDecrypt();
                InputStream in = new FileInputStream(filePath);


                // Copy the bits from instream to outstream	
                File fi=new File(filePath);
                int flength=(int) fi.length();
                byte[] buf = new byte[flength];

                if (in.read(buf) > 0)
                {
                    byte[] bufOut=objEncryptDecrypt.Decrypt(buf);
                    Data=new String(bufOut);
                    //Log.Write(Data, _LogLevel.NORAML);
                }
                else
                {
                    com.sisindia.ai.mtrainer.android.features.server.Log.Write("input file is blank", com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                }
                in.close();
            }
            docData = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            try
            {
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(Data));
                docData = db.parse(is);
            } catch (ParserConfigurationException e) {
                com.sisindia.ai.mtrainer.android.features.server.Log.Write(" XML parse error" + e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                System.out.println("XML parse error: " + e.getMessage());
                return false;
            } catch (SAXException e) {
                com.sisindia.ai.mtrainer.android.features.server.Log.Write(" Wrong XML file structure" + e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                System.out.println("Wrong XML file structure: " + e.getMessage());
                return false;
            } catch (IOException e) {
                com.sisindia.ai.mtrainer.android.features.server.Log.Write(" I/O exeption:" + e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                System.out.println("I/O exeption: " + e.getMessage());
                return false;
            }
            return true;
        } catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error Occured in ReadXmlFile: "+e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
            return false;
        }
    }

    public boolean ReadXmlData(String Data, boolean isEncrypted)
    {
        try {
            if(isEncrypted)
            {
                //DECRYPT DATA
                EncryptDecrypt objEncrypDecrypt=new EncryptDecrypt();
                Data=objEncrypDecrypt.DecryptString(Data);
            }
            docData = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(Data));
                docData = db.parse(is);
            } catch (ParserConfigurationException e) {
                com.sisindia.ai.mtrainer.android.features.server.Log.Write(" XML parse error" + e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                System.out.println("XML parse error: " + e.getMessage());
                return false;
            } catch (SAXException e) {
                com.sisindia.ai.mtrainer.android.features.server.Log.Write(" Wrong XML file structure" + e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                System.out.println("Wrong XML file structure: " + e.getMessage());
                return false;
            } catch (IOException e) {
                com.sisindia.ai.mtrainer.android.features.server.Log.Write(" I/O exeption:" + e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                System.out.println("I/O exeption: " + e.getMessage());
                return false;
            }

            return true;
        } catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error Occured in ReadXmlData: "+e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
            return false;
        }
    }

    public boolean WriteXml(String fileName, boolean isEncrypted)
    {
        try
        {
            //ic_write original temp file
            if(isEncrypted)
            {
                String tempFile=_Context.getFilesDir().getAbsolutePath()+"/tempTrinfin.lic";

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                StreamResult result = new StreamResult(new FileWriter(tempFile));
                DOMSource source = new DOMSource(docData);
                transformer.transform(source, result);


                EncryptDecrypt objEncryptDecrypt=new EncryptDecrypt();
                InputStream in = new FileInputStream(tempFile);
                OutputStream out = new FileOutputStream(fileName);

                // Copy the bits from instream to outstream	
                File fi=new File(tempFile);
                int flength=(int) fi.length();
                byte[] buf = new byte[flength];

                if (in.read(buf) > 0)
                {
                    byte[] bufOut=objEncryptDecrypt.Encrypt(buf);
                    out.write(bufOut, 0, bufOut.length);
                }
                else
                {
                    com.sisindia.ai.mtrainer.android.features.server.Log.Write("input file is blank", com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
                }
                in.close();
                out.close();
            }
            else
            {
                String tempFile=fileName;

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                StreamResult result = new StreamResult(new FileWriter(tempFile));
                DOMSource source = new DOMSource(docData);
                transformer.transform(source, result);
            }
            return true;
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server. Log.Write("Error occured in WriteXml: "+e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
            return false;
        }
    }

    public void SetValue(String ColumnName, String Value)
    {
        try
        {
            docData.getElementsByTagName(ColumnName).item(0).getFirstChild().setNodeValue(Value);
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occured in SetValue: "+ColumnName+" "+e.toString() ,com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
    }

    public String GetValue(String ColumnName)
    {
        String Value="ERROR";
        try
        {
            Value=docData.getElementsByTagName(ColumnName).item(0).getFirstChild().getNodeValue();
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occured in GetValue: "+ColumnName+" "+e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
        return Value;
    }

    public int Select(String ColumnName, String Data)
    {
        int rowCount= docData.getElementsByTagName("dtFileInfo").getLength();
        for (int i = 0; i < rowCount; i++) {
            String Value=docData.getElementsByTagName(ColumnName).item(i).getFirstChild().getNodeValue();
            //Log.Write(Value, _LogLevel.SERVER_DETAILS);
            if(Data.toUpperCase().compareTo(Value.toUpperCase())==0)
            {
                return i;
            }
        }
        return -1;
    }
    public int getRowsLength()
    {
        try
        {
            return docData.getElementsByTagName("dtProjectDetails").getLength();
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occured in getRowsLength"+e.toString() ,com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
        return 0;
    }

    public boolean NewRow()
    {
        try {
            Element baseRoot;
            baseRoot = docData.getDocumentElement();

            Element root = docData.createElement("dtProjectDetails");
            baseRoot.appendChild(root);

            Element child = docData.createElement("ProjectVersion");
            root.appendChild(child);

            Text text = docData.createTextNode("NOT_SET");
            child.appendChild(text);

            child = docData.createElement("Date");
            root.appendChild(child);

            text = docData.createTextNode("NOT_SET");
            child.appendChild(text);

            child = docData.createElement("Status");
            root.appendChild(child);

            text = docData.createTextNode("NOT_SET");
            child.appendChild(text);

            child = docData.createElement("ProjectVersionDescription");
            root.appendChild(child);

            text = docData.createTextNode("NOT_SET");
            child.appendChild(text);

            return true;
        } catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occurred in creating new row", com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
            return false;
        }

    }

    public int getUpgradeRowsLength()
    {
        try
        {
            return docData.getElementsByTagName("dtRevised").getLength();
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.  Log.Write("Error occured in getRowsLength"+e.toString() ,com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
        return 0;
    }

    public boolean NewUpgradeRow()
    {
        try {
            Element baseRoot;
            baseRoot = docData.getDocumentElement();

            Element root = docData.createElement("dtRevised");
            baseRoot.appendChild(root);

            Element child = docData.createElement("FilePath");
            root.appendChild(child);

            Text text = docData.createTextNode("NOT_SET");
            child.appendChild(text);

            return true;
        } catch (Exception e)
        {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occurred in creating new row",com.sisindia.ai.mtrainer.android.features.server.Log. _LogLevel.NORAML);
            return false;
        }
    }

    public void SetValue(String ColumnName, String Value, int index)
    {
        try
        {
            docData.getElementsByTagName(ColumnName).item(index).getFirstChild().setNodeValue(Value);
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occured in SetValue index: "+ColumnName+" "+e.toString() ,com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
    }

    public String GetValue(String ColumnName, int index)
    {
        String Value="ERROR";
        try
        {
            Value=docData.getElementsByTagName(ColumnName).item(index).getFirstChild().getNodeValue();
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occured in GetValue index: "+ColumnName+" "+e.toString(),com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
        return Value;
    }

    public boolean NewLicenseRow()
    {
        try {
            Element baseRoot;
            baseRoot = docData.getDocumentElement();

            Element root = docData.createElement("dtLicense");
            baseRoot.appendChild(root);

            Element child = docData.createElement("LicenseLotId");
            root.appendChild(child);
            Text text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("LicenseId");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("LicenseValidity");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("LicenseStartDate");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("LicenseMaxDate");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("LicenseExpiryDate");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ClientName");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ClientEmailId");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ClientPhone");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ClientKey");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ActivationType");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("TabletId");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("SdCardID");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ServerID");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("Status");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ProjectId");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("ProjectName");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("BufferDays");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            child = docData.createElement("DownloadStatus");
            root.appendChild(child);
            text = docData.createTextNode("NA");
            child.appendChild(text);

            return true;
        } catch (Exception e)
        {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occurred in creating new row", com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
            return false;
        }
    }

    public int getLicenseRowsLength()
    {
        try
        {
            return docData.getElementsByTagName("dtLicense").getLength();
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occured in getLicenseRowsLength"+e.toString() ,com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
        return 0;
    }
    public int getDownloadRowsLength()
    {
        try
        {
            return docData.getElementsByTagName("dtProjectDetails").getLength();
        }
        catch (Exception e) {
            com.sisindia.ai.mtrainer.android.features.server.Log.Write("Error occured in getDownloadRowsLength"+e.toString() ,com.sisindia.ai.mtrainer.android.features.server.Log._LogLevel.NORAML);
        }
        return 0;
    }
}
